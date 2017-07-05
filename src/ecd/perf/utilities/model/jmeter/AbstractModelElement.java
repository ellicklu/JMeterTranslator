package ecd.perf.utilities.model.jmeter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractModelElement extends LinkedHashMap<String,Object>{
	private static final long serialVersionUID = 1L;
	public abstract String getTagName();
	public abstract String getUIClass();
	public abstract String getTestclass();
	public abstract List<Object> getChildren();
	public abstract boolean isPrintEmptyHashTree();
	public LinkedHashMap<String, Object> getProperties() {
		return this;
	}
	private final String testName;
	private boolean enabled=true;
	private AbstractGroupElement parent;

	protected AbstractModelElement(String testname) {
		testName = testname;
	}
	public void setEnable(boolean enable) {
		this.enabled = enable;
	}
	
	public boolean isEnabled(){
		return this.enabled;
	}
	
	public String getTestName() {
		return testName;
	}
	
	public AbstractGroupElement getParent() {
		return parent;
	}
	
	protected void setParent(AbstractGroupElement parent) {
		this.parent = parent;
	}

	public Map<String, String> getAttributes() {
		HashMap<String, String> attrs = new HashMap<String, String>();
		//version="1.2" properties="2.6" jmeter="2.11 r1554548"
		attrs.put("enabled", String.valueOf(isEnabled()));
		attrs.put("testname", testName);
		return attrs;
	}
	
	protected void initElementProperties() {
		
	}
}
