package ecd.perf.utilities.model.jmeter.logic;

import java.util.ArrayList;
import java.util.List;

import ecd.perf.utilities.model.jmeter.AbstractModelElement;
public class ModuleController extends AbstractModelElement {
	private static final long serialVersionUID = 1L;
	private final List<String> pathNodes;
	public ModuleController(String testname) {
		super(testname);
		pathNodes = new ArrayList<String>();
		this.put("ModuleController.node_path", pathNodes);
	}

	@Override
	public List<Object> getChildren() {
		return null;
	}

	@Override
	public String getTagName() {
		return "ModuleController";
	}

	@Override
	public String getTestclass() {
		return "ModuleController";
	}

	@Override
	public String getUIClass() {
		return "ModuleControllerGui";
	}
	
	public void appendPathNode(String testName) {
		pathNodes.add(testName);
	}

	@Override
	public boolean isPrintEmptyHashTree() {
		return true;
	}

}
