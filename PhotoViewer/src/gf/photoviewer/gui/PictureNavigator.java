package gf.photoviewer.gui;

import static gf.photoviewer.PhotoViewerConstants.INSET;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import gf.photoviewer.event.PDCEvent;
import gf.photoviewer.event.PVHandler;
import gf.photoviewer.model.AlbumModel;
import gf.photoviewer.model.PictureModel;
import gf.photoviewer.model.TagModel;

public class PictureNavigator extends JPanel {
	private static final int MIN_SIDE_PANEL_WIDTH = 170;
	
	private AlbumPanel albumPanel;
	private AlbumModel albumModel;
	
	private TagPanel tagPanel;
	private TagModel tagModel;
	
	private PicturePanel picturePanel;
	private PictureModel pictureModel;
	
	private PVHandler<PDCEvent> pdcHandler;
	
	public PictureNavigator(JFrame owner) throws Exception {
		setLayout(new BorderLayout());
		
		JTabbedPane sideTabbedPane = new JTabbedPane();
		sideTabbedPane.setPreferredSize(new Dimension(MIN_SIDE_PANEL_WIDTH, 0));

		pictureModel = new PictureModel();
		albumModel = new AlbumModel();
		tagModel = new TagModel();

		albumPanel = new AlbumPanel(albumModel, pictureModel);
		sideTabbedPane.addTab("Albums", albumPanel);
		
		tagPanel = new TagPanel(tagModel, pictureModel);
		sideTabbedPane.addTab("Tags", tagPanel);
		
		sideTabbedPane.setBorder(new EmptyBorder(2 * INSET, 2 * INSET, 2 * INSET, 2 * INSET));
		
		picturePanel = new PicturePanel(owner, pictureModel, albumModel, tagModel);
		picturePanel.setAlbumSelectionModel(albumPanel.getSelectionModel());
		picturePanel.setTagSelectionModel(tagPanel.getSelectionModel());
		picturePanel.setBorder(new EmptyBorder(2 * INSET, 2 * INSET, 2 * INSET, 2 * INSET));
		
		sideTabbedPane.addChangeListener(event -> {
			int index = sideTabbedPane.getSelectedIndex();
			
			switch (index) {
				case 0:
					picturePanel.showPicturesOfSelectedAlbum();
					break;
				case 1:
					picturePanel.showPicturesOfSelectedTag();
					break;
			}
		});
		
		pdcHandler = new PVHandler<>();

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				sideTabbedPane, picturePanel);
		splitPane.setContinuousLayout(true);
		add(splitPane);
		
		picturePanel.addPDCListener(event -> pdcHandler.invoke(event));
	}
	
	public void addPDCListener(Consumer<PDCEvent> c) {
		pdcHandler.addListener(c);
	}

	public PictureModel getPictureModel() {
		return pictureModel;
	}
	
	public ListSelectionModel getPictureSelectionModel() {
		return picturePanel.getPictureSelectionModel();
	}
	
	public AbstractAction getAddPicturesAction() {
		return picturePanel.getAddPicturesAction();
	}
	
	public AbstractAction getNewAlbumAction() {
		return albumPanel.getAddAlbumAction();
	}
}
