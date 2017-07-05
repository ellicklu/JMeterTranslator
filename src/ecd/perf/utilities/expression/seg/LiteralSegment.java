package ecd.perf.utilities.expression.seg;

import java.util.Map;

import ecd.perf.utilities.expression.BinaryUtil;
import ecd.perf.utilities.expression.ats.SegmentParser;
import ecd.perf.utilities.expression.ats.SegmentParserOptions;

public class LiteralSegment extends Segment
{
	private String m_parsableLiteralValue;
	private String m_unsafeLiteralValue;

	public LiteralSegment()
	{
	}

	/**
	 * @param value A properly escaped, parsable string. This value must NOT contain invalid transform syntax.
	 * Valid inputs:
	 * ABC#@DEF#{
	 * Invalid inputs:
	 * ABC@DEF{
	 */
	public LiteralSegment(String parsableLiteralValue)
	{
		m_parsableLiteralValue = parsableLiteralValue;
		m_unsafeLiteralValue = SegmentParser.unmaskUnsafeChars(parsableLiteralValue);
	}

	/**
	 *  @param value A properly escaped, parsable string. This value must NOT contain invalid transform syntax.
	 * Valid inputs:
	 * ABC#@DEF#{
	 * Invalid inputs:
	 * ABC@DEF{
	 */
	public void setLiteralValue(String parsableLiteralValue)
	{
		m_parsableLiteralValue = parsableLiteralValue;
		m_unsafeLiteralValue = SegmentParser.unmaskUnsafeChars(parsableLiteralValue);
	}

	@Override
	public byte[] evaluateBytes(Variables variables, SegmentParserOptions options)
	{
		return BinaryUtil.hexString2Binary(evaluateString(variables, options));
	}

	@Override
	public String evaluateString(Variables variables, SegmentParserOptions options)
	{
		return m_unsafeLiteralValue;
	}

	@Override
	public void addSubSegment(Segment subSeg)
	{
		//do nothing
	}

	@Override
	public String getRecordedValue()
	{
		return m_unsafeLiteralValue;
	}

	@Override
	public String getDisplayValue()
	{
		return m_unsafeLiteralValue;
	}

	@Override
	public String getCurrentValue()
	{
		return m_parsableLiteralValue;
	}
	
	/**
	 * Returns the literal value of the segment, NOT in a parsable format.
	 * 
	 * This method will NOT return any escape sequences for tokens
	 * that might be used by a parser.
	 * 
	 * Example 1:
	 * Consider the following String that is parsed into a LiteralSegment:
	 * ABC@DEF{GHI
	 * 
	 * The literal value of the LiteralSegment would return:
	 * ABC@DEF{GHI
	 * 
	 * Example 2:
	 * Consider the following String that is parsed into a LiteralSegment:
	 * ABC#@DEF#{GHI
	 * 
	 * The literal value of the LiteralSegment will be:
	 * ABC@DEF{GHI
	 * 
	 * @return Literal value of the segment. The returned value might not be able to be parsed into a Segment.
	 */
	public String getLiteralValue()
	{
		return m_unsafeLiteralValue;
	}

	@Override
	public String translate(Map<String,String> encodeOptions) {
		return m_unsafeLiteralValue;
	}
}
