package ecd.perf.utilities.model.jmeter;

import ecd.perf.utilities.model.jmeter.http.HTTPSampler;
public class LoopGroupController extends AbstractGroupElement{
	private static final long serialVersionUID = 1L;
	protected LoopGroupController(String loops) {
		super("Loop Controller");
		this.put("LoopController.continue_forever", "false");
		this.put("LoopController.loops", loops);
	}

	public void addHTTPSampler(HTTPSampler http){
		addSubStep(http);
	}

	@Override
	public String getTagName() {
		return "LoopController";
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
	public boolean isPrintEmptyHashTree() {
		return true;
	}

}
