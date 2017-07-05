package ecd.perf.utilities.expression.function;

import java.util.List;
import java.util.Map;

import ecd.perf.utilities.expression.*;
import ecd.perf.utilities.expression.ats.SegmentParserOptions;
import ecd.perf.utilities.expression.seg.Variables;

/**
 * Custom transform function which returns the current Openscript Product Version.
 * For example, the current product version may be "9.10.0173".
 * 
 * @since 2.2.0
 */
public class ProductVersionFunction extends CustomFunction
{
	@Override
	public String getId()
	{
		return "productVersion"; //$NON-NLS-1$
	}

	@Override
	public byte[] compute(Variables variables, List<String> parameters, SegmentParserOptions options)
	{
		String productVersion = "12.3.1"; //$NON-NLS-1$
		return productVersion.getBytes();
	}
	
	@Override
	public String translate(List<String> paraValues, Map<String,String> encodeOptions) {
		return "2.0";
	}
}
