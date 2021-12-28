package gf.photoviewer.model;

import gf.photoviewer.io.AlbumIO;
import gf.photoviewer.resources.Album;

public class AlbumModel extends LabeledIdentityModel<Album, AlbumIO> {
	public AlbumModel() throws Exception {
		super(AlbumIO.class);
	}
}
