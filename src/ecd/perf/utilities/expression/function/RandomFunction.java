package ecd.perf.utilities.expression.function;

import java.util.List;
import java.util.Map;
import java.util.Random;

import ecd.perf.utilities.expression.*;
import ecd.perf.utilities.expression.ats.SegmentParserOptions;
import ecd.perf.utilities.expression.seg.Variables;

/**
 * Get the random value between the first two parameters that comes after the transform id.
 * @since 2.1.0
 */
public class RandomFunction extends CustomFunction
{
	private final Random m_random = new Random();

	@Override
	public String getId()
	{
		return "random"; //$NON-NLS-1$
	}

	@Override
	public byte[] compute(Variables variables, List<String> customArguments, SegmentParserOptions options)
	{
		// check parameters
		if (customArguments == null || customArguments.size() == 0)
			return "1".getBytes();

		String bottom_str = null;
		String ceil_str = null;

		if (customArguments.size() == 1) {
			bottom_str = "0";//$NON-NLS-1$
			ceil_str = customArguments.get(0);
		}
		else {
			bottom_str = customArguments.get(0);
			ceil_str = customArguments.get(1);
		}

		// parse and validate parameters
		int ceil = 0;
		int bottom = 0;
		try {
			ceil = Integer.parseInt(ceil_str);
		}
		catch (Exception e) {
			return "1".getBytes();
		}
		try {
			bottom = Integer.parseInt(bottom_str);
		}
		catch (Exception e) {
			return "1".getBytes();
		}
		if (ceil < 0 || bottom < 0 || ceil <= bottom) {
			return "1".getBytes();
		}

		// get random value
		int randomValue = bottom + m_random.nextInt(ceil - bottom);
		return String.valueOf(randomValue).getBytes();
	}

	@Override
	public String translate(List<String> paraValues, Map<String,String> encodeOptions) {
		String bottom_str = null;
		String ceil_str = null;
		// check parameters
		if (paraValues == null || paraValues.size() == 0){
			bottom_str = "0";
			ceil_str = "9";
		} else if (paraValues.size() == 1) {
			bottom_str = "0";//$NON-NLS-1$
			ceil_str = paraValues.get(0);
		} else {
			bottom_str = paraValues.get(0);
			ceil_str = paraValues.get(1);
		}

		// parse and validate parameters
		int ceil = 0;
		int bottom = 0;
		try {
			ceil = Integer.parseInt(ceil_str);
		}
		catch (Exception e) {
			ceil = 9;
		}
		try {
			bottom = Integer.parseInt(bottom_str);
		}
		catch (Exception e) {
			bottom = 0;
		}
		if (ceil < 0 || bottom < 0 || ceil <= bottom) {
			ceil = bottom + 1;
		}

		return "${__Random("+bottom+","+ceil+")}";
	}


}
