package ecd.perf.utilities.model.jmeter.postProcessor;

import java.util.List;

public class RegexExtractor extends PostProcessor {

	public RegexExtractor(String varName, String regEx, String template, String recVal, String matchIdx, boolean isFromHeader) {
		super(varName);
		this.put("RegexExtractor.useHeaders",String.valueOf(isFromHeader));
		this.put("RegexExtractor.refname",varName);
		this.put("RegexExtractor.regex",regEx);
		this.put("RegexExtractor.template",template);
		this.put("RegexExtractor.default",recVal);
		this.put("RegexExtractor.match_number",String.valueOf(matchIdx));
		this.put("Sample.scope","all");
	}

	@Override
	public List<Object> getChildren() {
		return null;
	}

	@Override
	public String getTagName() {
		return "RegexExtractor";
	}

	@Override
	public String getTestclass() {
		return "RegexExtractor";
	}

	@Override
	public String getUIClass() {
		return "RegexExtractorGui";
	}

	@Override
	public boolean isPrintEmptyHashTree() {
		return true;
	}

}
