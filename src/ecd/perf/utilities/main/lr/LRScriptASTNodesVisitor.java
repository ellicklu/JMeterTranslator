package ecd.perf.utilities.main.lr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ecd.perf.utilities.expression.TransformsUtil;
import ecd.perf.utilities.main.AbstractScriptLoader;
import ecd.perf.utilities.main.AbstractScriptVisitor;
import ecd.perf.utilities.model.jmeter.AbstractModelElement;
import ecd.perf.utilities.model.jmeter.JmeterTestPlan;
import ecd.perf.utilities.model.jmeter.TransactionController;
import ecd.perf.utilities.model.jmeter.http.HTTPSampler;
import ecd.perf.utilities.model.jmeter.postProcessor.JSR223PostProcessor;
import ecd.perf.utilities.model.jmeter.postProcessor.PostProcessor;
import ecd.perf.utilities.model.jmeter.postProcessor.RegexExtractor;
import ecd.perf.utilities.model.jmeter.postProcessor.ResponseAssertion;
import ecd.perf.utilities.model.jmeter.timer.ConstantTimer;

public class LRScriptASTNodesVisitor extends AbstractScriptVisitor {

	protected LRScriptASTNodesVisitor(AbstractScriptLoader loader) {
		super(loader);
	}

	private final HashMap<String, JSR223PostProcessor> inlineScripts = new HashMap<String, JSR223PostProcessor>();

	public static final String METHOD_ATOI = "atoi";
	public static final String METHOD_GET_DOCS_AFTER_CREATING_FILE = "getDocsAfterCreatingFile";
	public static final String METHOD_INIT_IMPORT = "InitImport";
	public static final String METHOD_LR_END_TRANS = "lr_end_transaction";
	public static final String METHOD_LR_EVAL_STRING = "lr_eval_string";
	public static final String METHOD_LR_START_TRANSACTION = "lr_start_transaction";
	public static final String METHOD_LR_THINK_TIME = "lr_think_time";
	public static final String METHOD_REGIST_REQ_ID_QUERY = "registRequestIdQuery";
	public static final String METHOD_SAVE_REGISTERED_REQ_ID = "saveRegistedRequestIdAs";
	public static final String METHOD_SET_SELECTION = "setSelectionAndIDOfFile";
	public static final String METHOD_SET_UID = "setUID";
	public static final String METHOD_WEB_REG_SAVE_PARAM = "web_reg_save_param";
	public static final String METHOD_WEB_REG_SAVE_PARAM_EX = "web_reg_save_param_ex";
	public static final String METHOD_WEB_SUBMIT_DATA = "web_submit_data";
	public static final String METHOD_WEB_CUSTOM_REQUEST = "web_custom_request";
	public static final String METHOD_WEB_URL = "web_url";
	public static final String METHOD_CONCURRENT_START = "web_concurrent_start";
	public static final String METHOD_CONCURRENT_END = "web_concurrent_end";
	public static final String METHOD_REGISTER_FIND = "web_reg_find";
	public static final String METHOD_SET_SOCKETS_OPTION = "web_set_sockets_option";
	public static final String METHOD_WEB_SET_USER = "web_set_user";
	//("{submitter}", "{passwordS}", "{host}:{port}");
	//("INITIAL_BASIC_AUTH","1");
	

	public static final String ARG_ACTION = "Action=";
	public static final String ARG_URL = "URL=";
	public static final String ARG_CONTENT_TYPE = "RecContentType=";
	public static final String ARG_REFERER = "Referer=";
	public static final String ARG_NAME = "Name=";
	public static final String ARG_VALUE = "Value=";
	public static final String ARG_TEST_PFX = "TextPfx=";
	public static final String ARG_TEST_SFX = "TextSfx=";
	public static final String ARG_TEST = "Text=";
	public static final String ENDITEM = "ENDITEM";

	public static final String ARG_LB = "LB/IC=";
	public static final String ARG_RB = "RB/IC=";
	public static final String ARG_LBRE = "LB/RE=";
	public static final String ARG_RBRE = "RB/RE=";
	public static final String ARG_ORD = "Ord=";
	public static final String ARG_NOT_FOUND = "NotFound=";
	public static final String ARG_REL_FRAME_ID = "RelFrameId=";
	public static final String ARG_SEARCH = "Search=";
	public static final String LAST = "LAST";

	private static final Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[()\\[\\].+*?^$\\\\|]");
	
	private List<PostProcessor> savedProcessors = new ArrayList<PostProcessor>();
	
