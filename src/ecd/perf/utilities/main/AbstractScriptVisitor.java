package ecd.perf.utilities.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;

import ecd.perf.utilities.model.jmeter.AbstractGroupElement;
import ecd.perf.utilities.model.jmeter.AbstractModelElement;
import ecd.perf.utilities.model.jmeter.JmeterTestPlan;
import ecd.perf.utilities.model.jmeter.TransactionController;
import ecd.perf.utilities.model.jmeter.http.HTTPSampler;
import ecd.perf.utilities.model.jmeter.logic.ModuleController;
import ecd.perf.utilities.model.jmeter.postProcessor.JSR223PostProcessor;
import ecd.perf.utilities.model.jmeter.postProcessor.PostProcessor;
import ecd.perf.utilities.model.jmeter.timer.ConstantTimer;

public abstract class AbstractScriptVisitor extends ASTVisitor {
	protected final List<String> modulesToParse = new ArrayList<String>();
	protected final List<String> modulesParsed = new ArrayList<String>();
	protected final JmeterTestPlan testPlanRoot;
	protected final AbstractScriptLoader loader;
	protected final HashMap<String, String> encodeOptions = new HashMap<String, String>();
	protected final HashMap<String, JSR223PostProcessor> inlineScripts = new HashMap<String, JSR223PostProcessor>();
	
	protected AbstractGroupElement  currentTransaction = null;
	protected HTTPSampler currentHTTPSampler = null;
	protected int stepGroupLevel = 0;
	protected String currentActionModule = null;
	
	protected AbstractScriptVisitor(AbstractScriptLoader loader) {
		testPlanRoot = new JmeterTestPlan();
		this.loader = loader;
	}
	
    public JmeterTestPlan getRootTestPlan() {
		return testPlanRoot;
    }
    
	protected void beginTransaction(AbstractGroupElement newTrans) {
		addSubStep(newTrans);
    	currentTransaction = newTrans;
		
		if(currentHTTPSampler != null) {
			//currentHTTPSampler.setTimer(new ConstantTimer(3000,5000));
			currentHTTPSampler.setTimer(new ConstantTimer("${THINK_FROM}","${THINK_TO}"));
		}
		stepGroupLevel ++;
    }
    
	protected void endTransaction() {
    	currentTransaction = currentTransaction.getParent();
    	stepGroupLevel --;
    	if(stepGroupLevel < 0) {
    		System.out.println("warn: wrong level:" + stepGroupLevel);
    	}
    }
	
	protected void handleCallMethod(MethodInvocation node) {
    	String methodName = node.getName().getFullyQualifiedName();
    	if(!modulesToParse.contains(methodName) && !modulesParsed.contains(methodName)) {
        	modulesToParse.add(methodName);//register the method to be parsed
    	}
    	ModuleController module = new ModuleController("Call "+ methodName);
    	module.appendPathNode("WorkBench");// this shall be hardcoded
    	module.appendPathNode(testPlanRoot.getTestPlan().getTestName());
    	module.appendPathNode(testPlanRoot.getTestPlan().getModuleActions().getTestName());
    	module.appendPathNode(testPlanRoot.getTestPlan().getModuleActions().getMainController().getTestName());
    	module.appendPathNode(methodName);
    	
		addSubStep(module);
    }
	
	protected void handleKnownCustomizedMethod(MethodInvocation node, String methodName, String groovyPath) {
    	PostProcessor postprocessor;
    	if("inlines".equalsIgnoreCase(groovyPath)){
    		if(inlineScripts.containsKey(methodName)) {
    			postprocessor = inlineScripts.get(methodName);
    		} else {
    			inlineScripts.put(methodName, new JSR223PostProcessor(methodName, "", "", "//TODO"));
    			postprocessor = inlineScripts.get(methodName);
    		}
    	} else {
    		postprocessor = new JSR223PostProcessor(methodName, groovyPath, "", "");
    	}
    	if(currentHTTPSampler != null) {
    		currentHTTPSampler.addPostProcessor(postprocessor);
    	} else {
    		testPlanRoot.getTestPlan().getThreadGroup().getMainController().addSubStep(postprocessor);
    	}
    }
	
	protected void handleUnknownCustomizedMethod(MethodInvocation node, String methodName) {
    	PostProcessor postprocessor = new JSR223PostProcessor(methodName, "", "", "//TODO");
    	if(currentHTTPSampler != null) {
        	currentHTTPSampler.addPostProcessor(postprocessor);
    	} else {
    		testPlanRoot.getTestPlan().getThreadGroup().getMainController().addSubStep(postprocessor);
    	}
    }

	protected AbstractGroupElement getMainController(){
    	return testPlanRoot.getTestPlan().getThreadGroup().getMainController();
    }

    private TransactionController getParsingModule(){
    	List<Object> modules =  testPlanRoot.getTestPlan().getModuleActions().getMainController().getChildren();
    	if(currentActionModule != null){
        	for(Object module : modules) {
        		if(module instanceof TransactionController && currentActionModule.equals(((TransactionController)module).getTestName()))
        			return (TransactionController)module;
        	}
    	}
    	return null;
    }
    protected void addSubStep(AbstractModelElement subStep) {
		if(stepGroupLevel == 0) {
			if(currentActionModule == null) {//in main thread
				getMainController().addSubStep(subStep);
			} else { // in module action
				TransactionController currentModule = getParsingModule();
				if(currentModule == null) {
					System.out.println("warn: no current module found");
				} else {
					currentModule.addSubStep(subStep);
				}
			}
		} else {
			currentTransaction.addSubStep(subStep);
		}
    }    

}
