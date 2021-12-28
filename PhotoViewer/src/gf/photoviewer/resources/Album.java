package gf.photoviewer.resources;

public class Album extends LabeledIdentity {
	public Album(String label) {
		this(INVALID_ID, label);
	}
	
	public Album(int id, String label) {
		super(id, label);
	}
	
	@Override
	public String toString() {
		return super.toString() + "Album[]";
	}
}