	public boolean visit(TypeDeclaration node) {
		currentActionModule = node.getName().getFullyQualifiedName();
		TransactionController currentTransaction = new TransactionController(currentActionModule);
		testPlanRoot.getTestPlan().getModuleActions().getMainController().addSubStep(currentTransaction);
		return true;
	}
	
	public void endVisit(TypeDeclaration node) {
		String className = node.getName().getFullyQualifiedName();
        modulesToParse.remove(className);
        modulesParsed.add(className);
        if(currentActionModule != null && currentActionModule.equals(className)){
        	currentActionModule = null;
        }
	}
	
    @Override  
    public boolean visit(MethodDeclaration node) {  
    	String methodName = node.getName().getFullyQualifiedName();
        if(loader.getVarProccessors().containsKey(methodName)) {
            if(inlineScripts.containsKey(methodName)) {
            	String code = node.getBody().toString();
            	inlineScripts.get(methodName).setCode(code);
            	return false;
            } else {
            	return false;
            }        	
        }
        return true;  
    }  

	public boolean visit(MethodInvocation node) {
		String methodName = node.getName().getFullyQualifiedName();
		//System.out.println(methodName);
		if(METHOD_WEB_SUBMIT_DATA.equalsIgnoreCase(methodName)) {
    		currentHTTPSampler = handlePostRequest(node);
    		return false;
		} else if(METHOD_WEB_CUSTOM_REQUEST.equalsIgnoreCase(methodName)) {
    		currentHTTPSampler = handleGetRequest(node);
    		return false;
		} else if(METHOD_WEB_URL.equalsIgnoreCase(methodName)) {
    		currentHTTPSampler = handleGetRequest(node);
    		return false;
		} else if (METHOD_LR_START_TRANSACTION.equalsIgnoreCase(methodName)) { 
			handleStartTransaction(node);
			return false;
		} else if (METHOD_LR_END_TRANS.equalsIgnoreCase(methodName)) {
			handleEndTransaction(node);
			return false;
		} else if (METHOD_LR_THINK_TIME.equalsIgnoreCase(methodName)){
			handleThinkTime(node);
			return false;
		} else if (METHOD_WEB_REG_SAVE_PARAM.equalsIgnoreCase(methodName)){
			handleSaveParam(node);
			return false;
		} else if (METHOD_WEB_REG_SAVE_PARAM_EX.equalsIgnoreCase(methodName)) {
			handleSaveParamEx(node);
			return false;
		} else if (METHOD_CONCURRENT_START.equalsIgnoreCase(methodName) || METHOD_CONCURRENT_END.equalsIgnoreCase(methodName) ){
			//just add the requests in sequence, in jmeter there is no direct equavient for concurrent
			return false;
		} else if (METHOD_REGISTER_FIND.equalsIgnoreCase(methodName)) {
			handleRegisterFind(node);
			return false;
		} else {
			if(loader.getVarProccessors().containsKey(methodName)) {
				handleKnownCustomizedMethod(node, methodName, loader.getVarProccessors().get(methodName));
			} else if(node.getExpression() == null && node.arguments().size() == 0) {
				System.out.println("customized module:" + methodName);
				handleCallMethod(node);
			} else {
				System.out.println("warn: other unknown api, generate groovy scripts:" + methodName);
				handleUnknownCustomizedMethod(node, methodName);
			}
		}
		return true;
	}
	
    private void handleStartTransaction(MethodInvocation node) {
		List<Expression> arguments = node.arguments();
		String stepName = null;
		for(Expression argument : arguments) {
			if(argument instanceof StringLiteral) {
				stepName = ((StringLiteral)argument).getLiteralValue();
				stepName = "Tx"+stepName.replaceAll("\\[|\\]|\\s", "_");
				break;
			}
		}
		beginTransaction(new TransactionController(stepName));

    }
    
    private void handleEndTransaction(MethodInvocation node) {
    	endTransaction();
    }
    
    private void handleThinkTime(MethodInvocation node) {
    	currentHTTPSampler.setTimer(new ConstantTimer("${THINK_FROM}","${THINK_TO}"));
    }
    
    
	
    private void handleRegisterFind(MethodInvocation node) {
    	//web_reg_find("Text=\"ecs_lc_state\":\"Cancelled\"", LAST);
    	List<Expression> arguments = node.arguments();
    	List<String> assertions = new ArrayList<String>();
    	for(Expression arg : arguments) {
    		if(arg instanceof StringLiteral) {
        		String rawValue = ((StringLiteral)arg).getLiteralValue();
        		if(rawValue.startsWith(ARG_TEST)) {
        			assertions.add(rawValue.substring(ARG_TEST.length()));
        		} else if (rawValue.startsWith(ARG_TEST_PFX)) {
        			assertions.add(rawValue.substring(ARG_TEST_PFX.length()));
        		} else if (rawValue.startsWith(ARG_TEST_SFX)) {
        			assertions.add(rawValue.substring(ARG_TEST_SFX.length()));
        		}
    		}
    	}
    	ResponseAssertion assertion = new ResponseAssertion(assertions);
    	savedProcessors.add(assertion);
    }
    
