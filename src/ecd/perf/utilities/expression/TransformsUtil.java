package ecd.perf.utilities.expression;

import java.util.HashMap;
import java.util.Map;

import ecd.perf.utilities.expression.ats.Transforms;
import ecd.perf.utilities.expression.lr.LRTransforms;
import ecd.perf.utilities.expression.seg.Variables;
import ecd.perf.utilities.model.jmeter.CSVDataSet;



public class TransformsUtil
{
	public static final int ATSScript = 0;
	public static final int LRScript = 1;
	private static int scripttype = ATSScript;
	private final static Variables variables = new Variables();
	public static void setScriptType(int type) {
		scripttype = type;
	}
	
	public static void putPredefinedVariables(HashMap<String, String> vars) {
		for(String key : vars.keySet()) {
			variables.set(key, vars.get(key));
		}
	}
	
	public static String transform(String data, boolean useRecordedValue, boolean useDatabank)
	{
		return transform(data, variables, useRecordedValue, useDatabank, false, null);
	}
	public static String transform(String data, Variables variables, boolean useRecordedValue, boolean useDatabank, boolean urlEncode, String charset)
	{
		switch(scripttype){
			case ATSScript:
				return Transforms.transform(data, variables, useRecordedValue, useDatabank, urlEncode, charset);
			case LRScript:
				return LRTransforms.transform(data, variables, useRecordedValue, useDatabank, urlEncode, charset);
		}
		return null;
	}
	
	public static String translate(String data, Map<String,String> encodeOptions, final HashMap<String, CSVDataSet> databanks)
	{
		switch(scripttype){
			case ATSScript:
				return Transforms.translate(data, encodeOptions, databanks);
			case LRScript:
				return LRTransforms.translate(data, encodeOptions, databanks);
		}
		return null;
	}
}
