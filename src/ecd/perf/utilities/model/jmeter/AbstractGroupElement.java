package ecd.perf.utilities.model.jmeter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGroupElement extends AbstractModelElement {
	private static final long serialVersionUID = 1L;
	private final List<Object> subSteps;
	protected AbstractGroupElement(String testname) {
		super(testname);
		subSteps = new ArrayList<Object>();
	}
	public void addSubStep(AbstractModelElement subStep) {
		subSteps.add(subStep);
		subStep.setParent(this);
	}

	@Override
	public List<Object> getChildren() {
		return subSteps;
	}
}
