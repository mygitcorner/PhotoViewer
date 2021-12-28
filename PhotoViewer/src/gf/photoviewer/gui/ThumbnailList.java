package gf.photoviewer.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Paths;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

import gf.photoviewer.PhotoViewerUtils;
import gf.photoviewer.event.PDCEvent;
import gf.photoviewer.event.PVHandler;
import gf.photoviewer.model.PictureModel;
import gf.photoviewer.renderer.ThumbnailRenderer;
import gf.photoviewer.resources.Picture;

public class ThumbnailList extends JList<Picture> {
	private static final int SCROLL = 30;
	
	private PVHandler<PDCEvent> pdcHandler;
	private PictureModel pictureModel;
	private ViewPictureAction viewPictureAction;
	
	public ThumbnailList(PictureModel pictureModel) {
		super(pictureModel);
		setBorder(BorderFactory.createEtchedBorder());
		setBackground(Color.WHITE); 
		setVisibleRowCount(-1);
		setLayoutOrientation(JList.HORIZONTAL_WRAP);
		setCellRenderer(new ThumbnailRenderer(this, pictureModel));
		
		this.pictureModel = pictureModel;
		pdcHandler = new PVHandler<>();
		viewPictureAction = new ViewPictureAction();
		
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ENTER"), "view");
		getActionMap().put("view", viewPictureAction);
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent event) {
				if ((event.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) != 0 &&
						event.getClickCount() >= 2) {
					viewSelectedPicture();
				}
			}
		});
	}
	
	public ViewPictureAction getViewPictureAction() {
		return viewPictureAction;
	}

	public void addPDCListener(Consumer<PDCEvent> c) {
		pdcHandler.addListener(c);
	}
	
	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return SCROLL;
	}
	
	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return SCROLL;
	}
	
	public void viewSelectedPicture() {
		int[] indices = getSelectedIndices();

		if (indices.length > 0) {
			Picture targetPicture = getModel().getElementAt(indices[0]);
			PDCEvent pdcEvent = new PDCEvent(indices[0]);
			pdcHandler.invoke(pdcEvent);
		}
	}
	
	private class ViewPictureAction extends AbstractAction {
		public ViewPictureAction() {
			super("View");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			viewSelectedPicture();
		}
	}
}
