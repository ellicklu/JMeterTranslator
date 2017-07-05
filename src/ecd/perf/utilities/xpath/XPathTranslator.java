package ecd.perf.utilities.xpath;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XPathTranslator {
	private final static String REGEX_PATTERN_INPUT_STR = "\\.//INPUT\\[@name='([^']+)'\\]/@value";
	private final static String REGEX_PATTERN_FORM_STR = "\\.//FORM\\[@name='([^']+)'\\]/@action";
	private final static String REGEX_PATTERN_FRAME_STR = "\\.//FRAME\\[@name='([^']+)'\\]/@src";
	private final static String REGEX_PATTERN_FRAME_STR2 = "\\.//FRAME\\[@title='([^']+)'\\]/@src";
	private final static String REGEX_PATTERN_OPTION_STR = "\\.//OPTION\\[text\\(\\)='([^']+)'\\]/@value";
	private final static String REGEX_PATTERN_OPTION_STR2 = "\\.//OPTION\\[@name='([^']+)'\\]/@value";
	private final static String REGEX_PATTERN_LINK_STR = "\\.//A\\[text\\(\\)='([^']+)'\\]/@href";
	
	private final static Pattern REGEX_PATTERN_INPUT = Pattern.compile(REGEX_PATTERN_INPUT_STR);
	private final static Pattern REGEX_PATTERN_FORM = Pattern.compile(REGEX_PATTERN_FORM_STR);
	private final static Pattern REGEX_PATTERN_FRAME = Pattern.compile(REGEX_PATTERN_FRAME_STR);
	private final static Pattern REGEX_PATTERN_FRAME2 = Pattern.compile(REGEX_PATTERN_FRAME_STR2);
	private final static Pattern REGEX_PATTERN_OPTION = Pattern.compile(REGEX_PATTERN_OPTION_STR);
	private final static Pattern REGEX_PATTERN_OPTION2 = Pattern.compile(REGEX_PATTERN_OPTION_STR2);
	private final static Pattern REGEX_PATTERN_LINK = Pattern.compile(REGEX_PATTERN_LINK_STR);
	
	public static String translateXPathToRegex(String input){
		//match patterns
		Matcher matchInput = REGEX_PATTERN_INPUT.matcher(input);
		if(matchInput.matches()){
			String identifier = matchInput.group(1);
			return "<input[^>]+name=['\"]?"+(identifier)+"['\"]?[^>]+value=['\"]?([^'\"> ]+)['\"]?";
		}
		
		matchInput = REGEX_PATTERN_FORM.matcher(input);
		if(matchInput.matches()){
			String identifier = matchInput.group(1);
			return "<form[^>]+name=['\"]?"+(identifier)+"['\"]?[^>]+action=['\"]?([^'\"> ]+)['\"]?";
		}
		
		matchInput = REGEX_PATTERN_FRAME.matcher(input);
		if(matchInput.matches()){
			String identifier = matchInput.group(1);
			return "<frame[^>]+name=['\"]?"+(identifier)+"['\"]?[^>]+src=['\"]?([^'\"> ]+)['\"]?";
		}
		matchInput = REGEX_PATTERN_FRAME2.matcher(input);
		if(matchInput.matches()){
			String identifier = matchInput.group(1);
			return "<frame[^>]+title=['\"]?"+(identifier)+"['\"]?[^>]+src=['\"]?([^'\"> ]+)['\"]?";
		}
		matchInput = REGEX_PATTERN_OPTION.matcher(input);
		if(matchInput.matches()){
			String identifier = matchInput.group(1);
			return "<option[^>]+['\"]?[^>]+value=['\"]?([^'\"> ]+)['\"]?[^>]+>"+(identifier)+"<";
		}
		
		matchInput = REGEX_PATTERN_OPTION2.matcher(input);
		if(matchInput.matches()){
			String identifier = matchInput.group(1);
			return "<option[^>]+name=['\"]?"+(identifier)+"['\"]?[^>]+value=['\"]?([^'\"> ]+)['\"]?";
		}
		
		matchInput = REGEX_PATTERN_LINK.matcher(input);
		if(matchInput.matches()){
			String identifier = matchInput.group(1);
			return "<a[^>]+['\"]?[^>]+href=['\"]?([^'\"> ]+)['\"]?[^>]+>"+(identifier)+"<";
		}
		return null;
	}
}
