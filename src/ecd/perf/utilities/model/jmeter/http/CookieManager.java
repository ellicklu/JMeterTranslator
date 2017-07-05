package ecd.perf.utilities.model.jmeter.http;

import java.util.ArrayList;
import java.util.List;

import ecd.perf.utilities.model.jmeter.AbstractModelElement;
public class CookieManager extends AbstractModelElement {

	private static final long serialVersionUID = 1L;

	private final List<Object> cookies;
	public CookieManager() {
		super("HTTP Cookie Manager");
		this.put("CookieManager.policy", "standard");
		this.put("CookieManager.implementation", "org.apache.jmeter.protocol.http.control.HC4CookieHandler");
		this.put("CookieManager.clearEachIteration", true);
		cookies = new ArrayList<Object>();
		this.put("CookieManager.cookies", cookies);
	}

	@Override
	public List<Object> getChildren() {
		return null;
	}

	@Override
	public String getTagName() {
		return "CookieManager";
	}

	@Override
	public String getTestclass() {
		return "CookieManager";
	}

	@Override
	public String getUIClass() {
		return "CookiePanel";
	}

	@Override
	public boolean isPrintEmptyHashTree() {
		return true;
	}
	
	public void addCokkie(String name, String value, String domain, String path, long expire, boolean isSecure){
		CookieEntry entry = new CookieEntry(name,	value, domain, path, isSecure, expire);
		cookies.add(entry);
	}

}
