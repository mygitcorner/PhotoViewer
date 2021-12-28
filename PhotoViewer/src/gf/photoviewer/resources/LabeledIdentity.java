package gf.photoviewer.resources;

public class LabeledIdentity {
	protected static final int INVALID_ID = -1;
	
	private int id;
	String label;

	public LabeledIdentity(int id, String label) {
		this.id = id;
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public final boolean equals(Object otherObj) {
		if (getClass() != otherObj.getClass())
			return false;
		
		LabeledIdentity otherId = (LabeledIdentity) otherObj; 
		return id == otherId.id;
	}
	
	@Override
	public final int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return "LabeledIdentity[id=" + id + ", label=" + label + "]";
	}
}
