package ecd.perf.utilities.expression.lr;

import java.util.HashMap;
import java.util.Map;

import ecd.perf.utilities.expression.ats.SegmentParserOptions;
import ecd.perf.utilities.expression.seg.Segment;
import ecd.perf.utilities.expression.seg.Variables;
import ecd.perf.utilities.model.jmeter.CSVDataSet;

public class LRTransforms {
	
	public static void main(String args[]) throws Throwable {
//		String url = "http://{host}:{port}/cp-rest-web/repositories/{repo}/documents/{r_object_id_ext}/maincontent";
//		Segment seg = LRSegmentParserHelper.getInstance().parse(url);
		String url2 = "http://{host_{port}}/cp-rest-web/repositories/{repo}/documents/{r_object_id_ext}/maincontent";
		Segment seg2 = LRSegmentParserHelper.getInstance().parse(url2, null);
		System.out.println(seg2);
	}
	public static String transform(String data, Variables variables, boolean useRecordedValue, boolean useDatabank, boolean urlEncode, String charset)
	{
		if (data == null)
			return null;
		if (data.length() == 0)
			return ""; //$NON-NLS-1$
		try {
			Segment segment = LRSegmentParserHelper.getInstance().parse(data, null);
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
			Segment segment = LRSegmentParserHelper.getInstance().parse(data, databanks);
			return segment.translate(encodeOptions);
		} catch (Exception e) {
			return data;
		}
	}
}
