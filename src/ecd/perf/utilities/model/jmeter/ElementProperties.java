package ecd.perf.utilities.model.jmeter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ecd.perf.utilities.model.jmeter.http.ParamEntry;
public class ElementProperties extends AbstractModelElement {
	private static final long serialVersionUID = 1L;
	public static final String guiclassArguments="ArgumentsPanel";
	public static final String guiclassHTTPArguments="HTTPArgumentsPanel";
	public static final String guiclassAuthorization = "Authorization";
	
	private final List<Object> properties = new ArrayList<Object>();
	
	private final String guiClass;
	public ElementProperties(String guiClassName) {
		super("User Defined Variables");
		guiClass = guiClassName;
		this.put("Arguments.arguments", properties);
	}
	
	public void addProperty(String name, String value, String metadata) {
		properties.add(new ElementEntry(name, value, metadata));
	}
	
	public void addParameter(String name, String value) {
		properties.add(new ParamEntry(name, value));
	}
	
	public void addParameter(String name) {
		properties.add(new ParamEntry(name));
	}

	@Override
	public List<Object> getChildren() {
		return null;
	}

	@Override
	public String getTagName() {
		return "elementProp";
	}

	@Override
	public String getTestclass() {
		return "Arguments";
	}

	@Override
	public String getUIClass() {
		return guiClass;
	}
	
	public Map<String, String> getAttributes() {
		Map<String, String> attrs = super.getAttributes();
		if(guiclassAuthorization.equals(guiClass)){
			attrs.put("elementType", guiclassAuthorization);	
		} else {
			attrs.put("elementType", "Arguments");
		}
		return attrs;
	}

	@Override
	public boolean isPrintEmptyHashTree() {
		return false;
	}


}
