/**
 * Copyright 1997-2009 Oracle All rights reserved.
 */
package ecd.perf.utilities.expression.ats;

import java.util.HashMap;
import java.util.Map;

import ecd.perf.utilities.expression.CustomFunction;
import ecd.perf.utilities.expression.function.*;
import ecd.perf.utilities.expression.seg.Segment;
import ecd.perf.utilities.expression.seg.Variables;
import ecd.perf.utilities.model.jmeter.CSVDataSet;

public class Transforms
{
	static {
		SegmentParser.addCustomTransformFunction(new VariableIncrementerFunction());
		SegmentParser.addCustomTransformFunction(new TimestampFunction());
		SegmentParser.addCustomTransformFunction(new EncodeFunction());
		SegmentParser.addCustomTransformFunction(new DecodeFunction());
		SegmentParser.addCustomTransformFunction(new TodayFunction());
		SegmentParser.addCustomTransformFunction(new JSTRFunction());
		SegmentParser.addCustomTransformFunction(new LenFunction());
		SegmentParser.addCustomTransformFunction(new VUIdFunction());
		SegmentParser.addCustomTransformFunction(new SessionNameFunction());
		SegmentParser.addCustomTransformFunction(new IterationNumFunction());
		SegmentParser.addCustomTransformFunction(new HostIPFunction());
		SegmentParser.addCustomTransformFunction(new HostNameFunction());
		SegmentParser.addCustomTransformFunction(new TimestampSecsFunction());
		SegmentParser.addCustomTransformFunction(new RandomFunction());
        SegmentParser.addCustomTransformFunction(new RandomPerIterationFunction());
		SegmentParser.addCustomTransformFunction(new DecryptFunction());
		SegmentParser.addCustomTransformFunction(new EncryptFunction());
		SegmentParser.addCustomTransformFunction(new DeobfuscateFunction());
		SegmentParser.addCustomTransformFunction(new ObfuscateFunction());
		SegmentParser.addCustomTransformFunction(new TopLevelStepNameFunction());
		SegmentParser.addCustomTransformFunction(new ProductVersionFunction());
		SegmentParser.addCustomTransformFunction(new OSVersionFunction());
		SegmentParser.addCustomTransformFunction(new OSNameFunction());
		SegmentParser.addCustomTransformFunction(new GUIDFunction());
		SegmentParser.addCustomTransformFunction(new JSRandomTokenFunction());
		SegmentParser.addCustomTransformFunction(new ResourceFileFunction());
	}

	/**
	 * Transform a string, substituting the values of variables where applicable.
	 * All recorded values specified for referenced variables are ignored by this method.
	 * @param data String containing transform syntax to transform.
	 * @param variables Variables collection to use when substituting variables into the given data String.
	 * @return Fully transformed string of data. String is encoded using the platform's default charset.
	 * @throws AbstractScriptException if an exception occurs parsing or filling the transformed data.
	 */
	public static String transform(String data, Variables variables, final HashMap<String, CSVDataSet> databanks)
	{
		return transform(data, variables, false, databanks);
	}
	
	/**
	 * Transform a string, substituting the values of variables where applicable.
	 * @param data String containing transform syntax to transform.
	 * @param variables Variables collection to use when substituting variables into the given data String.
	 * @param useRecordedValue If false and a referenced variable cannot be found, this method throws a
	 * 			VariableNotFoundException.
	 * 			If true and a referenced variable is not found, its recorded value (if available)
	 * 			will be substituted into the string and no VariableNotFoundException will be thrown.
	 * @return Fully transformed string of data. String is encoded using the platform's default charset.
	 * @throws AbstractScriptException if an exception occurs parsing or filling the transformed data.
	 */
	public static String transform(String data, Variables variables, boolean useRecordedValue, final HashMap<String, CSVDataSet> databanks)
	{
		return transform(data, variables, useRecordedValue, true, databanks);
	}
	
