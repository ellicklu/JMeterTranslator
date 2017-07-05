package ecd.perf.utilities.model.jmeter.logic;

import java.util.List;
import java.util.Map;

import ecd.perf.utilities.model.jmeter.AbstractModelElement;

public class LoopController extends AbstractModelElement{

	private static final long serialVersionUID = 1L;

	public LoopController() {
		super("Loop Controller");
		this.put("LoopController.continue_forever", "false");
		this.put("LoopController.loops", "1");
	}

	@Override
	public List<Object> getChildren() {
		return null;
	}

	@Override
	public String getTagName() {
		return "elementProp";
	}

	@Override
	public String getTestclass() {
		return "LoopController";
	}

	@Override
	public String getUIClass() {
		return "LoopControlPanel";
	}
	
	@Override
	public Map<String, String> getAttributes() {
		Map<String, String> attrs = super.getAttributes();
		attrs.put("elementType", "LoopController");
		return attrs;
	}

	@Override
	public boolean isPrintEmptyHashTree() {
		return false;
	}
}
