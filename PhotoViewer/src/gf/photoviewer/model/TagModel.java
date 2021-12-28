package gf.photoviewer.model;

import java.util.List;

import gf.photoviewer.io.TagIO;
import gf.photoviewer.resources.Picture;
import gf.photoviewer.resources.Tag;

public class TagModel extends LabeledIdentityModel<Tag, TagIO> {
	public TagModel() throws Exception {
		super(TagIO.class);
	}
	
	public List<Tag> getTagsFromPicture(Picture picture) throws Exception {
		return getIOObj().getTagsFromPicture(picture);
	}
}
