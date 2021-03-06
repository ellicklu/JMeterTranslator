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
 * Convert the parameter comes after transform id from hexadecimal string to binary content. 
 * @since 2.1.0
 *
 */
public class DecodeFunction extends CustomFunction
{
	@Override
	public String getId()
	{
		return "decode"; //$NON-NLS-1$
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

		return BinaryUtil.hexString2Binary(param);
	}

	@Override
	public String translate(List<String> paraValues, Map<String,String> encodeOptions) {
		return "${__urldecode("+translateWithEscapeParam(paraValues.get(0))+")}";
	}
}
