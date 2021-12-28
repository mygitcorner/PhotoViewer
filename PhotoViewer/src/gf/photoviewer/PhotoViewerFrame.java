package gf.photoviewer;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import gf.photoviewer.gui.PictureNavigator;
import gf.photoviewer.gui.PicturePanel;
import gf.photoviewer.gui.PictureViewer;
import gf.photoviewer.model.PictureModel;

//TODO select multiple tags

public class PhotoViewerFrame extends JFrame {
	private static final double SCREEN_SIZE_RATIO = 0.7;
	private static final String PICTURE_NAVIGATOR = "nav";
	private static final String PICTURE_VIEWER = "viewer";
	
	private JPanel mainPanel;
	private PictureNavigator pictureNavigator;
	private PictureViewer pictureViewer;
	
	private JMenuBar menuBar;
	private JMenu newMenu;
	private JMenuItem newAlbumItem;
	private JMenuItem addPicturesItem;
	private JMenu optionsMenu;
	private JMenu tagSelectionMenu;
	private JRadioButtonMenuItem andItem;
	private JRadioButtonMenuItem orItem;
	
	public PhotoViewerFrame() {
		setTitle("PhotoViewer");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationByPlatform(true);
		
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int frameWidth = (int)(screenSize.width * SCREEN_SIZE_RATIO);
		int frameHeight = (int)(screenSize.height * SCREEN_SIZE_RATIO);
		setSize(frameWidth, frameHeight);
		
		try {
			pictureNavigator = new PictureNavigator(this);
		} catch (Exception e) {
			PVLogger.getLogger().log(Level.SEVERE, "Fatal error", e);
			JOptionPane.showMessageDialog(this,
					"A fatal error occurred. The application will exit.", "Fatal error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		pictureViewer = new PictureViewer(pictureNavigator.getPictureModel(),
				pictureNavigator.getPictureSelectionModel());
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new CardLayout());
		mainPanel.add(pictureNavigator, PICTURE_NAVIGATOR);
		mainPanel.add(pictureViewer, PICTURE_VIEWER);
		
		pictureNavigator.addPDCListener(event -> {
			CardLayout c1 = (CardLayout) mainPanel.getLayout();
			c1.show(mainPanel, PICTURE_VIEWER);
			pictureViewer.requestFocus();
		});
		
		pictureViewer.addGoBackListener(event -> {
			CardLayout c1 = (CardLayout) mainPanel.getLayout();
			c1.show(mainPanel, PICTURE_NAVIGATOR);
			pictureNavigator.requestFocus();
		});
		
		add(mainPanel);
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		newMenu = new JMenu("New");
		newAlbumItem = new JMenuItem(pictureNavigator.getNewAlbumAction());
		addPicturesItem = new JMenuItem(pictureNavigator.getAddPicturesAction());

		newMenu.add(newAlbumItem);
		newMenu.add(addPicturesItem);
		menuBar.add(newMenu);
		
		optionsMenu = new JMenu("Options");
		tagSelectionMenu = new JMenu("Tag selection type");
		
		andItem = new JRadioButtonMenuItem("And");
		andItem.addActionListener(event -> pictureNavigator.getPictureModel()
				.setTagSelectionType(PictureModel.TagSelectionType.AND));
		orItem = new JRadioButtonMenuItem("Or");
		orItem.addActionListener(event -> pictureNavigator.getPictureModel()
				.setTagSelectionType(PictureModel.TagSelectionType.OR));
		
		tagSelectionMenu.add(andItem);
		tagSelectionMenu.add(orItem);
		
		ButtonGroup tagSelectionGroup = new ButtonGroup();
		tagSelectionGroup.add(andItem);
		tagSelectionGroup.add(orItem);
		orItem.setSelected(true);
		
		optionsMenu.add(tagSelectionMenu);
		menuBar.add(optionsMenu);
		
		JMenu layoutMenu = new JMenu("Layout");
		UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
		for (UIManager.LookAndFeelInfo info : infos) {
			JMenuItem LFMenuItem = new JMenuItem(info.getName());
			LFMenuItem.addActionListener(event -> {
				try {
					UIManager.setLookAndFeel(info.getClassName());
					SwingUtilities.updateComponentTreeUI(PhotoViewerFrame.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			layoutMenu.add(LFMenuItem);
		}
		menuBar.add(layoutMenu);
	}
}
