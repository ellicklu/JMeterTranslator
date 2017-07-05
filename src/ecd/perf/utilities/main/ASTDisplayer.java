package ecd.perf.utilities.main;
import ecd.perf.utilities.main.ats.ATSScriptLoader;
import ecd.perf.utilities.main.lr.LRScriptLoader;

public class ASTDisplayer {
	public static void main(String[] args) throws Exception {
//		String filename = "C:\\OracleATS\\OFT\\script.java";
//		AbstractScriptLoader sLoader = new ATSScriptLoader();
//		sLoader.loadScript(filename);
		String filename = "C:\\CP_Thermal_script\\pre_cci.c";
		AbstractScriptLoader sLoader = new LRScriptLoader();
		sLoader.loadScript(filename);
		sLoader.writeToXMLFile("c:\\cpx.jmx");
	}
}
