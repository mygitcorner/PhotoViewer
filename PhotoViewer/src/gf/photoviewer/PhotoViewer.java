package gf.photoviewer;

import java.awt.EventQueue;
import java.nio.file.Paths;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.UIManager;

import gf.photoviewer.resources.Picture;
import gf.photoviewer.resources.Tag;

public class PhotoViewer {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			} catch (Exception e) {
				PVLogger.getLogger().log(Level.WARNING, "Windows look and feel not available", e);
			}

			JFrame frame = new PhotoViewerFrame();
			frame.setVisible(true);
		});
	}

}
