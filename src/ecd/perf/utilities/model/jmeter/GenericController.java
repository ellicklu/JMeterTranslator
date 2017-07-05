package ecd.perf.utilities.model.jmeter;

import ecd.perf.utilities.model.jmeter.http.HTTPSampler;
public class GenericController extends AbstractGroupElement{
	private static final long serialVersionUID = 1L;
	protected GenericController() {
		super("Simple Controller");
	}

	public void addHTTPSampler(HTTPSampler http){
		addSubStep(http);
	}

	@Override
	public String getTagName() {
		return "GenericController";
	}

	@Override
	public String getTestclass() {
		return "GenericController";
	}

	@Override
	public String getUIClass() {
		return "LogicControllerGui";
	}
	
	@Override
	public boolean isPrintEmptyHashTree() {
		return true;
	}

}
