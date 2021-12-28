package gf.photoviewer.image;

import java.io.File;
import java.util.stream.Stream;

import javax.swing.filechooser.FileFilter;

public class ImageFileFilter extends FileFilter {
	private static String[] extensions = { "jpg", "JPG", "jpeg", "png", "gif", "jpe", "jfif" };
	private static String description = "Image Files (" + String.join(", ", extensions) + ")";

	@Override
	public boolean accept(File file) {
		if (file.isDirectory())
			return true;
		
		return Stream.of(extensions).anyMatch(file.getPath()::endsWith);
	}

	@Override
	public String getDescription() {
		return description;
	}

}
