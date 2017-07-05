package ecd.perf.utilities.expression.function;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import ecd.perf.utilities.expression.*;
import ecd.perf.utilities.expression.ats.SegmentParserOptions;
import ecd.perf.utilities.expression.seg.Variables;


/**
 * Custom transform function which returns a randomly generated UUID number
 * @since 2.5.0
 */
public class GUIDFunction extends CustomFunction
{
	@Override
	public String getId()
	{
		return "guid"; //$NON-NLS-1$
	}

	@Override
	public byte[] compute(Variables variables, List<String> parameters, SegmentParserOptions options)
	{
		return UUID.randomUUID().toString().toUpperCase().getBytes();
	}
	
	@Override
	public String translate(List<String> paraValues, Map<String,String> encodeOptions) {
		return "${UUID()}";
	}
}
