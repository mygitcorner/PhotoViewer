package gf.photoviewer.gui;

import static gf.photoviewer.PhotoViewerConstants.INSET;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import gf.photoviewer.GBC;
import gf.photoviewer.PVLogger;
import gf.photoviewer.model.PictureModel;
import gf.photoviewer.model.TagModel;
import gf.photoviewer.renderer.TagRenderer;
import gf.photoviewer.resources.Tag;

public class TagPanel extends JPanel {
	private JList<Tag> tagList;
	private TagModel tagModel;
	private PictureModel pictureModel;
	
	private ListPopupMenu popupMenu;
	
	public TagPanel(TagModel tagModel, PictureModel pictureModel) {
		this.tagModel = tagModel;
		this.pictureModel = pictureModel;
		
		tagList = new JList<>(tagModel);
		tagList.setCellRenderer(new TagRenderer());
		tagList.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		popupMenu = new ListPopupMenu(tagList);
		popupMenu.add(new JMenuItem(new RemoveTagsAction()));
		popupMenu.add(new JMenuItem(new RenameTagAction()));
		tagList.setComponentPopupMenu(popupMenu);
		
		setLayout(new GridBagLayout());
		add(new EmptyBorderScrollPane(tagList), new GBC(0, 0).
				setWeight(100, 100).
				setFill(GBC.BOTH).
				setInsets(0, 0, INSET, 0));
	}
	
	public ListSelectionModel getSelectionModel() {
		return tagList.getSelectionModel();
	}
	
	private Tag getSelectedTag() {
		if (!tagList.getSelectionModel().isSelectionEmpty())
			return tagModel.getElementAt(tagList.getSelectionModel().getAnchorSelectionIndex());
		else
			return null;
	}
	
	public class RemoveTagsAction extends AbstractAction {
		public RemoveTagsAction() {
			super("Remove");
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			Tag selectedTag = getSelectedTag();
			
			if (selectedTag == null)
				return;
			
			int result = JOptionPane.showConfirmDialog(TagPanel.this,
					"Are you sure that you want to remove tag '"
					+ selectedTag.getLabel() + "'?",
					"Removing tag",
					JOptionPane.YES_NO_OPTION);
			
			if (result != JOptionPane.YES_OPTION)
				return;
			
			try {
				tagModel.remove(selectedTag);
				pictureModel.removePicturesFromTag(selectedTag);
			} catch (Exception e) {
				PVLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
				JOptionPane.showMessageDialog(TagPanel.this,
						"An error occurred while removing the tag. Try again.",
						"Tag error",
						JOptionPane.ERROR_MESSAGE);
			}
		}	
	}
	
	public class RenameTagAction extends AbstractAction {
		public RenameTagAction() {
			super("Rename");
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			Tag selectedTag = getSelectedTag();
			
			String newName = (String) JOptionPane.showInputDialog(TagPanel.this,
					"Enter new album name: ",
					"Rename Tag",
					JOptionPane.PLAIN_MESSAGE,
					null,
					null,
					selectedTag.getLabel());
			
			if (newName == null)
				return;
			
			try {
				tagModel.rename(selectedTag, newName);
			} catch (Exception e) {
				PVLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
				JOptionPane.showMessageDialog(TagPanel.this,
						"An error occurred while renaming the tag. Try again.",
						"Tag error",
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}
	
}
