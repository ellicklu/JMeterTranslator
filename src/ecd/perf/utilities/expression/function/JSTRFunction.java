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
 * 
 * @since 2.1.0
 */
public class JSTRFunction extends CustomFunction
{
	@Override
	public String getId()
	{
		return "jstr"; //$NON-NLS-1$
	}

	@Override
	public byte[] compute(Variables variables, List<String> parameters, SegmentParserOptions options)
	{
		//check parameter
		String param = null;
		if (parameters != null && parameters.size() != 0) {
			param = parameters.get(0);
		}
		if (param == null) {
			return null;
		}
		String all = "0123456789ABCDEF"; //$NON-NLS-1$
		int length = param.length();
		StringBuffer buffer = new StringBuffer();
		buffer.append('\\');
		buffer.append(all.charAt((length  >> 12) & 0x0000000F));
		buffer.append(all.charAt((length  >> 8) & 0x0000000F));
		buffer.append('\\');
		buffer.append(all.charAt((length  >> 4) & 0x0000000F));
		buffer.append(all.charAt((length & 0x0000000F)));
		buffer.append(param);
		return buffer.toString().getBytes();
	}
	@Override
	public String translate(List<String> paraValues, Map<String,String> encodeOptions) {
		return "${__jstr("+translateWithEscapeParam(paraValues.get(0))+")}";
	}
}
