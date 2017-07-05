package ecd.perf.utilities.model.jmeter.listener;

import java.util.List;

import ecd.perf.utilities.model.jmeter.ElementProperties;

public class BackendListener extends AbstractJmeterListener {
	private static final long serialVersionUID = 1L;
	private final ElementProperties arguments;
	public BackendListener(String graphiteHost, String graphitePort, String scnName) {
		super("Backend Listener");
		arguments = new ElementProperties(ElementProperties.guiclassArguments);
		this.put("arguments", arguments);
		arguments.addProperty("graphiteMetricsSender", "org.apache.jmeter.visualizers.backend.graphite.TextGraphiteMetricsSender", "=");
		arguments.addProperty("graphiteHost", graphiteHost, "=");
		arguments.addProperty("graphitePort", graphitePort, "=");
		arguments.addProperty("rootMetricsPrefix", "jmeter."+scnName+".", "=");
		arguments.addProperty("summaryOnly", "false", "=");
		arguments.addProperty("samplersList", "Tx_.*", "=");
		arguments.addProperty("useRegexpForSamplersList", "true", "=");
		arguments.addProperty("percentiles", "90;95;99", "=");
		
		this.put("classname", "org.apache.jmeter.visualizers.backend.graphite.GraphiteBackendListenerClient");
	}
	@Override
	public List<Object> getChildren() {
		return null;
	}

	@Override
	public String getTagName() {
		return "BackendListener";
	}

	@Override
	public String getTestclass() {
		return "BackendListener";
	}

	@Override
	public String getUIClass() {
		return "BackendListenerGui";
	}

	@Override
	public boolean isPrintEmptyHashTree() {
		return true;
	}
}