    private void handleSaveParamEx(MethodInvocation node) {
//    	web_reg_save_param_ex( 
//    			"paramName=r_object_id_ext",
//    			"LB/RE=\"has_content\":true,\"object_name\":\"[^\"]+\",\"object_id\":\"",
//    			"RB/IC=\",\"in_creation\":false,\"",
//    	 		"NotFound=warning",
//    			LAST);
    }
    
    private void handleSaveParam(MethodInvocation node) {
    	List<Expression> arguments = node.arguments();
    	String varName = null;
    	String leftBound = null;
    	String rightBound = null;
    	String pattern = null;
    	boolean isOptional = false;
    	boolean isFromHeader = false;
    	String recVal = "";
    	int index = -1;
    	
    	if(arguments.size() < 2) {
    		System.out.println("warn:argument number not correct");
    		return;
    	}
    	
    	for(Expression arg : arguments) {
    		if(arg instanceof StringLiteral) {
        		String rawValue = ((StringLiteral)arg).getLiteralValue();
        		if(rawValue.startsWith(ARG_LB)) {
        			leftBound = rawValue.substring(ARG_LB.length());
            		leftBound = TransformsUtil.translate(leftBound, encodeOptions, loader.getDatabanks());
            		leftBound = SPECIAL_REGEX_CHARS.matcher(leftBound).replaceAll("\\\\$0");
        		} else if(rawValue.startsWith(ARG_RB)) {
        			rightBound = rawValue.substring(ARG_RB.length());
            		rightBound = TransformsUtil.translate(rightBound, encodeOptions, loader.getDatabanks());
            		rightBound = SPECIAL_REGEX_CHARS.matcher(rightBound).replaceAll("\\\\$0");
        		} else if(rawValue.startsWith(ARG_LBRE)) {
        			leftBound = rawValue.substring(ARG_LBRE.length());
            		leftBound = TransformsUtil.translate(leftBound, encodeOptions, loader.getDatabanks());
        		} else if(rawValue.startsWith(ARG_RBRE)) {
        			rightBound = rawValue.substring(ARG_RBRE.length());
            		rightBound = TransformsUtil.translate(rightBound, encodeOptions, loader.getDatabanks());
        		} else if(rawValue.startsWith(ARG_ORD)) {
        			try{
        				index = Integer.parseInt(rawValue.substring(ARG_ORD.length()));
        			} catch (Throwable t) {
        				if("All".equalsIgnoreCase(rawValue.substring(ARG_ORD.length()))) {
        					index = 0;
        				} else {
        					index = -1;
        				}
        			}
        		} else if(rawValue.startsWith(ARG_NOT_FOUND)) {
        			isOptional = "error".equalsIgnoreCase(rawValue.substring(ARG_NOT_FOUND.length())) ? true : false;
        		} else if(rawValue.startsWith(ARG_REL_FRAME_ID)) {
        			//TODO
        		} else if(rawValue.startsWith(ARG_SEARCH)) {
        			isFromHeader = "body".equalsIgnoreCase(rawValue.substring(ARG_REL_FRAME_ID.length())) ? false: true;
        		} else if(rawValue.indexOf('=') == -1){
        			varName = TransformsUtil.translate(rawValue, encodeOptions, loader.getDatabanks());
        		} else {
        			//do nothing
        		}
    		}
    	}
    	
    	if(varName == null && arguments.get(0) instanceof StringLiteral) {
    		varName = TransformsUtil.translate(((StringLiteral)arguments.get(0)).getLiteralValue(), encodeOptions, loader.getDatabanks());
    	}
    	if(varName == null){
			System.out.println("warn:varname cannot be parsed");
			return;
    	}
    	
    	if(leftBound != null && rightBound != null) {
    		pattern = leftBound + "(.+?)" + rightBound;
    	} else {
    		System.out.println("warn:pattern cannot be parsed");
    		return;
    	}
    	
    	
    	if(index == -1) {
    		System.out.println("warn:index cannot be parsed");
    		index=1;// first variable
    	}
    	
    	PostProcessor postprocessor = new RegexExtractor(varName, pattern, "$1$", recVal, String.valueOf(index), isFromHeader);
    	savedProcessors.add(postprocessor);
    }

	
    private HTTPSampler handleGetRequest(MethodInvocation node) {
    	int id = getRequestID(node);
    	HTTPSampler sampler = new HTTPSampler(id, getUrl(node), getEncoding(node), HTTPSampler.METHOD_GET, false, encodeOptions, loader);
    	parseHeaders(node, sampler);
    	parseParams(node, sampler);
    	addSubStep(sampler);
		return sampler;
    }

