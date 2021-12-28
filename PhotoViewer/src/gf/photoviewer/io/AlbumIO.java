package gf.photoviewer.io;

import gf.photoviewer.resources.Album;

public class AlbumIO extends LabeledIndentityIO<Album> {
	private static AlbumIO singleton = null;

	public static AlbumIO getInstance() throws Exception {
		if (singleton == null)
			singleton = new AlbumIO();
		
		return singleton;
	}
	
	private AlbumIO() throws Exception {
		super("Albums", Album.class);
	}
}
