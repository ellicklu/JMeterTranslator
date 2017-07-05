package ecd.perf.utilities.expression.ats;

/**
 * Maintains various settings that apply when parsing and evaluating strings.
 */
public class SegmentParserOptions
{
	private boolean m_useRecordedValue;
	private boolean m_useBlankForMissingVariable;
	private boolean m_useDatabank;
	private boolean m_useUrlEncode;
	private String m_charset;
	
	public SegmentParserOptions() {}
	
	/**
	 * @return True if the parser should substitute recorded values when evaluating Variable Segments and a named variable cannot be found.
	 */
	public boolean getUseRecordedValue() 
	{
		return m_useRecordedValue;
	}
	
	/**
	 * Set to true if the parser should substitute recorded values when evaluating Variable Segments and a named variable cannot be found.
	 * @param useRecordedValue
	 */
	public void setUseRecordedValue(boolean useRecordedValue) 
	{
		m_useRecordedValue = useRecordedValue;
	}

	/**
	 * @return True if the parser should substitute a blank string when evaluating Variable Segments and a named variable cannot be found.
	 */
	public boolean getUseBlankForMissingVariable()
	{
		return m_useBlankForMissingVariable;
	}

	/**
	 * Set to true if the parser should a blank string when evaluating Variable Segments and a named variable cannot be found.
	 * @param useBlankForMissingVars
	 */
	public void setUseBlankForMissingVariable(boolean useBlankForMissingVariable)
	{
		m_useBlankForMissingVariable = useBlankForMissingVariable;
	}

	/**
	 * @return True if use Databank
	 */
	public boolean isUseDatabank() {
		return m_useDatabank;
	}

	/**
	 * Set to true if use Databank.
	 * @param useDatabank
	 */
	public void setUseDatabank(boolean useDatabank) {
		m_useDatabank = useDatabank;
	}
	
	/**
	 * Return True if solved values of variable segments need be url-encoded.
	 * @return
	 */
	public boolean getUseUrlEncode() {
		return m_useUrlEncode;
	}

	/**
	 * Set to true if solved values of variable segments need be url-encoded.
	 * @param urlEncode
	 */
	public void setUseUrlEncode(boolean urlEncode) {
		m_useUrlEncode = urlEncode;
	}
	
	public String getCharset()
	{
		return m_charset;
	}
	
	public void setCharset(String charset)
	{
		m_charset = charset;
	}
	
}
