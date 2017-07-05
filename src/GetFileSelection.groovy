import org.apache.oro.text.regex.*;
import org.apache.jmeter.util.JMeterUtils;
String fname = vars.get("reportFileName");
String status = "Placeholder";

Perl5Matcher matcher1 = JMeterUtils.getMatcher();
PatternMatcherInput input1 = new PatternMatcherInput(prev.getResponseDataAsString());
String regexStatus = "<div class='dmfBody_7'>\\s+(.+?)\\s+</div>";
Pattern patternStatus = JMeterUtils.getPatternCache().getPattern(regexStatus, Perl5Compiler.READ_ONLY_MASK);
ArrayList<String> filestatus= new ArrayList<String>();
//matcher.setMultiline(false);
while (matcher1.contains(input1, patternStatus)) {
	if(matcher1.getMatch().groups() > 0) {
		filestatus.add(matcher1.getMatch().group(1));
		log.info("status:"+matcher1.getMatch().group(1));
	}
}

Perl5Matcher matcher2 = JMeterUtils.getMatcher();
PatternMatcherInput input2 = new PatternMatcherInput(prev.getResponseDataAsString());
String regexNames = "name='dnd:ObjectList_[0-9]+' title=\"(.+?)\"";
Pattern patternNames = JMeterUtils.getPatternCache().getPattern(regexNames, Perl5Compiler.READ_ONLY_MASK);
ArrayList<String> filenames= new ArrayList<String> ();
while (matcher2.contains(input2, patternNames)) {
	if(matcher2.getMatch().groups() > 0) {
		filenames.add(matcher2.getMatch().group(1));
		log.info("name:"+matcher2.getMatch().group(1));
	}
}


String selection="0";
if (filestatus.size() == 0 || filenames.size() == 0 || filestatus.size() != filenames.size()) {
	
} else {
  for (int i=0; i < filestatus.size(); i++) {
    if (status.equalsIgnoreCase(filestatus.get(i)) && fname.equals(filenames.get(i))) {
      StringBuilder sbld=new StringBuilder(20);
      for (int m=0; m < filestatus.size(); m++) {
        if (m == i) {
          sbld.append('1');
        }
        else {
          sbld.append('0');
        }
      }
      selection=sbld.toString();
      break;
    }
  }
}
vars.put("varSelection",selection);
log.info("varSelection:"+selection);