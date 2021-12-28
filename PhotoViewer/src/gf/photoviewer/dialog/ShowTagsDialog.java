package gf.photoviewer.dialog;

import static gf.photoviewer.PhotoViewerConstants.INSET;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import gf.photoviewer.GBC;
import gf.photoviewer.PVLogger;
import gf.photoviewer.model.PictureModel;
import gf.photoviewer.model.TagModel;
import gf.photoviewer.renderer.TagRenderer;
import gf.photoviewer.resources.Picture;
import gf.photoviewer.resources.Tag;

public class ShowTagsDialog extends JDialog {
	private TagModel tagModel;
	private PictureModel pictureModel;
	private Picture picture;
	
	private DefaultListModel<Tag> tagBoxModel;
	private JList<Tag> tagBox;
	
	private JButton okButton;
	private JButton removeButton;

	public static void showDialog(JFrame owner,
			TagModel tagModel,
			PictureModel pictureModel,
			Picture picture) throws Exception {
		ShowTagsDialog dialog = new ShowTagsDialog(owner, tagModel, pictureModel, picture);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
	}
	
	private ShowTagsDialog(JFrame owner,
			TagModel tagModel,
			PictureModel pictureModel,
			Picture picture) throws Exception {
		super(owner, "Tags of " + picture.getLabel(), true);
		setSize(300, 300);
		setLayout(new GridBagLayout());
		
		this.tagModel = tagModel;
		this.pictureModel = pictureModel;
		this.picture = picture;
		
		JPanel panel = new JPanel(new GridBagLayout());
		
		tagBoxModel = new DefaultListModel<>(); 
		tagBoxModel.addAll(tagModel.getTagsFromPicture(picture));
		
		tagBox = new JList<>(tagBoxModel);
		tagBox.setCellRenderer(new TagRenderer());
		tagBox.getSelectionModel().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		okButton = new JButton("Ok");
		okButton.addActionListener(event -> setVisible(false));
		
		removeButton = new JButton(new RemoveTagsAction());
		
		panel.add(new JScrollPane(tagBox), new GBC(0, 0, 2, 1).
				setWeight(100, 100).
				setFill(GBC.BOTH).
				setInsets(INSET, 0, INSET, 0));
		panel.add(okButton, new GBC(0, 1).
				setWeight(100, 0).
				setFill(GBC.HORIZONTAL).
				setInsets(INSET, 0, INSET, 0));
		panel.add(removeButton, new GBC(1, 1).
				setWeight(100, 0).
				setFill(GBC.HORIZONTAL).
				setInsets(INSET, INSET, 0, 0));
		
		add(panel, new GBC(0, 0).
				setWeight(100, 100).
				setFill(GBC.BOTH).
				setInsets(2 * INSET));
	}
	
	private class RemoveTagsAction extends AbstractAction {
		public RemoveTagsAction() {
			super("Remove");
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			List<Tag> tagsToRemove = tagBox.getSelectedValuesList();
			
			if (tagsToRemove.isEmpty())
				return;
			
			try {
				pictureModel.removeTagsFromPicture(tagsToRemove, picture);
				tagsToRemove.forEach(tagBoxModel::removeElement);
			} catch (Exception e) {
				PVLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
				JOptionPane.showMessageDialog(ShowTagsDialog.this,
						"An error occurred while removing the tags. Try again.", 
						"Tag error",
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}
}
