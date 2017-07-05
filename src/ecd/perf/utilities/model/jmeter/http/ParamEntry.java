package ecd.perf.utilities.model.jmeter.http;

public class ParamEntry {
	private String name;
    private String value;
    private String metadata;
    private boolean alwaysEncode;
    private boolean useEquals;
    
	public ParamEntry(String n, String v) {
    	name = n;
    	value = v;
    	metadata = "=";
    	alwaysEncode = true;
    	useEquals = true;
    }
	
	public ParamEntry(String n) {
    	name = n;
    	value = "";
    	metadata = "";
    	alwaysEncode = true;
    	useEquals = false;
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
    
	public String toString() {
		return name;
	}
	
    public String getMetadata() {
		return metadata;
	}
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
	public boolean getAlwaysEncode() {
		return alwaysEncode;
	}
	public void setAlwaysEncode(boolean alwaysEncode) {
		this.alwaysEncode = alwaysEncode;
	}
	public boolean getUseEquals() {
		return useEquals;
	}
	public void setUseEquals(boolean useEquals) {
		this.useEquals = useEquals;
	}
}
