package ecd.perf.utilities.model.jmeter.logic;

import ecd.perf.utilities.model.jmeter.AbstractGroupElement;

public abstract class AbstractConditionController extends AbstractGroupElement {

	private static final long serialVersionUID = 1L;

	protected AbstractConditionController(String testname) {
		super(testname);
		this.put(getConditionPropName(), "NOT_SET_YET");
	}
	
	protected abstract String getConditionPropName();

	public void setWaitForVariableCondition(String variableName) {
		this.put(getConditionPropName(), "${__javaScript(\"${"+variableName+"}\" == \"\";)}");
	}
	
	public void setLoopCondition(String varLoopCount, String maxLoopNum) {
		this.put(getConditionPropName(), "${__javaScript(${__intSum(${"+varLoopCount+"},1,"+varLoopCount+")} < "+maxLoopNum+";)}"); 
	}
	
	public void setExpressionCondition(String expression) {
		this.put(getConditionPropName(), expression);
	}
	
	public void setTrueFalseCondition(boolean val) {
		this.put(getConditionPropName(), String.valueOf(val));
	}
}
