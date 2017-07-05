package ecd.perf.utilities.expression;

import ecd.perf.utilities.expression.seg.Variables;

/**
 * Interface of objects that can be evaluated with variables during runtime.
 * @author lwq
 */
public interface IRuntimeEvaluatable {
	/**
	 * Evaluate the object with the given Variables Object.
	 * @param variables The runtime variables.
	 * @return The final string that some variables in it is substituted with the given variables.
	 */
	public String evaluate(Variables variables);
}
