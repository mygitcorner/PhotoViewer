package gf.photoviewer.gui;

import static gf.photoviewer.PhotoViewerConstants.INSET;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import gf.photoviewer.GBC;
import gf.photoviewer.PVLogger;
import gf.photoviewer.dialog.ShowTagsDialog;
import gf.photoviewer.dialog.TagPicturesDialog;
import gf.photoviewer.event.PDCEvent;
import gf.photoviewer.event.PVHandler;
import gf.photoviewer.image.ThumbnailCache;
import gf.photoviewer.model.AlbumModel;
import gf.photoviewer.model.PictureModel;
import gf.photoviewer.model.TagModel;
import gf.photoviewer.resources.Album;
import gf.photoviewer.resources.Picture;
import gf.photoviewer.resources.Tag;

/**
 * A picturePanel encapsulates all the functionality necessary
 * to display and manipulate pictures. Pictures can be added
 * to an album, be tagged and removed from albums/tags.
 * The currently displayed pictures are determined by the 
 * selected album or tag. This class uses the MVC pattern to
 * interact with its data. Provide list models and selection models
 * to control the displayed pictures and selected albums/tags.
 */
public class PicturePanel extends JPanel {
	private PictureModel pictureModel;
	private AlbumModel albumModel;
	private TagModel tagModel;
	private ListSelectionModel albumSelectionModel;
	private ListSelectionModel tagSelectionModel;

	private PictureButtonPanel pictureControlPanel;
	private ThumbnailList pictureList;
	private PictureStatusField pictureStatusField;
	
	private ListPopupMenu popupMenu;
	
	private AbstractAction addPicturesAction;
	private AbstractAction removePicturesAction;
	private TagPicturesAction tagPicturesAction;
	private ShowTagsAction showTagsAction;
	private AbstractAction viewPictureAction;
	
	private PVHandler<PDCEvent> pdcHandler;
	private JFrame owner;
	
	/**
	 * Constructs a {@code PicturePanel} with the specified owner.
	 * 
	 * @param owner the {@code JFrame} that encloses this object.
	 * It is used to properly display dialogs.
	 */
	public PicturePanel(JFrame owner,
			PictureModel pictureModel,
			AlbumModel albumModel,
			TagModel tagModel) {
		this.owner = owner;
		this.pictureModel = pictureModel;
		this.albumModel = albumModel;
		this.tagModel = tagModel;
		
		pictureList = new ThumbnailList(pictureModel);
		pictureControlPanel = new PictureButtonPanel(pictureList, pictureModel, albumModel);
		pictureStatusField = new PictureStatusField(pictureModel);
		
		addPicturesAction = pictureControlPanel.getAddPicturesAction();
		removePicturesAction = pictureControlPanel.getRemovePicturesAction();
		tagPicturesAction = new TagPicturesAction();
		showTagsAction = new ShowTagsAction();
		viewPictureAction = pictureList.getViewPictureAction();
		
		popupMenu = new ListPopupMenu(pictureList);
		popupMenu.add(new JMenuItem(removePicturesAction));
		popupMenu.addSeparator();
		popupMenu.add(new JMenuItem(tagPicturesAction));
		popupMenu.add(new JMenuItem(showTagsAction));
		popupMenu.addSeparator();
		popupMenu.add(new JMenuItem(viewPictureAction));
		pictureList.setComponentPopupMenu(popupMenu);	
		
		pdcHandler = new PVHandler<>();
		pictureList.addPDCListener(event -> pdcHandler.invoke(event));
		
		setLayout(new GridBagLayout());
		add(pictureControlPanel, new GBC(0, 0)
				.setWeight(100, 0)
				.setFill(GBC.HORIZONTAL)
				.setInsets(0, 0, INSET, 0));
		add(new EmptyBorderScrollPane(pictureList), new GBC(0, 1)
				.setWeight(100, 100)
				.setFill(GBC.BOTH)
				.setInsets(INSET, 0, INSET, 0));
		add(pictureStatusField, new GBC(0, 2)
				.setWeight(100, 0)
				.setFill(GBC.HORIZONTAL)
				.setInsets(INSET, 0, 0, 0));
	}
	
	/**
	 * Sets the album selection model. If the selection models'
	 * selection changes the view is notified. Pictures of the
	 * selected album will be displayed.
	 * 
	 * @param albumSelectionModel the album selection model to be set
	 */
	public void setAlbumSelectionModel(ListSelectionModel albumSelectionModel) {
		this.albumSelectionModel = albumSelectionModel;
		pictureControlPanel.setAlbumSelectionModel(albumSelectionModel);
		albumSelectionModel.addListSelectionListener(event -> showPicturesOfSelectedAlbum());
	}
	
	/**
	 * Sets the tag selection model. If the selection models'
	 * selection changes the view is notified. Pictures of the
	 * selected tag will be displayed.
	 * 
	 * @param tagSelectionModel the tag selection model to be set
	 */
	public void setTagSelectionModel(ListSelectionModel tagSelectionModel) {
		this.tagSelectionModel = tagSelectionModel;
		tagSelectionModel.addListSelectionListener(event -> showPicturesOfSelectedTag());
	}
	
