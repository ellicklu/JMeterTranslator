package ecd.perf.utilities.main.ats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import org.xml.sax.SAXException;

import ecd.perf.utilities.expression.file.AssetsXMLSaxHandler;
import ecd.perf.utilities.expression.file.DataBankInfo;
import ecd.perf.utilities.expression.file.XMLSerializer;
import ecd.perf.utilities.main.AbstractScriptLoader;
import ecd.perf.utilities.main.SourceFileLoadUtil;
import ecd.perf.utilities.model.jmeter.CSVDataSet;
import ecd.perf.utilities.model.jmeter.JmeterTestPlan;
import ecd.perf.utilities.model.jmeter.listener.BackendListener;

public class ATSScriptLoader extends AbstractScriptLoader {

	private HashMap<String, DataBankInfo> databanksInScript;
	@Override
	public void loadScript(String filename) {
		char[] src = SourceFileLoadUtil.loadFileSouce(filename);
		if(src == null) {
			return;
		}
		varProccessors.put("solveClientReqID", "C:\\scripts\\SolveClientID.groovy");
		varProccessors.put("getFileSelection", "inlines");
		setScriptPath(filename);
		setSource(src);
		parseAssets();
		testPlanRoot = parseATSSource();
		addGlobalParameters(testPlanRoot);
		BackendListener backendListener = new BackendListener("10.37.1.87","2003","cp.create_file");
		testPlanRoot.getTestPlan().getThreadGroup().addListener(backendListener);
	}

	private JmeterTestPlan parseATSSource() {
		ScriptASTNodesVisitor visitor = new ScriptASTNodesVisitor(this);
		visitSource(visitor);
		return visitor.getRootTestPlan();
	}
	

	public final String getDatabankFullPath(String alias) {
		if(databanksInScript != null && databanksInScript.containsKey(alias)) {
			return databanksInScript.get(alias).getDatabankFileName();
		} else {
			return null;
		}
	}
	
	public final CSVDataSet loadDatabankByName(String databankName) {
		//TODO: get databank full file path
		String assetPath = getAssetPath();
		if(assetPath != null) {
			//File assetFile = new File(assetPath);
		}
		String databankFullPath = getDatabankFullPath(databankName);
		if(databankFullPath == null) {
			databankFullPath = databankName+".csv";
		}
		CSVDataSet csv = new CSVDataSet(databankName, databankFullPath, "UTF8", ",", false, true, false, "shareMode.all");
		getDatabanks().put(databankName, csv);
		//System.out.println("databank:"+databankName);
		return csv;
	}
	public String getAssetPath() {
		File scriptFile = new File(getScriptPath());
		if(scriptFile.isFile()) {
			String scriptFolder = scriptFile.getParent();
			String assetPath =  scriptFolder + File.separator + "assets.xml";
			File assetFile = new File(assetPath);
			if(assetFile.exists()) {
				return assetPath;
			}
		}
		return null;
	}
	private void parseAssets() {
		String assetPath = getAssetPath();
		if(assetPath == null)
			return;
		try {
			AssetsXMLSaxHandler handler = new AssetsXMLSaxHandler();
			InputStream stream = new FileInputStream(assetPath);
			XMLSerializer.parseDocument(stream, handler);
			databanksInScript = handler.getParsedDatabanks();
		} catch (SAXException e) {
			return;
		} catch (FileNotFoundException e) {
			return;
		}
	}
}
