package ecd.perf.utilities.model.jmeter.postProcessor;

import java.util.List;
public class JSR223PostProcessor extends PostProcessor {

	private static final long serialVersionUID = 1L;
	public JSR223PostProcessor(String testname, String filepath, String parameters, String script) {
		super(testname);//SolveClientID
		this.put("cacheKey", "cache_key_"+testname);//shall be unique
		this.put("filename", filepath);//C:\scripts\SolveclientID.groovy
		this.put("parameters", parameters);
		this.put("script", script);
		this.put("scriptLanguage", "groovy");
	}
	
	public void setCode(String code) {
		this.put("script", code);
	}

	@Override
	public List<Object> getChildren() {
		return null;
	}

	@Override
	public String getTagName() {
		return "JSR223PostProcessor";
	}

	@Override
	public String getTestclass() {
		return "JSR223PostProcessor";
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
