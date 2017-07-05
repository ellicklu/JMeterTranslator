package ecd.perf.utilities.model.jmeter;

public class TransactionController extends AbstractGroupElement {
	private static final long serialVersionUID = 1L;
	
	public TransactionController(String testname) {
		super(testname);
		this.put("TransactionController.includeTimers", false);
		this.put("TransactionController.parent", false);
	}
	
	@Override
	public String getTagName() {
		return "TransactionController";
	}

	@Override
	public String getTestclass() {
		return "TransactionController";
	}

	@Override
	public String getUIClass() {
		return "TransactionControllerGui";
	}

	@Override
	public boolean isPrintEmptyHashTree() {
		return true;
	}

}