	/**
	 * Transform a string, substituting the values of variables where applicable.
	 * @param data String containing transform syntax to transform.
	 * @param variables Variables collection to use when substituting variables into the given data String.
	 * @param useRecordedValue If false and a referenced variable cannot be found, this method throws a
	 * 			VariableNotFoundException.
	 * 			If true and a referenced variable is not found, its recorded value (if available)
	 * 			will be substituted into the string and no VariableNotFoundException will be thrown.
	 * @param useDatabank If true and variable cannot be found, this method throws a
	 * 			VariableNotFoundException.
	 * 			If false and a referenced variable is not found and this variable is databank variable, 
	 * 			its recorded value (if available)will be substituted into the string and no VariableNotFoundException will be thrown.
	 * 			But if no recorded value, still throw a VariableNotFoundException.
	 * @return Fully transformed string of data. String is encoded using the platform's default charset.
	 * @throws AbstractScriptException if an exception occurs parsing or filling the transformed data.
	 */
	public static String transform(String data, Variables variables, boolean useRecordedValue, boolean useDatabank, final HashMap<String, CSVDataSet> databanks)
	{
		if (data == null)
			return null;
		if (data.length() == 0)
			return ""; //$NON-NLS-1$
		try {
			Segment segment = SegmentParserHelper.getInstance().parse(data, databanks);
			SegmentParserOptions option = new SegmentParserOptions();
			option.setUseRecordedValue(useRecordedValue);
			option.setUseDatabank(useDatabank);
			return segment.evaluateString(variables, option);
		} catch (Exception e) {
			return data;
		}
	}
	
	/**
	 * Transform a string, substituting the values of variables where applicable.
	 * @param data String containing transform syntax to transform.
	 * @param variables Variables collection to use when substituting variables into the given data String.
	 * @param useRecordedValue If false and a referenced variable cannot be found, this method throws a
	 * 			VariableNotFoundException.
	 * 			If true and a referenced variable is not found, its recorded value (if available)
	 * 			will be substituted into the string and no VariableNotFoundException will be thrown.
	 * @param useDatabank If true and variable cannot be found, this method throws a
	 * 			VariableNotFoundException.
	 * 			If false and a referenced variable is not found and this variable is databank variable, 
	 * 			its recorded value (if available)will be substituted into the string and no VariableNotFoundException will be thrown.
	 * 			But if no recorded value, still throw a VariableNotFoundException.
	 * @param urlEncode If true, the result string of each variable segment will be url-encoded.
	 * @return Fully transformed string of data. String is encoded using the platform's default charset.
	 * @throws AbstractScriptException if an exception occurs parsing or filling the transformed data.
	 */
	public static String transform(String data, Variables variables, boolean useRecordedValue, boolean useDatabank, boolean urlEncode, String charset)
	{
		if (data == null)
			return null;
		if (data.length() == 0)
			return ""; //$NON-NLS-1$
		try {
			Segment segment = SegmentParserHelper.getInstance().parse(data, null);
			SegmentParserOptions option = new SegmentParserOptions();
			option.setUseRecordedValue(useRecordedValue);
			option.setUseDatabank(useDatabank);
			option.setUseUrlEncode(urlEncode);
			option.setCharset(charset);
			return segment.evaluateString(variables, option);
		} catch (Exception e) {
			return data;
		}
	}
	
	public static String translate(String data, Map<String,String> encodeOptions, final HashMap<String, CSVDataSet> databanks) {
		if (data == null)
			return null;
		if (data.length() == 0)
			return ""; //$NON-NLS-1$
		try {
			Segment segment = SegmentParserHelper.getInstance().parse(data, databanks);
			return segment.translate(encodeOptions);
		} catch (Exception e) {
			return data;
		}
	}
	
	/**
	 * Transform a string, substituting the values of variables where applicable.
	 * All recorded values specified for referenced variables are ignored by this method.
	 * @param data String containing transform syntax to transform.
	 * @param variables Variables collection to use when substituting variables into the given data String.
	 * @return Fully transformed raw byte array.
	 * @throws AbstractScriptException if an exception occurs parsing or filling the transformed data.
	 */
	public static byte[] transformBytes(String data, Variables variables, final HashMap<String, CSVDataSet> databanks)
	{
		if (data == null)
			return null;
		if (data.length() == 0)
			return new byte[0];
		Segment segment;
		try {
			segment = SegmentParserHelper.getInstance().parse(data, databanks);
			return segment.evaluateBytes(variables, new SegmentParserOptions());
		} catch (Exception e) {
			return data.getBytes();
		}
	}
	
	/**
	 * Add user defined custom transform function to the function map, so that the function, with its function 
	 * id denoted with @, is recognized during transform. 
	 * @param function The user defined custom function to add to the custom function map
	 */
	public static void addCustomTransformFunction(CustomFunction function)
	{
		SegmentParser.addCustomTransformFunction(function);
	}
}
