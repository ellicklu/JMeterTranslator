package ecd.perf.utilities.expression.seg;

import java.util.Map;

import ecd.perf.utilities.expression.ats.SegmentParserOptions;

public abstract class Segment
{
	public abstract String evaluateString(Variables variables, SegmentParserOptions options);

	public abstract byte[] evaluateBytes(Variables variables, SegmentParserOptions options);

	public abstract String translate(Map<String, String> encodeOptions);

	public abstract void addSubSegment(Segment subSeg);

	public abstract String getRecordedValue();
	
	public abstract String getDisplayValue();
	
	public abstract String getCurrentValue();
	
}
