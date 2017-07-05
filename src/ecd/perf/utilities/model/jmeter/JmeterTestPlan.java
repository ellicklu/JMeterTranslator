package ecd.perf.utilities.model.jmeter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JmeterTestPlan extends AbstractModelElement {

	private static final long serialVersionUID = 1L;

	public JmeterTestPlan() {
		super("");
		testPlan = new TestPlan();
	}

	@Override
	public String getTagName() {
		return "jmeterTestPlan";
	}

	@Override
	public String getUIClass() {
		return null;
	}
	
	public String getTestclass() {
		return null;
	}
	
	private TestPlan testPlan;
	
	public TestPlan getTestPlan() {
		return this.testPlan;
	}

	@Override
	public List<Object> getChildren() {
		List<Object> tree = new ArrayList<Object>();
		tree.add(testPlan);
		return tree;
	}

	@Override
	public Map<String, String> getAttributes() {
		HashMap<String, String> attrs = new HashMap<String, String>();
		//version="1.2" properties="2.6" jmeter="2.11 r1554548"
		attrs.put("version", "1.2");
		attrs.put("properties", "2.9");
		attrs.put("jmeter", "3.0 r1743807");
		return attrs;
	}

	@Override
	public boolean isPrintEmptyHashTree() {
		return false;
	}
}
