package gf.photoviewer.gui;

import static gf.photoviewer.PhotoViewerConstants.INSET;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import gf.photoviewer.GBC;
import gf.photoviewer.PVLogger;
import gf.photoviewer.model.AlbumModel;
import gf.photoviewer.model.PictureModel;
import gf.photoviewer.renderer.AlbumRenderer;
import gf.photoviewer.resources.Album;


public class AlbumPanel extends JPanel {
	private AlbumModel albumModel;
	private PictureModel pictureModel;
	private JList<Album> albumList;

	private JButton newAlbumButton;
	private ListPopupMenu popupMenu;
	
	private NewAlbumAction addAlbumAction;
	private RemoveAlbumsAction removeAlbumAction;
	private RenameAlbumAction renameAlbumAction;

	public AlbumPanel(AlbumModel albumModel, PictureModel pictureModel) {
		this.albumModel = albumModel;
		this.pictureModel = pictureModel;
		
		addAlbumAction = new NewAlbumAction();
		removeAlbumAction = new RemoveAlbumsAction();
		renameAlbumAction = new RenameAlbumAction();
		
		albumList = new JList<>(albumModel);
		albumList.setCellRenderer(new AlbumRenderer());
		albumList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		popupMenu = new ListPopupMenu(albumList);
		popupMenu.add(new JMenuItem(removeAlbumAction));
		popupMenu.add(new JMenuItem(renameAlbumAction));
		albumList.setComponentPopupMenu(popupMenu);
		
		newAlbumButton = new JButton(addAlbumAction);
		
		setBackground(Color.WHITE);
		setLayout(new GridBagLayout());
		add(new EmptyBorderScrollPane(albumList), new GBC(0, 0).
				setWeight(100, 100).
				setFill(GBC.BOTH).
				setInsets(0, 0, INSET, 0));
		add(newAlbumButton, new GBC(0, 1).
				setWeight(100, 0).
				setFill(GBC.HORIZONTAL).
				setInsets(INSET, 0, 0, 0));
	}

	public ListSelectionModel getSelectionModel() {
		return albumList.getSelectionModel();
	}
	
	private Album getSelectedAlbum() {
		if (!getSelectionModel().isSelectionEmpty())
			return albumModel.getElementAt(getSelectionModel().getSelectedIndices()[0]);
		else
			return null;
	}
	
	public NewAlbumAction getAddAlbumAction() {
		return addAlbumAction;
	}

	public class NewAlbumAction extends AbstractAction {

		public NewAlbumAction() {
			super("New Album");
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			String albumName = "";
			albumName = (String) JOptionPane.showInputDialog(AlbumPanel.this,
					"Enter album name: ",
					"New Album",
					JOptionPane.PLAIN_MESSAGE,
					null,
					null,
					"New Album");
			
			if (albumName == null)
				return;
			
			try {
				albumModel.add(new Album(albumName));
			} catch (Exception e) {
				PVLogger.getLogger().log(Level.SEVERE, "Failed to add album", e);
				JOptionPane.showMessageDialog(AlbumPanel.this,
						"An error occurred while adding the album. Try again.",
						"Album error",
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	public class RemoveAlbumsAction extends AbstractAction {
		public RemoveAlbumsAction() {
			super("Remove");
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			Album selectedAlbum = getSelectedAlbum();
			
			if (selectedAlbum == null)
				return;
			
			int result = JOptionPane.showConfirmDialog(AlbumPanel.this,
					"Are you sure that you want to remove album '"
					+ selectedAlbum.getLabel() + "'?",
					"Removing album",
					JOptionPane.YES_NO_OPTION);
			
			if (result != JOptionPane.YES_OPTION)
				return;
			
			try {
				albumModel.remove(selectedAlbum);
				pictureModel.removePicturesFromAlbum(selectedAlbum);
			} catch (Exception e) {
				PVLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
				JOptionPane.showMessageDialog(AlbumPanel.this,
						"An error occurred while removing the album. Try again.",
						"Album error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public class RenameAlbumAction extends AbstractAction {
		public RenameAlbumAction() {
			super("Rename");
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			Album selectedAlbum = getSelectedAlbum();
			
			if (selectedAlbum == null)
				return;
			
			String newName = (String) JOptionPane.showInputDialog(AlbumPanel.this,
					"Enter new album name: ",
					"Rename album",
					JOptionPane.PLAIN_MESSAGE,
					null,
					null,
					selectedAlbum.getLabel());
			
			if (newName == null)
				return;
			
			try {
				albumModel.rename(selectedAlbum, newName);
			} catch (Exception e) {
				PVLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
				JOptionPane.showMessageDialog(AlbumPanel.this,
						"An error occurred while renaming the album. Try again.",
						"Album error",
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}
}
