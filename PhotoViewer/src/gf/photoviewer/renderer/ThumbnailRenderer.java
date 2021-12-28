package gf.photoviewer.renderer;

import static gf.photoviewer.PhotoViewerConstants.THUMBNAIL_SIZE;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import gf.photoviewer.image.ThumbnailCache;
import gf.photoviewer.model.PictureModel;
import gf.photoviewer.resources.Picture;

public class ThumbnailRenderer extends JComponent implements ListCellRenderer<Picture> {
	private static final int MAX_LABEL_LINES = 2;
	private static final int PADDING = 10;
	private static final int SELECTION_ALPHA = 50;
	private static final Font FONT = new Font("SansSerif", Font.PLAIN, 12);
	private static final BufferedImage DUMMY_IMAGE = new BufferedImage(THUMBNAIL_SIZE,
			THUMBNAIL_SIZE, BufferedImage.TYPE_INT_ARGB);
	
	private BufferedImage image;
	private String label;
	private int id;
	private Color selectionBackground;
	private boolean selected;
	
	public ThumbnailRenderer(JList thumbnailList, PictureModel pictureModel) {
		ThumbnailCache.getInstance().addImageEventListener(event -> thumbnailList.repaint());
		
		pictureModel.addListDataListener(new ListDataListener() {
			@Override
			public void intervalAdded(ListDataEvent e) { cache(); }

			@Override
			public void intervalRemoved(ListDataEvent e) { cache(); }

			@Override
			public void contentsChanged(ListDataEvent e) { cache(); }
			
			void cache() {
				ThumbnailCache.getInstance().cache(pictureModel.getElements());
			}
		});
	}

	private String[] splitLabel(FontMetrics fm) {
		//calculate how many chars fit inside the cell width
		int lineCount = (int) Math.ceil((double) fm.stringWidth(label) / THUMBNAIL_SIZE);
		int charsPerLine = (int) Math.ceil((double) label.length() / lineCount);
		
		//limit amount of lines
		int numLines = Math.min(MAX_LABEL_LINES, lineCount);
		String[] result = new String[numLines];
		
		//slice label in parts
		for (int i = 0, chIndex = 0; i < numLines; i++, chIndex += charsPerLine) {
			result[i] = label.substring(chIndex,
					Math.min(chIndex + charsPerLine, label.length()));
		}
		
		//if amount of lines exceeded limit, replace last 3
		//visible characters with dots to indicate a long label
		if (lineCount > MAX_LABEL_LINES) {
			int lastLine = result.length - 1;
			int lastChar = charsPerLine - 1;
			String lastLineWithDots = result[lastLine].substring(0, lastChar - 2) + "...";
			result[lastLine] = lastLineWithDots;
		}

		return result;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Picture> list,
			Picture value, int index, boolean isSelected, boolean cellHasFocus) {
		image = DUMMY_IMAGE;
		
		if (ThumbnailCache.getInstance().isCached(value)) {
			image = ThumbnailCache.getInstance().get(value);
		}
		
		label = value.getLabel();
		selected = isSelected;

		Color c = list.getSelectionBackground();
		selectionBackground = new Color(c.getRed(), c.getGreen(), c.getBlue(),
				SELECTION_ALPHA);
		
		return this;
	}

	@Override
	public void paintComponent(Graphics g) {
		g.translate(PADDING, PADDING);
		g.drawImage(image, (THUMBNAIL_SIZE - image.getWidth(null)) / 2, 0, null);

		g.setFont(FONT);
		FontMetrics fm = g.getFontMetrics(FONT);
		String[] labelParts = splitLabel(fm);

		int labely = image.getHeight(null) + fm.getAscent();
		for (String part : labelParts) {
			int labelx = (THUMBNAIL_SIZE - fm.stringWidth(part)) / 2;
			g.drawString(part, labelx, labely);
			labely += fm.getHeight();
		}

		g.translate(-PADDING, -PADDING);
		if (selected) {
			g.setColor(selectionBackground);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	public Dimension getPreferredSize() {
		FontMetrics fm = getGraphics().getFontMetrics(FONT);
		int width = THUMBNAIL_SIZE + 2 * PADDING;
		int height = image.getHeight(null) + splitLabel(fm).length * fm.getHeight() + 2 * PADDING;
		return new Dimension(width, height);
	}
}
