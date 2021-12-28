package gf.photoviewer.event;

import gf.photoviewer.resources.Picture;

public class ImageEvent {
	private Picture picture;

	public ImageEvent(Picture picture) {
		this.picture = picture;
	}

	public Picture getPicture() {
		return picture;
	}

	public void setPicture(Picture picture) {
		this.picture = picture;
	}	
}
