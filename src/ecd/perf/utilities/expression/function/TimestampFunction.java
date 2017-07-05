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
 * Custom transform function which returns the current system timestamp in milliseconds
 * @since 2.1.0
 */
public class TimestampFunction extends CustomFunction
{
	@Override
	public String getId()
	{
		return "timestamp"; //$NON-NLS-1$
	}

	@Override
	public byte[] compute(Variables variables, List<String> parameters, SegmentParserOptions options)
	{
		return new Long(System.currentTimeMillis()).toString().getBytes();
	}
	
	@Override
	public String translate(List<String> paraValues, Map<String,String> encodeOptions) {
		return "${__time()}";
	}
}
