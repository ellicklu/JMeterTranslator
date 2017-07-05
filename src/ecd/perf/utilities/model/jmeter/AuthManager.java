package ecd.perf.utilities.model.jmeter;

import java.util.List;
//<collectionProp name="AuthManager.auth_list">
//  <elementProp name="" elementType="Authorization">
//    <stringProp name="Authorization.url">{host}:{port}</stringProp>
//    <stringProp name="Authorization.username">{Submitter}</stringProp>
//    <stringProp name="Authorization.password">{Password}</stringProp>
//    <stringProp name="Authorization.domain"></stringProp>
//    <stringProp name="Authorization.realm"></stringProp>
//  </elementProp>
//</collectionProp>
//</AuthManager>
public class AuthManager extends AbstractModelElement {
	private static final long serialVersionUID = 1L;

	protected AuthManager() {
		super("HTTP Authorization Manager");
		
	}

	@Override
	public List<Object> getChildren() {
		return null;
	}

	@Override
	public String getTagName() {
		return "AuthManager";
	}

	@Override
	public String getTestclass() {
		return "AuthManager";
	}

	@Override
	public String getUIClass() {
		return "AuthPanel";
	}

	@Override
	public boolean isPrintEmptyHashTree() {
		return false;
	}

}
