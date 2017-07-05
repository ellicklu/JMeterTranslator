package ecd.perf.utilities.model.jmeter.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ecd.perf.utilities.expression.TransformsUtil;
import ecd.perf.utilities.main.AbstractScriptLoader;
import ecd.perf.utilities.main.HostInfo;
import ecd.perf.utilities.model.jmeter.AbstractModelElement;
import ecd.perf.utilities.model.jmeter.ElementProperties;
import ecd.perf.utilities.model.jmeter.timer.ConstantTimer;

public class HTTPSampler extends AbstractModelElement {
	private static final long serialVersionUID = 1L;
	public static final String METHOD_POST = "POST";
	public static final String METHOD_GET = "GET";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_DELETE = "DELETE";
	public static final String PROTOCOL_HTTP = "http";
	public static final String PROTOCOL_HTTPS = "https";
	private final ElementProperties arguments;
	private final HeaderManager headerManager;
	private final List<Object> postProcessors;
	private ConstantTimer timer = null;

	public HTTPSampler(String testname, String domain, String port,
			String protocol, String encoding, String path, String method, boolean isMultiPost) {
		super(testname);
		headerManager = new HeaderManager();
		arguments = new ElementProperties(ElementProperties.guiclassHTTPArguments);
		// arguments.addProperty("abc","def","=");
		this.put("HTTPsampler.Arguments", arguments);
		
		postProcessors = new ArrayList<Object>();
		initProps(domain, port, protocol, encoding, path, method, isMultiPost);
	}
	
	public HTTPSampler(int id, String url, String encoding, String method, boolean isMultiPost, Map<String, String> encodeOptions, final AbstractScriptLoader loader){
		super(id+'.'+getTestName(url, loader));
		headerManager = new HeaderManager();
		arguments = new ElementProperties(ElementProperties.guiclassHTTPArguments);
		this.put("HTTPsampler.Arguments", arguments);
		
		postProcessors = new ArrayList<Object>();
		URL urlObj;
		try {
			String transformedUrl = TransformsUtil.transform(url, true, false);
			urlObj = new URL(transformedUrl);
			String hostUrlWithPort = urlObj.getProtocol()+"://"+urlObj.getHost()+":"+urlObj.getPort();
			String hostUrl = urlObj.getProtocol()+"://"+urlObj.getHost();
			String path;
			if(url.contains(hostUrlWithPort)) {
				path = TransformsUtil.translate(url.substring(hostUrlWithPort.length()), encodeOptions, loader.getDatabanks());
			} else if(url.contains(hostUrl)) {
				path = TransformsUtil.translate(url.substring(hostUrl.length()), encodeOptions, loader.getDatabanks());
			} else if(transformedUrl.contains("?")) {
				String tempPath = urlObj.getPath() + transformedUrl.substring(transformedUrl.indexOf('?'));
				if(url.contains(","+tempPath+"}}")) {
					int pathIndex = url.indexOf("{{", url.indexOf("}}"));
					path = TransformsUtil.translate(url.substring(pathIndex), encodeOptions, loader.getDatabanks());
				} else {
					path = tempPath;
				}
			} else {
				if(urlObj.getPath() != null && urlObj.getPath().contains("DATABANK_VAL_NOT_FOUND")) {
					int pathIndex = url.indexOf('/', url.indexOf("//")+2);
					path = TransformsUtil.translate(url.substring(pathIndex), encodeOptions, loader.getDatabanks());
				} else {
					path = urlObj.getPath();
				}
			}
			String domain = urlObj.getHost();
			String port = String.valueOf(urlObj.getPort());
			String hostPort = domain+":"+port;
			HostInfo mappedHost = loader.getHostMap().get(hostPort);
			if(mappedHost != null){
				//donothing
			} else {
				mappedHost = new HostInfo();
				mappedHost.setHost(domain);
				mappedHost.setPort(port);
				mappedHost.setAlias(String.valueOf(loader.getHostMap().keySet().size()+1));
				loader.getHostMap().put(hostPort, mappedHost);
			}
			initProps(
					//urlObj.getHost(),
					"${HOST"+mappedHost.getAlias()+"}",
					//String.valueOf(urlObj.getPort()),
					"${PORT"+mappedHost.getAlias()+"}",
					urlObj.getProtocol(), 
					encoding, 
					path, 
					method, 
					isMultiPost);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public void addHeader(String name, String value){
		headerManager.addHeader(name, value);
	}
	
	public void addParam(String name, String value) {
		arguments.addParameter(name, value);
	}
	
	public void addParam(String name) {
		arguments.addParameter(name);
	}
	
	public void addPostProcessor(AbstractModelElement postProcessor) {
		postProcessors.add(postProcessor);
	}
	
	private static String getTestName(String url, AbstractScriptLoader loader) {
    	String translatedUrl = TransformsUtil.transform(url, true, false);
		int lastSlash = translatedUrl.lastIndexOf('/');
		if(lastSlash >= 0 && lastSlash < translatedUrl.length() - 1) {
			String lastName = translatedUrl.substring(lastSlash+1);
			if(lastName.contains(".")) {
				lastName = lastName.substring(0, lastName.indexOf("."));
			}
			if(lastName.contains("?")) {
				lastName = lastName.substring(0, lastName.indexOf("?"));
			}
			return lastName;
		}
		return "Empty Name";
	}
	
	private void initProps(String domain, String port, String protocol, String encoding, String path, String method, boolean isMultiPost){
		this.put("HTTPSampler.domain", domain);
		this.put("HTTPSampler.port", port);
		this.put("HTTPSampler.connect_timeout", "");
		this.put("HTTPSampler.response_timeout", "");
		this.put("HTTPSampler.protocol", protocol);
		this.put("HTTPSampler.contentEncoding", encoding);
		this.put("HTTPSampler.path", path);
		this.put("HTTPSampler.method", method);
		this.put("HTTPSampler.follow_redirects", true);
		this.put("HTTPSampler.auto_redirects", false);
		this.put("HTTPSampler.use_keepalive", true);
		this.put("HTTPSampler.DO_MULTIPART_POST", isMultiPost);
		this.put("HTTPSampler.monitor", false);
		this.put("HTTPSampler.embedded_url_re", "");
	}
	
	public void setTimer(ConstantTimer timer) {
		this.timer = timer;
	}

	@Override
	public List<Object> getChildren() {
		postProcessors.add(0, headerManager);
		if(timer != null)
			postProcessors.add(timer);
		return postProcessors;
	}

	@Override
	public String getTagName() {
		return "HTTPSamplerProxy";
	}

	@Override
	public String getTestclass() {
		return "HTTPSamplerProxy";
	}

	@Override
	public String getUIClass() {
		return "HttpTestSampleGui";
	}

	@Override
	public boolean isPrintEmptyHashTree() {
		return true;
	}

}
