package ecd.perf.utilities.model.jmeter;

import java.util.List;
import java.util.Map;

public class ConfigureTestElement extends AbstractModelElement {
	private static final long serialVersionUID = 1L;
	private static final String propPrefix = "HTTPSampler.";

	public ConfigureTestElement(String testName){
		super(testName);
		setDomain("");
		setPort("");
		setConnectTimeout("");
		setResponseTimeout("");
		setProtocol("");
		setContentEncoding("");
		setPath("");
		setConcurrentPool("");
		arguments = new ElementProperties(ElementProperties.guiclassHTTPArguments);
		this.put("HTTPsampler.Arguments", arguments);
	}

	@Override
	public String getTagName() {
		return "ConfigTestElement";
	}

	@Override
	public String getTestclass() {
		return "ConfigTestElement";
	}

	@Override
	public String getUIClass() {
		return "HttpDefaultsGui";
	}

	private String testName;
	private final ElementProperties arguments;
	
	public void setTestName(String testname) {
		testName = testname;
	}
	public String getTestName() {
		return testName;
	}
	
	public void setDomain(String domain) {
		this.put(propPrefix+"domain", domain);
	}
	
	public void setPort(String port) {
		this.put(propPrefix+"port", port);
	}
	
	public void setConnectTimeout(String connectTimeout) {
		this.put(propPrefix+"connect_timeout", connectTimeout);
	}
	
	public void setResponseTimeout(String responseTimeout) {
		this.put(propPrefix+"response_timeout", responseTimeout);
	}
	
	public void setProtocol(String protocol) {
		this.put(propPrefix+"protocol", protocol);
	}
	public void setContentEncoding(String contentEnconding) {
		this.put(propPrefix+"contentEnconding", contentEnconding);
	}
	public void setPath(String path) {
		this.put(propPrefix+"path", path);
	}
	public void setConcurrentPool(String concurrentPool) {
		this.put(propPrefix+"concurrentPool", concurrentPool);
	}
	
	@Override
	public List<Object> getChildren() {
		return null;
	}

	@Override
	public Map<String, String> getAttributes() {
		Map<String, String> attrs = super.getAttributes();
		return attrs;
	}

	@Override
	public boolean isPrintEmptyHashTree() {
		return true;
	}
}
