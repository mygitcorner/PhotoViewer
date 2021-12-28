package gf.photoviewer.io;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import gf.photoviewer.resources.Picture;
import gf.photoviewer.resources.Tag;

public class TagIO extends LabeledIndentityIO<Tag >{
	private static TagIO singleton = null;

	public static TagIO getInstance() throws Exception {
		if (singleton == null)
			singleton = new TagIO();
		
		return singleton;
	}
	
	private TagIO() throws Exception {
		super("Tags", Tag.class);
	}
	
	public List<Tag> getTagsFromPicture(Picture picture) throws Exception {
		String query = "SELECT * FROM Tags WHERE Id IN "
				+ "(SELECT TagId FROM TagPicturePairs WHERE pictureId = ?)";
		
		List<Tag> tags = new ArrayList<>();

		try (PreparedStatement stat = conn.prepareStatement(query)) {
			stat.setInt(1, picture.getId());

			try (ResultSet result = stat.executeQuery()) {
				while (result.next())
					tags.add(fromResultSet(result));
			}
		}

		return tags;
	}
	
	@Override
	public void remove(Tag tag) throws Exception {
		super.remove(tag);
		String query = "DELETE FROM TagPicturePairs WHERE TagId = ?";

		try (PreparedStatement stat = conn.prepareStatement(query)) {
			stat.setInt(1, tag.getId());
			stat.executeUpdate();
		}
	}
}
