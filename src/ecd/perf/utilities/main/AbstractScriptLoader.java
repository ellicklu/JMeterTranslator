package ecd.perf.utilities.main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

import ecd.perf.utilities.model.jmeter.CSVDataSet;
import ecd.perf.utilities.model.jmeter.JmeterTestPlan;
import ecd.perf.utilities.xmlStream.core.XmlStream;
import ecd.perf.utilities.xmlStream.core.XmlStreamException;

public abstract class AbstractScriptLoader {
	private final ASTParser parser;
	private final XmlStream xmlWriter = new XmlStream();
	private final HashMap<String, HostInfo> hostMap = new HashMap<String, HostInfo> ();
	private final HashMap<String, CSVDataSet> databanks = new HashMap<String, CSVDataSet>();
	protected final HashMap<String, String> varProccessors = new HashMap<String,String>();
	protected JmeterTestPlan testPlanRoot;
	private String scriptPath;
	public abstract void loadScript(String filepath);
	
	protected AbstractScriptLoader() {
		parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		Map<String, String> compilerOptions = JavaCore.getOptions();
		compilerOptions.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_7);
		compilerOptions.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_7);
		compilerOptions.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);
		parser.setCompilerOptions(compilerOptions);
	}
	
	public final HashMap<String, CSVDataSet> getDatabanks(){
		return databanks;
	}
	
	public final HashMap<String, HostInfo> getHostMap(){
		return hostMap;
	}
	
	public final HashMap<String, String> getVarProccessors(){
		return varProccessors;
	}
	
	public void setSource(char[] source) {
		parser.setSource(source);
	}
	
	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}
	public String getScriptPath() {
		return scriptPath;
	}
	
	protected void visitSource(ASTVisitor visitor) {
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.accept(visitor);
	}
	
	protected void addGlobalParameters(JmeterTestPlan testPlanRoot){
		if(this.hostMap != null && this.hostMap.entrySet().size() > 0) {
			for(HostInfo host: hostMap.values()) {
				testPlanRoot.getTestPlan().addGlobalVariable("HOST"+host.getAlias(), "${__P(host"+host.getAlias()+","+host.getHost()+")}");
				testPlanRoot.getTestPlan().addGlobalVariable("PORT"+host.getAlias(), "${__P(port"+host.getAlias()+","+host.getPort()+")}");
			}
		}
		testPlanRoot.getTestPlan().addGlobalVariable("THREADS", "${__P(threads,2)}");
		testPlanRoot.getTestPlan().addGlobalVariable("RAMPTIME", "${__P(ramptime,2)}");
		testPlanRoot.getTestPlan().addGlobalVariable("LOOPS", "${__P(loops,2)}");
		testPlanRoot.getTestPlan().addGlobalVariable("THINK_FROM", "${__P(thinkfrom,3000)}");
		testPlanRoot.getTestPlan().addGlobalVariable("THINK_TO", "${__P(thinkto,5000)}");
	}

	public void writeToXMLFile(String filepath) {
		if(testPlanRoot == null) {
			System.out.println("ERROR: empty content of test plan, abort output.");
			return;
		}
		FileWriter fw = null;
		try {
			String content = xmlWriter.toXML(testPlanRoot);
			fw = new FileWriter(filepath);
			fw.write(content);
			fw.flush();
		} catch (XmlStreamException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
