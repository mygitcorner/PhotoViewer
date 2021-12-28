package gf.photoviewer.gui;

import static gf.photoviewer.PhotoViewerConstants.INSET;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import gf.photoviewer.GBC;
import gf.photoviewer.PVLogger;
import gf.photoviewer.image.ImageFileFilter;
import gf.photoviewer.image.ThumbnailCache;
import gf.photoviewer.model.AlbumModel;
import gf.photoviewer.model.PictureModel;
import gf.photoviewer.resources.Album;
import gf.photoviewer.resources.Picture;
import gf.photoviewer.resources.Tag;

public class PictureButtonPanel extends JPanel {
	private JList<Picture> pictureList;
	private PictureModel pictureModel;
	private AlbumModel albumModel;
	private ListSelectionModel albumSelectionModel;
	
	private JButton addPictureButton;
	private JButton removePicturesButton;
	
	private AbstractAction addPicturesAction;
	private AbstractAction removePicturesAction;
	
	private JFileChooser fileDialog;
	
	public PictureButtonPanel(JList<Picture> pictureList, PictureModel pictureModel,
			AlbumModel albumModel) {
		this.pictureList = pictureList;
		this.pictureModel = pictureModel;
		this.albumModel = albumModel;
		
		addPicturesAction = new AddPicturesAction();
		addPicturesAction.setEnabled(false);
		removePicturesAction = new RemovePicturesAction();
		removePicturesAction.setEnabled(false);
		
		addPictureButton = new JButton(addPicturesAction);
		removePicturesButton = new JButton(removePicturesAction);
		removePicturesButton.setEnabled(false);
		
		fileDialog = new JFileChooser();
		fileDialog.setFileFilter(new ImageFileFilter());
		fileDialog.setCurrentDirectory(new File("C:\\Users\\Papa\\Pictures"));
		fileDialog.setMultiSelectionEnabled(true);
		
		setLayout(new GridBagLayout());
		add(addPictureButton, new GBC(0, 0)
				.setWeight(0, 0)
				.setInsets(0, 0, 0, INSET));
		add(removePicturesButton, new GBC(1, 0)
				.setWeight(0, 0)
				.setInsets(0, INSET, 0, 0));
		add(new JPanel(), new GBC(2, 0)
				.setWeight(100, 0)
				.setFill(GBC.HORIZONTAL));
	}

	public void setAlbumSelectionModel(ListSelectionModel albumSelectionModel) {
		this.albumSelectionModel = albumSelectionModel;
	}

	public AbstractAction getAddPicturesAction() {
		return addPicturesAction;
	}

	public AbstractAction getRemovePicturesAction() {
		return removePicturesAction;
	}
	
	private Album getSelectedAlbum() {
		if (!albumSelectionModel.isSelectionEmpty())
			return albumModel.getElementAt(albumSelectionModel.getSelectedIndices()[0]);
		else
			return null;
	}

	/**
	 * This action pops up a file chooser and asks the user to selected
	 * zero or more images. If none are selected, the action does
	 * nothing. Otherwise {@code Picture} Objects are constructed
	 * (using the filename as label) and are added to the picture model.
	 */
	private class AddPicturesAction extends AbstractAction {
		public AddPicturesAction() {
			super("Add pictures");
		}

		public void actionPerformed(ActionEvent event) {
			int result = fileDialog.showOpenDialog(PictureButtonPanel.this);

			if (result == JFileChooser.APPROVE_OPTION) {
				try {		
					List<Picture> picturesToAdd = Stream.of(fileDialog.getSelectedFiles())
							.map(f -> new Picture(f.toPath(), f.getName()))
							.collect(Collectors.toList());
					
					pictureModel.addPicturesToAlbum(picturesToAdd, getSelectedAlbum());
				} catch (Exception e) {
					PVLogger.getLogger().log(Level.SEVERE, "Failed to add pictures", e);
					JOptionPane.showMessageDialog(pictureList,
							"An error occurred while adding some of the picture. Try again.",
							"Picture error", 
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	/**
	 * This action removes the selected pictures. If no pictures are
	 * selected the action does nothing.
	 */
	private class RemovePicturesAction extends AbstractAction {
		public RemovePicturesAction() {
			super("Remove pictures");
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			List<Picture> picturesToRemove = pictureList.getSelectedValuesList();
			
			if (picturesToRemove.isEmpty()) {
				return;
			}
			
			try {
				if (pictureModel.getCurrentResource().currentIsAlbum()) {
					if (JOptionPane.showConfirmDialog(pictureList,
							"Are you sure you want to remove these pictures?",
							"Removing pictures",
							JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
						return;
					}
					
					pictureModel.removePictures(picturesToRemove);
				} else if (pictureModel.getCurrentResource().currentIsTags()) {
					if (JOptionPane.showConfirmDialog(pictureList,
							"Are you sure you want to remove these pictures from the selected tags?",
							"Removing pictures",
							JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
						return;
					}
					
					List<Tag> tags = pictureModel.getCurrentResource().getCurrentTags();
					for (Picture picture : picturesToRemove)
						pictureModel.removeTagsFromPicture(tags, picture);
				}
			} catch (Exception e) {
				PVLogger.getLogger().log(Level.SEVERE, "Failed to remove pictures", e);
				JOptionPane.showMessageDialog(pictureList,
						"An error occurred while removing some of the pictures. Try again.",
						"Picture error",
						JOptionPane.ERROR_MESSAGE);
			}
			
			pictureList.clearSelection();
		}
	}
}
