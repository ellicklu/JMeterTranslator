package ecd.perf.utilities.model.jmeter.logic;
public class IfController extends AbstractConditionController {
	private static final long serialVersionUID = 1L;
	private static final String IF_CONDITION = "IfController.condition";
	public IfController(String testname) {
		super(testname);
		this.put("IfController.evaluateAll", false);
	}

	@Override
	protected String getConditionPropName() {
		return IF_CONDITION;
	}

	@Override
	public String getTagName() {
		return "IfController";
	}

	@Override
	public String getTestclass() {
		return "IfController";
	}

	@Override
	public String getUIClass() {
		return "IfControllerPanel";
	}

	@Override
	public boolean isPrintEmptyHashTree() {
		return true;
	}

}
