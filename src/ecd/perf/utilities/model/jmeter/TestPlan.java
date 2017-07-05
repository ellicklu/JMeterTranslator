package ecd.perf.utilities.model.jmeter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ecd.perf.utilities.model.jmeter.http.CookieManager;
import ecd.perf.utilities.model.jmeter.http.HeaderManager;

public class TestPlan extends AbstractModelElement{
	private static final long serialVersionUID = 1L;
	private static final String propPrefix = "TestPlan.";

	private final ElementProperties arguments;
	
	private final CookieManager cookieMgr;
	private final HeaderManager headerMgr;
	private ConfigureTestElement configure;
	private final ThreadGroup threadGroup;
	
	private final ThreadGroup moduleActions;
	
	@Override
	public String getTagName() {
		return "TestPlan";
	}

	@Override
	public String getUIClass() {
		return "TestPlanGui";
	}

	@Override
	public String getTestclass() {
		return "TestPlan";
	}
  
	
	public void setComment(String comment) {
		this.put(propPrefix+"comments", comment);
	}
	
	public void setFunctionalMode(boolean isFunctional) {
		this.put(propPrefix+"functional_mode", isFunctional);
	}
	
	public void setSerializeThreadGroups(boolean isSerial) {
		this.put(propPrefix+"serialize_threadgroups", isSerial);
	}
	
	public void setUserDefinedClassPath(String classPath) {
		this.put(propPrefix+"user_define_classpath", classPath);
	}
	
	public TestPlan(){
		super("Test Plan");

		setComment("");
		setFunctionalMode(false);
		setSerializeThreadGroups(false);
		setUserDefinedClassPath("");
		arguments = new ElementProperties(ElementProperties.guiclassHTTPArguments); 
		this.put("TestPlan.user_defined_variables", arguments);

		configure = new ConfigureTestElement("HTTP Request Defaults");
		cookieMgr = new CookieManager();
		
		headerMgr = new HeaderManager();
		
		threadGroup = new ThreadGroup();
		
		moduleActions = new ThreadGroup("ActionModules", false);
	}
	
	@Override
	public List<Object> getChildren() {
		List<Object> tree = new ArrayList<Object>();
		tree.add(configure);
		tree.add(cookieMgr);
		tree.add(headerMgr);
		tree.add(threadGroup);
		tree.add(moduleActions);
		return tree;
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
	
	public ThreadGroup getThreadGroup() {
		return threadGroup;
	}
	
	public ThreadGroup getModuleActions() {
		return moduleActions;
	}
	
	public void addGlobalHeader(String name, String value) {
		headerMgr.addHeader(name, value);
	}
	
	public void addGlobalCookie(String name, String value, String domain, String path, long expire, boolean isSecure) {
		cookieMgr.addCokkie(name, value, domain, path, expire, isSecure);
	}
	
	public void addGlobalVariable(String name, String value) {
		arguments.addParameter(name, value);
	}
}
