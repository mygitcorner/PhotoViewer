package gf.photoviewer.resources;

import java.nio.file.Path;

/**
 * A {@code Picture} Object represents an image.
 * The path of this image and a label are stored.
 * Each picture has an id. Pictures that represent
 * different images should have different id's.
 * Pictures are contained in albums. The corresponding
 * album is represented by the albumId.
 */
public class Picture extends LabeledIdentity {
	private int albumId;
	private Path path;
	
	/**
	 * Constructs a picture with the path and label fields set.
	 * The id and albumId are set to invalid id's.
	 * 
	 * @param path The file path to the image
	 * @param label The label corresponding to the image. Typically the file name
	 */
	public Picture(Path path, String label) {
		this(INVALID_ID, INVALID_ID, path, label);
	}
	
	/** Constructs a picture with all fields set.
	 * 
	 * @param id The id of this picture
	 * @param albumId The id of the album this picture is contained in
	 * @param path The file path to the image
	 * @param label The label corresponding to the image. Typically the file name
	 */
	public Picture(int id, int albumId, Path path, String label) {
		super(id, label);
		this.albumId = albumId;
		this.path = path;
	}

	public int getAlbumId() {
		return albumId;
	}

	public void setAlbumId(int albumId) {
		this.albumId = albumId;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return super.toString() + "Picture[albumId=" + albumId + ", path=" + path + "]";
	}
	
	
}
