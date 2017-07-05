package ecd.perf.utilities.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ecd.perf.utilities.expression.ats.SegmentParser;
import ecd.perf.utilities.expression.ats.SegmentParserOptions;
import ecd.perf.utilities.expression.seg.Segment;
import ecd.perf.utilities.expression.seg.Variables;

public class CustomFunctionSegment extends Segment
{
	private List<Segment> m_parameterList;

	private String m_functionId;

	private Segment m_recordedValue;

	public CustomFunctionSegment()
	{
		m_parameterList = new ArrayList<Segment>();
	}

	public CustomFunctionSegment(List<Segment> parameters)
	{
		m_parameterList = parameters;
	}

	public void setFunctionId(String functionId)
	{
		m_functionId = functionId;
	}

	public String getFunctionId()
	{
		return m_functionId;
	}

	public void setRecordedValue(Segment recValue)
	{
		m_recordedValue = recValue;
	}

	@Override
	public byte[] evaluateBytes(Variables variables, SegmentParserOptions options)
	{
		CustomFunction function = SegmentParser.getCustomTransformFunction(m_functionId);
		if (function != null) {
			//evaluate parameters first
			List<String> para_value_list = evaluateParameters(variables, options);
			
			String value = BinaryUtil.binary2HexString(function.compute(variables, para_value_list, options), false);
			return BinaryUtil.hexString2Binary(value);
		}
		else {
			return "UNSUPPORTED_FUNCTION".getBytes();
		}
	}

	@Override
	public String evaluateString(Variables variables, SegmentParserOptions options)
	{
		CustomFunction function = SegmentParser.getCustomTransformFunction(m_functionId);
		if (function != null) {
			//evaluate parameters first
			List<String> para_value_list = evaluateParameters(variables, options);
			
			try {
				return function.computeString(variables, para_value_list, options);
			} catch (Exception ex) {
				return new String(function.compute(variables, para_value_list, options));
			}
		}
		else {
			return "UNSUPPORTED_FUNCTION";
		}
	}
	
	private List<String> evaluateParameters(Variables variables, SegmentParserOptions options) 			
	{
		List<String> para_value_list = null;
		if (m_parameterList != null) {
			para_value_list = new ArrayList<String>();
			for (Segment seg : m_parameterList) {
				String param = seg.evaluateString(variables, options);
				param = normalizeParameter(param);
				para_value_list.add(param);
			}
		}
		
		return para_value_list;
	}

	/* justify single quotes around parameter value and white spaces
	 * before and after single quoted parameters
	 */
	private String normalizeParameter(String param) 
	{
		if(param == null || param.length()==0)
			return null;
		
		String trimParam = param.trim();
		if(trimParam.length() != 0){				
			if((int)trimParam.charAt(0) == 39 && (int)trimParam.charAt(trimParam.length() -1) == 39){
				//remove apostrophe
				param = trimParam.substring(1, trimParam.length() -1);
			}
		}
		return param;
	}

	@Override
	public void addSubSegment(Segment subSeg)
	{
		m_parameterList.add(subSeg);
	}

	public List<Segment> getParameters()
	{
		return m_parameterList;
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
		try {
			//In treeView UI, we don't register custom functions.So just display what it shown in properties dialog.
			//evaluate parameters
			if (m_parameterList != null) {
				StringBuilder paramBuilder = new StringBuilder();
				paramBuilder.append("("); //$NON-NLS-1$
				boolean isFirstParam = true;
				for (Segment seg : m_parameterList) {
					if (!isFirstParam) {
						paramBuilder.append(","); //$NON-NLS-1$
					}
					else
						isFirstParam = false;
					paramBuilder.append(seg.evaluateString(null, new SegmentParserOptions()));
				}
				paramBuilder.append(")"); //$NON-NLS-1$
				
				return SegmentParser.DOUBLE_LBB_FUNCTION_PREFIX + m_functionId + paramBuilder.toString() + SegmentParser.DOUBLE_RBB;
			}
		}
		catch (Exception e) {
			//ignore
		}
		return m_recordedValue != null ? m_recordedValue.getDisplayValue() : m_functionId;
	}

	@Override
	public String getCurrentValue()
	{
		//In treeView UI, we don't register custom functions.So just display what it shown in properties dialog.
		//evaluate parameters
		if (m_parameterList != null) {
			StringBuilder paramBuilder = new StringBuilder();
			paramBuilder.append("("); //$NON-NLS-1$
			boolean isFirstParam = true;
			for (Segment seg : m_parameterList) {
				if (!isFirstParam) {
					paramBuilder.append(","); //$NON-NLS-1$
				}
				else
					isFirstParam = false;
				paramBuilder.append(seg.getCurrentValue());
			}
			paramBuilder.append(")"); //$NON-NLS-1$
			
			if (m_recordedValue == null)
				return SegmentParser.DOUBLE_LBB_FUNCTION_PREFIX + m_functionId + paramBuilder.toString() + SegmentParser.DOUBLE_RBB;
			else
				return SegmentParser.DOUBLE_LBB_FUNCTION_PREFIX + m_functionId + paramBuilder.toString() + ',' + m_recordedValue.getCurrentValue() + SegmentParser.DOUBLE_RBB;
		}
		
		if (m_recordedValue == null)
			return SegmentParser.DOUBLE_LBB_FUNCTION_PREFIX + m_functionId + SegmentParser.DOUBLE_RBB;
		else
			return SegmentParser.DOUBLE_LBB_FUNCTION_PREFIX + m_functionId + ',' + m_recordedValue.getCurrentValue() + SegmentParser.DOUBLE_RBB;
	}
	
	private List<String> translateParameters(Map<String,String> encodeOptions) 			
	{
		List<String> para_value_list = null;
		if (m_parameterList != null) {
			para_value_list = new ArrayList<String>();
			for (Segment seg : m_parameterList) {
				String param = seg.translate(encodeOptions);
				param = normalizeParameter(param);
				para_value_list.add(param);
			}
		}
		
		return para_value_list;
	}

	@Override
	public String translate(Map<String,String> encodeOptions) {
		CustomFunction function = SegmentParser.getCustomTransformFunction(m_functionId);
		if (function != null) {
			//evaluate parameters first
			List<String> para_value_list = translateParameters(encodeOptions);
			
			try {
				return function.translate(para_value_list, encodeOptions);
			} catch (Exception ex) {
				return "FUNC_TRANSLATE_ERROR";
			}
		}
		else {
			return "UNSUPPORTED_FUNCTION";
		}
	}
}
