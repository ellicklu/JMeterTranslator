package ecd.perf.utilities.main.lr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;

import ecd.perf.utilities.expression.TransformsUtil;
import ecd.perf.utilities.main.AbstractScriptLoader;
import ecd.perf.utilities.main.SourceFileLoadUtil;
import ecd.perf.utilities.model.jmeter.JmeterTestPlan;
import ecd.perf.utilities.model.jmeter.listener.BackendListener;

public class LRScriptLoader extends AbstractScriptLoader {
	public JmeterTestPlan parseLRSources(List<String> files) {
		TransformsUtil.setScriptType(TransformsUtil.LRScript);
		LRScriptASTNodesVisitor visitor = new LRScriptASTNodesVisitor(this);
		for(String filepath: files) {
			loadAndVisitActionFile(visitor, filepath);
		}
		return visitor.getRootTestPlan();
	}

	private void loadAndVisitActionFile(ASTVisitor visitor, String filename) {
		char[] src = SourceFileLoadUtil.loadLRFileSource(filename);
		if(src != null) {
			setScriptPath(filename);
			setSource(src);
			visitSource(visitor);
		}
	}
	
	public void loadScript(String filename) {
		File mainscript = new File(filename);

		File folder = mainscript.getParentFile();
		BufferedReader mainReader = null;
		try {
			mainReader = new BufferedReader(new FileReader(mainscript));
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: failed to read file - " + filename);
			e.printStackTrace();
		}
		String readLine = null;
		ArrayList<String> actionFiles = new ArrayList<String>();
		try {
			while ((readLine = mainReader.readLine()) != null) {
				if(readLine.matches("# 1 \".*\\.c\" 1")) {
					String actionFile = readLine.substring(readLine.indexOf("\"")+1, readLine.indexOf("\" 1"));
					//System.out.println("Parsing file: " + actionFile);
					actionFiles.add(folder.getAbsolutePath() + File.separator + actionFile);
				}
			}
		} catch (IOException e) {
			System.out.println("WARN: failed to read mainscript - " + filename);
		}
		
		if(actionFiles.size() > 0) {
			//getInstance().parseAssets();
			varProccessors.put("solveClientReqID", "C:\\scripts\\SolveClientID.groovy");
			varProccessors.put("getFileSelection", "inlines");
			File[] childFiles = folder.listFiles();
			for(File childFile : childFiles) {
				if(childFile.getAbsolutePath().endsWith(".prm")){
					TransformsUtil.putPredefinedVariables(LRParamUtil.parsePredefinedVariables(childFile));
					break;
				}
			}
			testPlanRoot = parseLRSources(actionFiles);
			addGlobalParameters(testPlanRoot);
			BackendListener backendListener = new BackendListener("10.37.1.87","2003","cp.create_file");
			testPlanRoot.getTestPlan().getThreadGroup().addListener(backendListener);
		} else {
			System.out.println("No Action Files Found!");
		}
	}
}
