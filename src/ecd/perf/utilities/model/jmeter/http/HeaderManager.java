package ecd.perf.utilities.model.jmeter.http;

import java.util.ArrayList;
import java.util.List;

import ecd.perf.utilities.model.jmeter.AbstractModelElement;

public class HeaderManager extends AbstractModelElement {
	private static final long serialVersionUID = 1L;
	private final List<HeaderEntry> headers;
	public HeaderManager() {
		super("HTTP Header Manager");
		headers = new ArrayList<HeaderEntry>();
		this.put("HeaderManager.headers", headers);
	}
	@Override
	public List<Object> getChildren() {
		return null;
	}

	@Override
	public String getTagName() {
		return "HeaderManager";
	}

	@Override
	public String getTestclass() {
		return "HeaderManager";
	}

	@Override
	public String getUIClass() {
		return "HeaderPanel";
	}

	@Override
	public boolean isPrintEmptyHashTree() {
		return true;
	}
	
	public void addHeader(String name, String value){
		HeaderEntry header = new HeaderEntry(name, value);
		headers.add(header);
	}

}
