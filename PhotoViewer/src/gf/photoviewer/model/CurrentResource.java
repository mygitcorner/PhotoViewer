package gf.photoviewer.model;

import java.util.List;

import gf.photoviewer.resources.Album;
import gf.photoviewer.resources.Tag;

public class CurrentResource {
	private Album currentAlbum;
	private List<Tag> currentTags;

	public CurrentResource() {
		currentAlbum = null;
		currentTags = null;
	}

	public void setCurrentAlbum(Album album) {
		currentAlbum = album;
		currentTags = null;
	}

	public void setCurrentTags(List<Tag> tags) {
		currentAlbum = null;
		currentTags = tags;
	}

	public Album getCurrentAlbum() {
		return currentAlbum;
	}

	public List<Tag> getCurrentTags() {
		return currentTags;
	}

	public boolean currentIsTags() {
		return currentTags != null;
	}

	public boolean currentIsAlbum() {
		return currentAlbum != null;
	}
	
	public boolean currentAlbumIs(Album album) {
		if (currentIsAlbum())
			return currentAlbum.equals(album);
		else
			return false;
	}
	
	public boolean currentTagIs(Tag tag) {
		if (currentIsTags())
			return currentTags.contains(tag);
		else
			return false;
	}

	public void setEmpty() {
		currentAlbum = null;
		currentTags = null;
	}
}