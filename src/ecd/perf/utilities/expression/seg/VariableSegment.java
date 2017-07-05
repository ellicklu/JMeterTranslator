package ecd.perf.utilities.expression.seg;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ecd.perf.utilities.expression.BinaryUtil;
import ecd.perf.utilities.expression.ats.SegmentParser;
import ecd.perf.utilities.expression.ats.SegmentParserOptions;
import ecd.perf.utilities.main.ASTDisplayer;
import ecd.perf.utilities.model.jmeter.CSVDataSet;

public class VariableSegment extends Segment
{
	public static final String DATABANK_VARIABLE_PREFIX = "db."; //$NON-NLS-1$
	private SegmentList m_variableNameSegment;
	private Segment m_recordedValue;
	
	private final HashMap<String, CSVDataSet> databanks;
	public VariableSegment(final HashMap<String, CSVDataSet> databanks)
	{
		this.databanks = databanks;
		m_variableNameSegment = new SegmentList();
	}

	@Override
	public void addSubSegment(Segment subSeg)
	{
		m_variableNameSegment.addSubSegment(subSeg);
	}

	public void setRecordedValue(Segment value)
	{
		m_recordedValue = value;
	}

	public void setVariableName(String name)
	{
		m_variableNameSegment = new SegmentList();

		addSubSegment(new LiteralSegment(SegmentParser.maskUnsafeBraceBracketAndSharp(name)));
	}

	@Override
	public byte[] evaluateBytes(Variables variables, SegmentParserOptions options)
	{
		return BinaryUtil.hexString2Binary(evaluateString(variables, options));
	}

	@Override
	public String evaluateString(Variables variables, SegmentParserOptions options)
	{
		String varName = m_variableNameSegment.evaluateString(variables, options);
		String value = variables.get(varName);
		
		//using recorded value
		if (value == null && options != null) {
			if (options.getUseRecordedValue() && m_recordedValue != null) {
				value = m_recordedValue.evaluateString(variables, options);
			}
			else if (options.getUseBlankForMissingVariable()) {
				value = ""; //$NON-NLS-1$
			}
		}
		
		//Databank variable can use recorded value only if databank isn't used.
		if (value == null) {
			if (isDatabankVariable(varName) && m_recordedValue != null && !options.isUseDatabank()) {
				value = m_recordedValue.evaluateString(variables, options);
			}
		}
		
		if (value == null) {
			if(variables.get(varName) != null) {
				value = variables.get(varName);
			} else {
				value = "DATABANK_VAL_NOT_FOUND";
			}
		}
		if (options.getUseUrlEncode()) {
			try {
				value = URLEncoder.encode(value, options.getCharset());
			} catch (UnsupportedEncodingException e) {
				//ignore the error
			}
		}
		return value;
	}
	
	private static boolean isDatabankVariable(String variableName){
		return variableName.startsWith(DATABANK_VARIABLE_PREFIX);
	}
	
	public String getVariableName()
	{
		List<Segment> segments = m_variableNameSegment.getSegments();
		if (segments.size() == 1 && segments.get(0) instanceof LiteralSegment)
			return ((LiteralSegment) segments.get(0)).getDisplayValue();
		
		return null;
	}
	
	public Segment getRecordedSegment()
	{
		return m_recordedValue;
	}

	@Override
	public String getRecordedValue()
	{
		return m_recordedValue != null ? m_recordedValue.getRecordedValue() : null;
	}

	@Override
	public String getDisplayValue()
	{
		return m_recordedValue != null ? m_recordedValue.getDisplayValue() : SegmentParser.DOUBLE_LBB + m_variableNameSegment.getDisplayValue() + SegmentParser.DOUBLE_RBB;
	}

	@Override
	public String getCurrentValue()
	{
		if (m_recordedValue == null)
			return SegmentParser.DOUBLE_LBB + m_variableNameSegment.getCurrentValue() + SegmentParser.DOUBLE_RBB;
		else
			return SegmentParser.DOUBLE_LBB + m_variableNameSegment.getCurrentValue() + ',' + m_recordedValue.getCurrentValue() + SegmentParser.DOUBLE_RBB;
	}

	@Override
	public String translate(Map<String, String> encodeOptions) {		
		String varName = m_variableNameSegment.translate(encodeOptions);
		if(isDatabankVariable(varName)) {
			String databankName = varName.substring(DATABANK_VARIABLE_PREFIX.length());
			int dotIndex = databankName.indexOf('.');
			if(dotIndex > -1) {
				String fieldName = databankName.substring(dotIndex+1);
				databankName = databankName.substring(0, dotIndex);
				if(databanks != null && databanks.containsKey(databankName)) {
					databanks.get(databankName).addDataField(fieldName);
					return "${"+fieldName+"}";
				}
			}
		}
		if(encodeOptions.containsKey(varName)) {
			return "${"+encodeOptions.get(varName) + "(${"+varName+"})}";
		} else {
			return "${"+varName+"}";
		}
	}
}
