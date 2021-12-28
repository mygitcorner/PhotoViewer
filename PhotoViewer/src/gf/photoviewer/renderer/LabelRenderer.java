package gf.photoviewer.renderer;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import gf.photoviewer.resources.LabeledIdentity;

public class LabelRenderer<E extends LabeledIdentity> extends JLabel implements ListCellRenderer<E> {
	private static final int PADDING = 30;

	@Override
	public Component getListCellRendererComponent(JList<? extends E> list, E value, int index,
			boolean isSelected, boolean cellHasFocus) {
		setText(value.getLabel());
		setOpaque(true);
		setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
		setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
		return this;
	}
	
	public Dimension getPreferredSize() {
		Dimension labelPref = super.getPreferredSize();
		return new Dimension(labelPref.width + PADDING, labelPref.height);
	}
}