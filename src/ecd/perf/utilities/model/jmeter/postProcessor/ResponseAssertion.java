package ecd.perf.utilities.model.jmeter.postProcessor;

import java.util.ArrayList;
import java.util.List;

public class ResponseAssertion extends PostProcessor {
	private static final long serialVersionUID = 1L;
	private final List<Object> assertions = new ArrayList<Object>();
	public ResponseAssertion(List<String> asserts) {
		super("Response Assertion");
		
		for(String assertString : asserts) {
			assertions.add(assertString);
		}
		this.put("Asserion.test_strings", assertions);
		this.put("Assertion.test_field", "Assertion.response_data_as_document");
		this.put("Assertion.assume_success", false);
		this.put("Assertion.test_type", 2);
		this.put("Assertion.scope", "all");
		
	}

	@Override
	public List<Object> getChildren() {
		return null;
	}

	@Override
	public String getTagName() {
		return "ResponseAssertion";
	}

	@Override
	public String getTestclass() {
		return "ResponseAssertion";
	}

	@Override
	public String getUIClass() {
		return "AssertionGui";
	}

	@Override
	public boolean isPrintEmptyHashTree() {
		return true;
	}

}
