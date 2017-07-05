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
 * Get the length of the parameter that comes after the transform id.
 * @since 2.1.0
 */
public class EncryptFunction extends CustomFunction
{
	@Override
	public String getId()
	{
		return "encrypt"; //$NON-NLS-1$
	}

	@Override
	public byte[] compute(Variables variables, List<String> parameters, SegmentParserOptions options)
	{
		String param = null;
		if (parameters != null && parameters.size() != 0) {
			param = parameters.get(0);
		}
		if (param == null) {
			return "".getBytes();
		}

		return "".getBytes();
	}
	@Override
	public String translate(List<String> paraValues, Map<String,String> encodeOptions) {
		return "${__encrypt("+translateWithEscapeParam(paraValues.get(0))+")}";
	}
}
