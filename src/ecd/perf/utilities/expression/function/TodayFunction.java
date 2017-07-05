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
 * Return today's date in string or hex string.
 * @since 2.1.0
 */
public class TodayFunction extends CustomFunction
{
	@Override
	public String getId()
	{
		return "today"; //$NON-NLS-1$
	}

	@Override
	public byte[] compute(Variables variables, List<String> parameters, SegmentParserOptions options)
	{
		//check parameter
		String type = null;
		String format = null;
		if (parameters != null && parameters.size() == 2) {
			type = parameters.get(0);
			format = parameters.get(1);
		}
		if (parameters != null && parameters.size() == 1) {
			format = parameters.get(0);
		}
		if (format == null) {
			"error_format".getBytes();
		}
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format);
		String sDate = formatter.format(new java.util.Date());
		String sPrefix;
		if ("x".equals(type)) //$NON-NLS-1$
			sPrefix = Integer.toHexString(sDate.length()) + "_"; //$NON-NLS-1$
		else if ("n".equals(type)) //$NON-NLS-1$
			sPrefix = Integer.toString(sDate.length()) + "*"; //$NON-NLS-1$
		else
			sPrefix = ""; //$NON-NLS-1$

		return (sPrefix + sDate).getBytes();

	}
	
	@Override
	public String translate(List<String> parameters, Map<String,String> encodeOptions)
	{
		//check parameter
		String type = null;
		String format = null;
		if (parameters != null && parameters.size() == 2) {
			type = parameters.get(0);
			format = parameters.get(1);
		}
		if (parameters != null && parameters.size() == 1) {
			format = parameters.get(0);
		}
		if (format == null) {
			format = "";
		}

		return "${__time("+translateWithEscapeParam(format)+")}";

	}
}
