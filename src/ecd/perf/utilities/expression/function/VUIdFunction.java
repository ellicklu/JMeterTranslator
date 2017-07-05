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
 * Custom transform function which returns the current VUser ID.
 * For example, the first VU to run in a session may be given an ID of "1". 
 * 
 * @since 2.2.0
 */
public class VUIdFunction extends CustomFunction
{
	@Override
	public String getId()
	{
		return "vuid"; //$NON-NLS-1$
	}

	@Override
	public byte[] compute(Variables variables, List<String> parameters, SegmentParserOptions options)
	{
		return "1".getBytes();
	}

	@Override
	public String translate(List<String> paraValues, Map<String,String> encodeOptions) {
		return "${__threadNum}";
	}
}
