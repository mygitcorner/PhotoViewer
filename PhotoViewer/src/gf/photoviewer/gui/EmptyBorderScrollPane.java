package gf.photoviewer.gui;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

public class EmptyBorderScrollPane extends JScrollPane {

	public EmptyBorderScrollPane(Component view) {
		super(view);
		setBorder(BorderFactory.createEmptyBorder());
	}
	
}
