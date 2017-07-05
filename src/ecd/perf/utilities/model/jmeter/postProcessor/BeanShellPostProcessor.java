package ecd.perf.utilities.model.jmeter.postProcessor;

import java.util.List;
public class BeanShellPostProcessor extends PostProcessor {

	private static final long serialVersionUID = 1L;

	public BeanShellPostProcessor(String testname, String filename, String parameters, boolean resetInterpreter, String script) {
		super(testname);
		this.put("filename", filename);
		this.put("parameters", parameters);
		this.put("resetInterpreter", resetInterpreter);
		this.put("script", script);
	}

	@Override
	public List<Object> getChildren() {
		return null;
	}

	@Override
	public String getTagName() {
		return "BeanShellPostProcessor";
	}

	@Override
	public String getTestclass() {
		return "BeanShellPostProcessor";
	}

	@Override
	public String getUIClass() {
		return "TestBeanGUI";
	}

	@Override
	public boolean isPrintEmptyHashTree() {
		return true;
	}

}
