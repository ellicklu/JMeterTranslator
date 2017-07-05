package ecd.perf.utilities.model.jmeter.http;

/**
 * @author lue5
 *
 */
public class CookieEntry {
	private String name;
	private String value;
	private String domain;
	private String path;
	private boolean secure;
	private long expires;
	private boolean pathSpecified;
	private boolean domainSpecified;
	
	public CookieEntry(String name,	String value, String domain, String path, boolean secure, long expires){
		this.name = name;
		this.value = value;
		this.domain = domain;
		this.path = path;
		this.secure = secure;
		this.expires = expires;
		this.pathSpecified = (path != null && path.length() > 0) ? true : false;
		this.domainSpecified =  (domain != null && domain.length() > 0) ? true : false;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getDomain() {
		return domain;
	}

	public String getPath() {
		return path;
	}

	public boolean isSecure() {
		return secure;
	}

	public long getExpires() {
		return expires;
	}

	public boolean isPathSpecified() {
		return pathSpecified;
	}

	public boolean isDomainSpecified() {
		return domainSpecified;
	}


}
