import org.apache.oro.text.regex.*;
import org.apache.jmeter.util.JMeterUtils;
String regex = "addBrowserIdToURL\\(['|\"][^'^\"]+__dmfRequestId\\\\x3D(.+?)\\\\x26";
Pattern pattern = JMeterUtils.getPatternCache().getPattern(regex, Perl5Compiler.READ_ONLY_MASK);
Perl5Matcher matcher = JMeterUtils.getMatcher();
List<MatchResult> matches = new ArrayList();
PatternMatcherInput input = new PatternMatcherInput(prev.getResponseDataAsString());
if (matcher.contains(input, pattern)) {
	String raw = matcher.getMatch().group(1);
	String decoded = URLDecoder.decode(raw.replaceAll("\\\\x", "%"), "UTF-8");	
	vars.put("CUST_clientReqID",decoded);
	//log.info("log:"+vars.get("CUST_clientReqID"));
}