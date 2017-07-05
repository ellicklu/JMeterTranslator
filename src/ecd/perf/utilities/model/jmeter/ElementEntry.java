package ecd.perf.utilities.model.jmeter;

public class ElementEntry {
	private String name;
    private String value;
    private String metadata;
    
    public ElementEntry(String n, String v, String m) {
    	name = n;
    	value = v;
    	metadata = m;
    }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getMetadata() {
		return metadata;
	}
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
    
	public String toString() {
		return name;
	}
}
