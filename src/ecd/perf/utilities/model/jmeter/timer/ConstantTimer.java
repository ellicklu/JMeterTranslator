package ecd.perf.utilities.model.jmeter.timer;

import java.util.List;

import ecd.perf.utilities.model.jmeter.AbstractModelElement;
public class ConstantTimer extends AbstractModelElement{

	private static final long serialVersionUID = 1L;

	public ConstantTimer(int offset, int maximum) {
		super("Constant Timer");
		this.put("ConstantTimer.delay","${__Random("+offset+","+maximum+")}");
	}
	
	public ConstantTimer(String thinkFrom, String thinkTo) {
		super("Constant Timer");
		this.put("ConstantTimer.delay","${__Random("+thinkFrom+","+thinkTo+")}");
	}

	@Override
	public List<Object> getChildren() {
		return null;
	}

	@Override
	public String getTagName() {
		return "ConstantTimer";
	}

	@Override
	public String getTestclass() {
		return "ConstantTimer";
	}

	@Override
	public String getUIClass() {
		return "ConstantTimerGui";
	}

	@Override
	public boolean isPrintEmptyHashTree() {
		return true;
	}

}