	public AbstractAction getAddPicturesAction() {
		return addPicturesAction;
	}
	
	public ListSelectionModel getPictureSelectionModel() {
		return pictureList.getSelectionModel();
	}
	
	public void addPDCListener(Consumer<PDCEvent> c) {
		pdcHandler.addListener(c);
	}
	
	/**
	 * Sets the current album of the picture model to
	 * the selected album. 
	 */
	public void showPicturesOfSelectedAlbum() {
		try {
			Album selectedAlbum = getSelectedAlbum();
			
			if (selectedAlbum != null) {
				pictureModel.setCurrentAlbum(selectedAlbum);
				pictureList.clearSelection();
				pictureList.ensureIndexIsVisible(0);
				addPicturesAction.setEnabled(true);
				removePicturesAction.setEnabled(true);
			} else {
				pictureModel.setCurrentAlbum(null);
				addPicturesAction.setEnabled(false);
				removePicturesAction.setEnabled(false);
			}
		} catch (Exception e) {
			PVLogger.getLogger().log(Level.SEVERE, "Failed to load pictures of album", e);
			JOptionPane.showMessageDialog(PicturePanel.this,
					"An error occurred while loading the pictures. Try again.",
					"Picture error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Sets the current tag of the picture model to
	 * the selected tag. 
	 */
	public void showPicturesOfSelectedTag() {
		try {
			List<Tag> selectedTags = getSelectedTags();

			if (selectedTags != null) {
				pictureModel.setCurrentTags(selectedTags);
				pictureList.clearSelection();
				pictureList.ensureIndexIsVisible(0);
				removePicturesAction.setEnabled(true);
			} else {
				pictureModel.setCurrentAlbum(null);
				removePicturesAction.setEnabled(false);
			}
			
			addPicturesAction.setEnabled(false);
		} catch (Exception e) {
			PVLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
			JOptionPane.showMessageDialog(PicturePanel.this,
					"An error occurred while loading pictures. Try again.",
					"Picture error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * 
	 * @return the currently selected picture. Returns null if the selection is empty
	 */
	private Picture getSelectedPicture() {
		ListSelectionModel model = pictureList.getSelectionModel();
		if (!model.isSelectionEmpty())
			return pictureModel.getElementAt(model.getAnchorSelectionIndex());
		else
			return null;
	}
	
	/**
	 * 
	 * @return the currently selected album. Returns null if the selection is empty
	 */
	private Album getSelectedAlbum() {
		if (!albumSelectionModel.isSelectionEmpty())
			return albumModel.getElementAt(albumSelectionModel.getSelectedIndices()[0]);
		else
			return null;
	}
	
	/**
	 * 
	 * @return the currently selected tag. Returns null if the selection is empty
	 */
	private List<Tag> getSelectedTags() {
		if (!tagSelectionModel.isSelectionEmpty()) {
			List<Tag> selectedTags = IntStream.of(tagSelectionModel.getSelectedIndices())
					.mapToObj(index -> tagModel.getElementAt(index))
					.collect(Collectors.toList());
			return selectedTags;
		} else {
			return null;
		}
	}

	/**
	 * This action pops up a dialog and allows the user to select
	 * tags that were added to the tagModel. The selected pictures
	 * are tagged with the selected tags.
	 */
	private class TagPicturesAction extends AbstractAction {
		public TagPicturesAction() {
			super("Add tags");
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			List<Picture> picturesToTag = pictureList.getSelectedValuesList();
			
			if (picturesToTag.isEmpty())
				return;
			
			try {
				List<Tag> selectedTags = TagPicturesDialog.showDialog(owner, tagModel, picturesToTag);
				
				if (selectedTags == null)
					return;

				try {
					for (Tag selectedTag : selectedTags)
						pictureModel.tagPictures(picturesToTag, selectedTag);
				} catch (Exception e) {
					PVLogger.getLogger().log(Level.SEVERE, "Failed to tag pictures", e);
					JOptionPane.showMessageDialog(PicturePanel.this,
							"An error occurred while tagging some of the pictures. Try again.",
							"Tag error",
							JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception e) {
				PVLogger.getLogger().log(Level.SEVERE, "Failed to display Tag dialog", e);
				JOptionPane.showMessageDialog(PicturePanel.this,
						"An error occurred while displaying the dialog. Try again.",
						"Tag error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * This action pops up a dialog that shows all the tags
	 * associated with the selected picture.
	 */
	private class ShowTagsAction extends AbstractAction {
		
		public ShowTagsAction() {
			super("Show tags");
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			Picture selectedPicture = getSelectedPicture();
			
			if (selectedPicture == null)
				return;
			
			try {
				ShowTagsDialog.showDialog(owner, tagModel, pictureModel, selectedPicture);
			} catch (Exception e) {
				PVLogger.getLogger().log(Level.SEVERE, "Failed to display Show-Tags dialog", e);
				JOptionPane.showMessageDialog(PicturePanel.this,
						"An error occurred while displaying the Show-Tags dialog. Try again.",
						"Tag error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
}
