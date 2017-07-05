/**
 * Copyright 1997-2009 Oracle All rights reserved.
 */
package ecd.perf.utilities.expression.function;

import java.util.List;
import java.util.Map;

import ecd.perf.utilities.expression.*;
import ecd.perf.utilities.expression.ats.SegmentParserOptions;
import ecd.perf.utilities.expression.seg.Variables;

/**
 * Custom transform function which gets the value of the
 * variable provided in the first argument and increments its value by 1, or by
 * the value of the 2nd argument.
 * The returned value is the original value of the variable.
 * @since 2.1.0
 */
public class VariableIncrementerFunction extends CustomFunction
{
	@Override
	public String getId()
	{
		return "getAndIncrement"; //$NON-NLS-1$
	}

	@Override
	public byte[] compute(Variables variables, List<String> customArguments, SegmentParserOptions options)
	{
		String varName = null;
		int incrementBy = 1;//default value
		if (customArguments != null && customArguments.size() >= 1) {
			varName = customArguments.get(0);
			if (customArguments.size() >= 2) {
				incrementBy = new Integer(customArguments.get(1)).intValue();
			}
		}
		if (varName == null) {
			"-1".getBytes();
		}
		String value = (String) variables.get(varName);

		if (value == null) {
			"-1".getBytes();
		}

		int iValue = 0;
		try {
			iValue = Integer.parseInt(value);
		}
		catch (NumberFormatException ignore) {
		}
		variables.set(varName, (iValue + incrementBy) + ""); //$NON-NLS-1$
		return value.getBytes();

	}

	@Override
	public String translate(List<String> paraValues, Map<String,String> encodeOptions) {
		String varName = null;
		int incrementBy = 1;//default value
		if (paraValues != null && paraValues.size() >= 1) {
			varName = paraValues.get(0);
			if (paraValues.size() >= 2) {
				incrementBy = new Integer(paraValues.get(1)).intValue();
			}
		}

		return "${__intSum(${"+varName+"},"+incrementBy+","+varName+")}";
	}
}
