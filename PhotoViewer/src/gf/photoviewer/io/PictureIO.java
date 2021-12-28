package gf.photoviewer.io;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gf.photoviewer.resources.Album;
import gf.photoviewer.resources.Picture;
import gf.photoviewer.resources.Tag;

public class PictureIO {
	private static PictureIO singleton = null;
	private Connection conn;

	public static PictureIO getInstance() throws Exception {
		if (singleton == null)
			singleton = new PictureIO();
		
		return singleton;
	}
	
	private Picture fromResultSet(ResultSet result) throws SQLException {
		int id = result.getInt("Id");
		int albumId = result.getInt("AlbumId");
		Path path = Paths.get(result.getString("Path"));
		String label = result.getString("Label");
		return new Picture(id, albumId, path, label);
	}
	
	private Picture getLastRecord() throws Exception {
		String query = "SELECT * FROM Pictures "
				+ "WHERE Id = ("
				+ "SELECT MAX(Id) FROM Pictures)";
		try (PreparedStatement stat = conn.prepareStatement(query)) {
			ResultSet result = stat.executeQuery();
			result.next();
			return fromResultSet(result);
		}
	}
	
	private PictureIO() throws Exception {
		conn = DatabaseManager.getinstance().getConnection();
	}
	
	public Picture addPictureToAlbum(Picture picture, Album album) throws Exception {
		String query = "INSERT INTO Pictures (AlbumId, Path, Label) VALUES (?, ?, ?)";
		
		try (PreparedStatement stat = conn.prepareStatement(query)) {
			stat.setInt(1, album.getId());
			stat.setString(2, picture.getPath().toString());
			stat.setString(3, picture.getLabel());
			stat.executeUpdate();
		}
		
		return getLastRecord();
	}
	
	public void tagPicture(Picture picture, Tag tag) throws Exception {
		String query = "INSERT INTO TagPicturePairs (TagId, PictureId) VALUES (?, ?)";
		
		try (PreparedStatement stat = conn.prepareStatement(query)) {
			stat.setInt(1, tag.getId());
			stat.setInt(2, picture.getId());
			stat.executeUpdate();
		}
	}

	public List<Picture> getPicturesFromAlbum(Album album) throws Exception {
		String query = "SELECT * FROM Pictures WHERE AlbumId = ?";
		
		List<Picture> pictures = new ArrayList<>();
		
		try (PreparedStatement stat = conn.prepareStatement(query)) {
			stat.setInt(1, album.getId());
			
			try (ResultSet result = stat.executeQuery()) {
				while (result.next())
					pictures.add(fromResultSet(result));
			}
		}
		
		return pictures;
	}
	
	public List<Picture> getPicturesFromTagsOr(List<Tag> tags) throws Exception {
		String query = "SELECT * FROM Pictures "
				+ "INNER JOIN (SELECT PictureId FROM TagPicturePairs WHERE TagId = ?) pairs "
				+ "ON Pictures.Id = pairs.PictureId";
		
		Set<Picture> pictures = new HashSet<>();
		
		for (Tag tag : tags) {
			try (PreparedStatement stat = conn.prepareStatement(query)) {
				stat.setInt(1, tag.getId());

				try (ResultSet result = stat.executeQuery()) {
					while (result.next())
						pictures.add(fromResultSet(result));
				}
			}
		}
		
		List<Picture> picturesAsList = new ArrayList<>();
		picturesAsList.addAll(pictures);
		
		return picturesAsList;
	}
	
	public List<Picture> getPicturesFromTagsAnd(List<Tag> tags) throws Exception {
		Map<Picture, Integer> ids = new HashMap<>();
		
		for (Tag tag : tags) {
			List<Tag> tempTag = new ArrayList();
			tempTag.add(tag);
			List<Picture> pictures = getPicturesFromTagsOr(tempTag); 
			
			pictures.forEach(p -> ids.merge(p, 1, Integer::sum));
		}
		
		List<Picture> pictures = new ArrayList<>();
		
		ids.forEach((k, v) -> {
			if (v == tags.size())
				pictures.add(k);
		});
		
		return pictures;
	}
	
	public void removePicture(Picture picture) throws Exception {
		String query = "DELETE FROM Pictures WHERE id = ?";
		
		try (PreparedStatement stat = conn.prepareStatement(query)) {
			stat.setInt(1, picture.getId());
			stat.executeUpdate();
		}
		
		query = "DELETE FROM TagPicturePairs WHERE PictureId = ?";

		try (PreparedStatement stat = conn.prepareStatement(query)) {
			stat.setInt(1, picture.getId());
			stat.executeUpdate();
		}
	}
	
	public void removePicturesFromAlbum(Album album) throws Exception {
		for (Picture picture : getPicturesFromAlbum(album))
			removePicture(picture);
	}
	
	public void untagPicture(Picture picture, Tag tag) throws Exception {
		String query = "DELETE FROM TagPicturePairs WHERE TagId = ? AND PictureId = ?";
		
		try (PreparedStatement stat = conn.prepareStatement(query)) {
			stat.setInt(1, tag.getId());
			stat.setInt(2, picture.getId());
			stat.executeUpdate();
		}
	}
	
	public void removePicturesFromTag(Tag tag) throws Exception {
		String query = "DELETE FROM TagPicturePairs WHERE TagId = ?";
		
		try (PreparedStatement stat = conn.prepareStatement(query)) {
			stat.setInt(1, tag.getId());
			stat.executeUpdate();
		}
	}
	
	public void removeTagFromPicture(Tag tag, Picture picture) throws Exception {
		String query = "DELETE FROM TagPicturePairs WHERE TagId = ? AND PictureId = ?";
		
		try (PreparedStatement stat = conn.prepareStatement(query)) {
			stat.setInt(1, tag.getId());
			stat.setInt(2, picture.getId());
			stat.executeUpdate();
		}
	}
}
