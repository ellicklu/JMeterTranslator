package ecd.perf.utilities.expression.function;

import java.util.List;
import java.util.Map;

import ecd.perf.utilities.expression.*;
import ecd.perf.utilities.expression.ats.SegmentParserOptions;
import ecd.perf.utilities.expression.seg.Variables;

public class JSRandomTokenFunction extends CustomFunction
{	
	@Override
	public String getId()
	{
		return "jsRandomToken"; //$NON-NLS-1$
	}

	private static final String PARAM_TOKEN = "token"; //$NON-NLS-1$
	
	@Override
	public byte[] compute(Variables variables, List<String> customArguments, SegmentParserOptions options)
	{
		if (customArguments == null || customArguments.size() == 0) {
			return null;
		}
		String token = customArguments.get(0);
		
		int randomOffset = getRandomInt() % 9 + 1;
		StringBuffer sb = new StringBuffer();
		sb.append(randomOffset);
		sb.append(token.substring(0, randomOffset));
		sb.append(getRandomInt() % 7);
		sb.append(token.substring(randomOffset, token.length() - randomOffset));
		sb.append(getRandomInt() % 5);
		sb.append(token.substring(token.length() - randomOffset, token.length()));
		
		return sb.toString().getBytes();
	}
	
	private int getRandomInt()
	{
		return (int) Math.floor(Math.random() * 10);
	}
	
	public static String revertString(String randomString)
	{
		if (!isValidRandomString(randomString)) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		int offset = randomString.charAt(0) - '0';
		String firstPart = randomString.substring(1, offset + 1);
		sb.append(firstPart);
		String secondPart = randomString.substring(offset + 2,randomString.length() - offset -1);
		sb.append(secondPart);
		String thirdPart = randomString.substring(randomString.length() - offset);
		sb.append(thirdPart);
		return sb.toString();
	}
	
	private static boolean isValidRandomString(String randomString)
	{
		if (randomString == null || randomString.isEmpty()) {
			return false;
		}
		int offset = randomString.charAt(0) - '0';
		if (offset < 1 || offset > 9) {
			return false;
		}
		int secondRandom = randomString.charAt(offset + 1) - '0';
		if (secondRandom < 0 || secondRandom > 6) {
			return false;
		}
		int thirdRandom = randomString.charAt(randomString.length() - offset - 1) - '0';
		if (thirdRandom < 0 || thirdRandom > 4) {
			return false;
		}
		return true;

	}

	@Override
	public String translate(List<String> paraValues, Map<String,String> encodeOptions) {
		String val = paraValues.get(0);
		int valLen = val.length();
		
		return "${__RandomString("+valLen+","+translateWithEscapeParam(val)+")}";
	}
	
}
