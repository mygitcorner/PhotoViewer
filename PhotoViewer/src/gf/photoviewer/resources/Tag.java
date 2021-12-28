package gf.photoviewer.resources;

public class Tag extends LabeledIdentity {
	public Tag(String label) {
		this(INVALID_ID, label);
	}
	
	public Tag(int id, String label) {
		super(id, label);
	}
	
	@Override
	public String toString() {
		return "Tag[]";
	}
	
	
}
