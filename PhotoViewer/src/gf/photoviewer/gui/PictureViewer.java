package gf.photoviewer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import gf.photoviewer.PVLogger;
import gf.photoviewer.event.PVHandler;
import gf.photoviewer.image.ThumbnailCache;
import gf.photoviewer.model.PictureModel;
import gf.photoviewer.resources.Picture;
import net.coobird.thumbnailator.Thumbnails;

public class PictureViewer extends JPanel {
	private PictureModel pictureModel;
	private ListSelectionModel pictureSelectionModel;
	
	private JPanel imagePanel;
	private JPanel buttonPanel;
	
	private ImageComponent imageComponent;
	
	private JButton goLeftButton;
	private JButton goRightButton;
	private JButton backButton;
	
	private GoBackAction goBackAction;
	private GoLeftAction goLeftAction;
	private GoRightAction goRightAction;
	
	private PVHandler<Void> goBackHandler;
	
	public PictureViewer(PictureModel pictureModel, ListSelectionModel pictureSelectionModel) {
		this.pictureModel = pictureModel;
		this.pictureSelectionModel = pictureSelectionModel;
		
		pictureSelectionModel.addListSelectionListener(event -> update());
		pictureModel.addListDataListener(new ListDataListener() {
			public void intervalAdded(ListDataEvent e) { update(); }
			public void intervalRemoved(ListDataEvent e) { update(); }
			public void contentsChanged(ListDataEvent e) { update(); }
		});
		
		setLayout(new BorderLayout());
		
		InputMap buttonInputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap buttonActionMap = getActionMap();
		
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		goBackHandler = new PVHandler<>();
		goBackAction = new GoBackAction();
		backButton = new JButton(goBackAction);
		buttonInputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "goBack");
		buttonActionMap.put("goBack", goBackAction);
		topPanel.add(backButton);
		 
		add(topPanel, BorderLayout.NORTH);
		
		imageComponent = new ImageComponent();
		add(imageComponent, BorderLayout.CENTER);
		
		buttonPanel = new JPanel();
		
		goLeftAction = new GoLeftAction();
		goLeftButton = new JButton(goLeftAction);
		buttonInputMap.put(KeyStroke.getKeyStroke("LEFT"), "goLeft");
		buttonActionMap.put("goLeft", goLeftAction);
		buttonPanel.add(goLeftButton);
		
		goRightAction = new GoRightAction();
		buttonInputMap.put(KeyStroke.getKeyStroke("RIGHT"), "goRight");
		buttonActionMap.put("goRight", goRightAction);
		goRightButton = new JButton(goRightAction);
		buttonPanel.add(goRightButton);
		
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
	public void addGoBackListener(Consumer<Void> c) {
		goBackHandler.addListener(c);
	}
	
	private int getSelection() {
		int[] indices = pictureSelectionModel.getSelectedIndices();
		if (indices.length > 0)
			return indices[0];
		else
			return -1;
	}
	
	private void changeSelection(int change) {
		int newIndex =  getSelection() + change;
		
		if (newIndex >= pictureModel.getSize())
			newIndex = pictureModel.getSize() -1;
		else if (newIndex < 0)
			newIndex = 0;
		
		pictureSelectionModel.setSelectionInterval(newIndex, newIndex);
	}
	
	private void update() {
		int currentIndex = getSelection();
		
		if (currentIndex != -1) {
			goRightButton.setEnabled(currentIndex < pictureModel.getSize() - 1);
			goLeftButton.setEnabled(currentIndex > 0);
			imageComponent.repaint();
		}
	}
	
	private class ImageComponent extends JComponent {
		@Override
		public void paintComponent(Graphics g) {
			int width = getWidth();
			int height = getHeight();
			try {
				int currentIndex = getSelection();
				
				if (currentIndex != -1) {
					Picture picture = pictureModel.getElementAt(currentIndex);
					BufferedImage image = Thumbnails
								.of(picture.getPath().toFile())
								.size(width, height)
								.asBufferedImage();

					g.drawImage(image, (width - image.getWidth(null)) / 2,
							(height - image.getHeight(null)) / 2, null);
				}
			} catch (IOException e) {
				PVLogger.getLogger().log(Level.WARNING, e.getMessage(), e);
				e.printStackTrace();
			}
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(0, 0);
		}
	}
	
	private class GoBackAction extends AbstractAction {
		public GoBackAction() {
			putValue(SMALL_ICON, new ImageIcon("PhotoViewer\\resources\\icons8-long-arrow-left-32.png"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			goBackHandler.invoke(null);
		}
	}
	
	private class GoLeftAction extends AbstractAction {
		public GoLeftAction() {
			putValue(SMALL_ICON, new ImageIcon("PhotoViewer\\resources\\icons8-chevron-left-32.png"));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			changeSelection(-1);
		}
	}
	
	private class GoRightAction extends AbstractAction {
		public GoRightAction() {
			putValue(SMALL_ICON, new ImageIcon("PhotoViewer\\resources\\icons8-chevron-right-32.png"));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			changeSelection(1);
		}
	}
}


