package ecd.perf.utilities.model.jmeter.logic;

public class WhileController extends AbstractConditionController {
	private static final long serialVersionUID = 1L;
	public final static String WHILE_CONDITION = "WhileController.condition";
	public WhileController(String testname) {
		super(testname);
	}
	
	public WhileController() {
		super("While Controller");
	}
	
	@Override
	public String getTagName() {
		return "WhileController";
	}

	@Override
	public String getTestclass() {
		return "WhileController";
	}

	@Override
	public String getUIClass() {
		return "WhileControllerGui";
	}

	@Override
	public boolean isPrintEmptyHashTree() {
		return true;
	}

	@Override
	protected String getConditionPropName() {
		return WHILE_CONDITION;
	}

}
