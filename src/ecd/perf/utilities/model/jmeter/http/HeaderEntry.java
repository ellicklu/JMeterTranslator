package ecd.perf.utilities.model.jmeter.http;

public class HeaderEntry {
	private String name;
    private String value;
    
    public HeaderEntry(String n, String v) {
    	name = n;
    	value = v;
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
}