    private HTTPSampler handlePostRequest(MethodInvocation node) {
    	int id = getRequestID(node);
    	HTTPSampler sampler = new HTTPSampler(id, getUrl(node), getEncoding(node), HTTPSampler.METHOD_POST, false, encodeOptions, loader);
    	parseHeaders(node, sampler);
    	parseParams(node, sampler);
    	addSubStep(sampler);
		return sampler;
    }
    
    @Override
    protected void addSubStep(AbstractModelElement subStep) {
    	if(subStep instanceof HTTPSampler) {
    		//save post proccessors.
    		for(PostProcessor processor : savedProcessors) {
    			((HTTPSampler)subStep).addPostProcessor(processor);
    		}
    		savedProcessors.clear();
    	}
    	super.addSubStep(subStep);
    }

	public JmeterTestPlan getRootTestPlan() {
		return testPlanRoot;
	}
	
    private int getRequestID(MethodInvocation node) {
		Expression argument = (Expression) node.arguments().get(0);
		if(argument instanceof NumberLiteral) {
			String valStr = ((NumberLiteral)argument).getToken();
			return Integer.parseInt(valStr);
		}
		return 0;
    	
    }
	
    private String getUrl(MethodInvocation node) {
    	//Action={Protocol}://{Hostname}:{Port}/{Context}/eif/library/objectlist/objectlist.jsp
    	List<Expression> methodArguments = node.arguments();
		String url = null;
		for(Expression arg : methodArguments) {
    		if(arg instanceof StringLiteral && ((StringLiteral)arg).getLiteralValue().startsWith(ARG_ACTION)) {
				url = ((StringLiteral)arg).getLiteralValue().substring(ARG_ACTION.length());
				break;
			} else if(arg instanceof StringLiteral && ((StringLiteral)arg).getLiteralValue().startsWith(ARG_URL)) {
				url = ((StringLiteral)arg).getLiteralValue().substring(ARG_URL.length());
				break;
			}
		}
		
		if(url == null) {
			System.out.println("WARN:Unable to parse url" + node.toString());
		} else {
			//System.out.println("Url" + url);
		}
		return url;
    }
    
    private String getEncoding(MethodInvocation node) {
    	String encoding = "UTF8";
    	return encoding;
    }
    
    private void parseHeaders(MethodInvocation node, HTTPSampler httpSampler){
    	List<Expression> arguments = node.arguments();
    	for(Expression argument : arguments) {
    		if(argument instanceof StringLiteral) {
    			if(((StringLiteral)argument).getLiteralValue().startsWith(ARG_CONTENT_TYPE)){
    				String contentType = ((StringLiteral)argument).getLiteralValue().substring(ARG_CONTENT_TYPE.length());
					httpSampler.addHeader("Content-Type", contentType);
    			} else if(((StringLiteral)argument).getLiteralValue().startsWith(ARG_REFERER)){
    				String referer = ((StringLiteral)argument).getLiteralValue().substring(ARG_REFERER.length());
					httpSampler.addHeader("Referer", referer);
    			} else {
    				//System.out.println(argument.getClass().getName());
    			}
    		}
    	}
    }
    	
    private void parseParams(MethodInvocation node, HTTPSampler httpSampler){
    	List<Expression> arguments = node.arguments();
    	String tempName = null,tempValue = null;
    	for(int i = 0; i < arguments.size(); i++) {
    		//"Name=ObjectList_doclist_grid_0_dataScrollLeft", "Value=0", ENDITEM,
    		Expression argument = arguments.get(i);
			if(argument instanceof StringLiteral && ((StringLiteral)argument).getLiteralValue().startsWith(ARG_NAME)){
				tempName = ((StringLiteral)argument).getLiteralValue().substring(ARG_NAME.length());
			} else if(argument instanceof StringLiteral && ((StringLiteral)argument).getLiteralValue().startsWith(ARG_VALUE)){
				tempValue = ((StringLiteral)argument).getLiteralValue().substring(ARG_VALUE.length());
			} else if(argument instanceof SimpleName && ((SimpleName)argument).getFullyQualifiedName().equals(ENDITEM)){
				//System.out.println(argument.getClass().getName());
				httpSampler.addParam(tempName, tempValue);
				tempName = null;
				tempValue = null;
			}
    	}
    }
}
