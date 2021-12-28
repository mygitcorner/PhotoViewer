package gf.photoviewer.dialog;

import static gf.photoviewer.PhotoViewerConstants.INSET;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import gf.photoviewer.GBC;
import gf.photoviewer.PVLogger;
import gf.photoviewer.model.TagModel;
import gf.photoviewer.renderer.TagRenderer;
import gf.photoviewer.resources.Picture;
import gf.photoviewer.resources.Tag;

//TODO check for duplicates and remove empty tags 

public class TagPicturesDialog extends JDialog {
	private JList<Tag> tagBox;
	private JList<Tag> existingTagBox;
	
	private TagModel tagModel;
	private DefaultListModel<Tag> tagsToAddModel;
	
	public static List<Tag> showDialog(JFrame owner,
			TagModel tagModel,
			List<Picture> pictures) throws Exception {
		TagPicturesDialog dialog = new TagPicturesDialog(owner, tagModel, pictures);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		return dialog.getResult();
	}
	
	private TagPicturesDialog(JFrame owner,
			TagModel tagModel,
			List<Picture> pictures) throws Exception {
		super(owner, "Tag pictures", true);
		setSize(400, 400);
		setLayout(new GridBagLayout());
		
		this.tagModel = tagModel;
		
		JPanel panel = new JPanel(new GridBagLayout());
		
		tagsToAddModel = new DefaultListModel<>();
		tagBox = new JList<>(tagsToAddModel);
		tagBox.setCellRenderer(new TagRenderer());
		tagBox.setVisibleRowCount(-1);
		tagBox.setLayoutOrientation(JList.VERTICAL_WRAP);
		tagBox.getSelectionModel().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		existingTagBox = new JList<>(tagModel);
		existingTagBox.setCellRenderer(new TagRenderer());
		existingTagBox.setVisibleRowCount(-1);
		existingTagBox.setLayoutOrientation(JList.VERTICAL_WRAP);
		existingTagBox.getSelectionModel().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(event -> setVisible(false));
		
		panel.add(new JLabel("Tags: "), new GBC(0, 0).
				setWeight(100, 0).
				setFill(GBC.HORIZONTAL).
				setInsets(0, 0, INSET, 0));
		panel.add(new JScrollPane(tagBox), new GBC(0, 1, 2, 1).
				setWeight(100, 100).
				setFill(GBC.BOTH).
				setInsets(INSET, 0, INSET, 0));
		panel.add(new JButton(new AddTagsAction()), new GBC(0, 2).
				setWeight(100, 0).
				setFill(GBC.HORIZONTAL).
				setInsets(INSET, 0, 0, INSET));
		panel.add(new JButton(new RemoveTagsAction()), new GBC(1, 2).
				setWeight(100, 0).
				setFill(GBC.HORIZONTAL).
				setInsets(INSET, INSET, 0, 0));
		panel.add(new JLabel("Existing tags: "), new GBC(0, 3, 2, 1).
				setWeight(100, 0).
				setFill(GBC.HORIZONTAL).
				setInsets(0, 0, INSET, 0));
		panel.add(new JScrollPane(existingTagBox), new GBC(0, 4, 2, 1).
				setWeight(100, 100).
				setFill(GBC.BOTH).
				setInsets(INSET, 0, INSET, 0));
		panel.add(new JButton(new NewTagAction()), new GBC(0, 5, 2, 1).
				setWeight(100, 0).
				setFill(GBC.HORIZONTAL).
				setInsets(INSET, 0, 0, 0));
		panel.add(okButton, new GBC(0, 6, 2, 1).
				setWeight(100, 0).
				setFill(GBC.HORIZONTAL).
				setInsets(INSET, 0, 0, 0));
		
		add(panel, new GBC(0, 0).
				setWeight(100, 100).
				setFill(GBC.BOTH).
				setInsets(2 * INSET));
	}
	
	public List<Tag> getResult() {
		return Collections.list(tagsToAddModel.elements());
	}

	private class NewTagAction extends AbstractAction {
		public NewTagAction() {
			super("New");
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			String tagName = (String) JOptionPane.showInputDialog(
					TagPicturesDialog.this,
					"Enter tag name",
					"New Tag",
					JOptionPane.PLAIN_MESSAGE,
					null,
					null,
					"New Tag");
			
			if (tagName == null)
				return;
			
			try {
				Tag newTag = tagModel.add(new Tag(tagName));
				tagsToAddModel.addElement(newTag);
			} catch (Exception e) {
				PVLogger.getLogger().log(Level.SEVERE, "Failed to create new tag", e);
				JOptionPane.showMessageDialog(TagPicturesDialog.this,
						"An error occurred while adding the tag. Try again.",
						"Tag error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private class AddTagsAction extends AbstractAction {
		public AddTagsAction() {
			super("Tag");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			List<Tag> tags = existingTagBox.getSelectedValuesList();
			
			if (tags.isEmpty())
				return;
			
			tagsToAddModel.addAll(tags);
			existingTagBox.clearSelection();
		}	
	}
	
	private class RemoveTagsAction extends AbstractAction {
		public RemoveTagsAction() {
			super("Untag");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			List<Tag> tags = tagBox.getSelectedValuesList();

			if (tags.isEmpty())
				return;

			tags.forEach(tagsToAddModel::removeElement);
			tagBox.clearSelection();
		}
	}
}