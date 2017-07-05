package ecd.perf.utilities.expression.function;

import java.util.List;
import java.util.Map;

import ecd.perf.utilities.expression.*;
import ecd.perf.utilities.expression.ats.SegmentParserOptions;
import ecd.perf.utilities.expression.seg.Variables;

public class TopLevelStepNameFunction extends CustomFunction
{

        private static final String EMPTY_STEPNAME = "EMPTY_STEP_GROUP"; //$NON-NLS-1$

	@Override
	public String getId()
	{
		return "topLevelStepName"; //$NON-NLS-1$
	}

	@Override
	public byte[] compute(Variables variables, List<String> customArguments,
			SegmentParserOptions options) {
		return EMPTY_STEPNAME.getBytes();
	}

	@Override
	public String translate(List<String> paraValues, Map<String,String> encodeOptions) {
		return "topLevelStepName";
	}
}
