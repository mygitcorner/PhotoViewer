package gf.photoviewer.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import gf.photoviewer.io.PictureIO;
import gf.photoviewer.resources.Album;
import gf.photoviewer.resources.Picture;
import gf.photoviewer.resources.Tag;


public class PictureModel extends AbstractListModel<Picture> {
	private PictureIO pictureIO;
	private List<Picture> pictures;
	private CurrentResource currentResource;
	private TagSelectionType tagSelectionType;
	
	public enum TagSelectionType {
		AND,
		OR
	}

	public PictureModel() throws Exception {
		pictures = new ArrayList<>();
		pictureIO = PictureIO.getInstance();
		currentResource = new CurrentResource();
		tagSelectionType = TagSelectionType.OR;
	}

	@Override
	public int getSize() {
		return pictures.size();
	}

	@Override
	public Picture getElementAt(int index) {
		return pictures.get(index);
	}
	
	public List<Picture> getElements() {
		return pictures;
	}

	public void setPicturesToCurrent() throws Exception {
		if (currentResource.currentIsAlbum())
			pictures = pictureIO.getPicturesFromAlbum(currentResource.getCurrentAlbum());
		else if (currentResource.currentIsTags()) {
			switch (tagSelectionType) {
				case AND:
					pictures = pictureIO.getPicturesFromTagsAnd(currentResource.getCurrentTags());
					break;
				case OR:
					pictures = pictureIO.getPicturesFromTagsOr(currentResource.getCurrentTags());
					break;
			}
		}

		fireContentsChanged(this, 0, pictures.size());
	}

	private void clearResources() {
		if (!pictures.isEmpty()) {
			int last = pictures.size() - 1;
			pictures.clear();
			currentResource.setEmpty();
			fireIntervalRemoved(this, 0, last);
		}
	}

	public void addPicturesToAlbum(List<Picture> picturesToAdd, Album album) throws Exception {
		boolean isCurrent = currentResource.currentAlbumIs(album);

		for (Picture pictureToAdd : picturesToAdd) {
			pictureToAdd = pictureIO.addPictureToAlbum(pictureToAdd, album);
			
			if (isCurrent)
				pictures.add(pictureToAdd);
		}

		if (isCurrent)
			fireIntervalAdded(this, getSize() - picturesToAdd.size(), getSize() - 1);
	}

	public void tagPictures(List<Picture> picturesToTag, Tag tag) throws Exception {
		boolean isCurrent = currentResource.currentTagIs(tag);

		for (Picture pictureToTag : picturesToTag) {
			pictureIO.tagPicture(pictureToTag, tag);
			
			if (isCurrent) {
				pictures.add(pictureToTag);
			}
		}

		if (isCurrent)
			fireIntervalAdded(this, getSize() - picturesToTag.size(), getSize() - 1);
	}

	public void setCurrentAlbum(Album album) throws Exception {
		if (album != null) {
			currentResource.setCurrentAlbum(album);
			setPicturesToCurrent();
		} else {
			clearResources();
		}
	}

	public void setCurrentTags(List<Tag> tags) throws Exception {
		currentResource.setCurrentTags(tags);
		setPicturesToCurrent();
	}
	
	public TagSelectionType getTagSelectionType() {
		return tagSelectionType;
	}

	public void setTagSelectionType(TagSelectionType type) {
		tagSelectionType = type;
	}

	public void removePictures(List<Picture> picturesToRemove) throws Exception {
		int last = pictures.size() - 1;
		for (Picture pictureToRemove : picturesToRemove) {
			pictureIO.removePicture(pictureToRemove);
			pictures.remove(pictureToRemove);
		}
		fireIntervalRemoved(this, 0, last);
	}

	public void removePicturesFromAlbum(Album album) throws Exception {
		pictureIO.removePicturesFromAlbum(album);
		
		if (currentResource.currentAlbumIs(album))
			clearResources();
	}

	public void removePicturesFromTag(Tag tag) throws Exception {
		pictureIO.removePicturesFromTag(tag);
		if (currentResource.currentTagIs(tag))
			clearResources();
	}
	
	public void untagPictures(List<Picture> picturesToUntag, Tag tag) throws Exception {
		int last = pictures.size() - 1;
		for (Picture pictureToUntag : picturesToUntag) {
			pictureIO.untagPicture(pictureToUntag, tag);
			pictures.remove(pictureToUntag);
		}
		fireIntervalRemoved(this, 0, last);
	}
	
	public void removeTagsFromPicture(List<Tag> tags, Picture picture) throws Exception {
		for (Tag tag : tags) {
			if (currentResource.currentTagIs(tag)) {
				pictures.remove(picture);
				fireIntervalRemoved(this, 0, pictures.size());
			}
			
			pictureIO.removeTagFromPicture(tag, picture);
		}
	}

	public CurrentResource getCurrentResource() {
		return currentResource;
	}

	@Override
	public String toString() {
		return pictures.toString();
	}
}