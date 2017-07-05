package ecd.perf.utilities.main.ats;
import java.util.List;

import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;  
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;  
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;  
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WhileStatement;

import ecd.perf.utilities.expression.TransformsUtil;
import ecd.perf.utilities.main.AbstractScriptLoader;
import ecd.perf.utilities.main.AbstractScriptVisitor;
import ecd.perf.utilities.model.jmeter.AbstractGroupElement;
import ecd.perf.utilities.model.jmeter.AbstractModelElement;
import ecd.perf.utilities.model.jmeter.ElementProperties;
import ecd.perf.utilities.model.jmeter.JmeterTestPlan;
import ecd.perf.utilities.model.jmeter.TransactionController;
import ecd.perf.utilities.model.jmeter.http.HTTPSampler;
import ecd.perf.utilities.model.jmeter.logic.IfController;
import ecd.perf.utilities.model.jmeter.logic.WhileController;
import ecd.perf.utilities.model.jmeter.postProcessor.BeanShellPostProcessor;
import ecd.perf.utilities.model.jmeter.postProcessor.JSR223PostProcessor;
import ecd.perf.utilities.model.jmeter.postProcessor.PostProcessor;
import ecd.perf.utilities.model.jmeter.postProcessor.RegexExtractor;
import ecd.perf.utilities.model.jmeter.timer.ConstantTimer;
import ecd.perf.utilities.xpath.XPathTranslator;

public class ScriptASTNodesVisitor extends AbstractScriptVisitor {
	//sections
	public static final String METHOD_INIT = "initialize";
	public static final String METHOD_RUN = "run";
	public static final String METHOD_FIN = "finish";
	//headers
	public static final String METHOD_HEADERS = "headers";
	public static final String METHOD_HEADER = "header";
	
	public static final String METHOD_SETUSERAGENT = "setUserAgent";
	
	public static final String METHOD_SETLANGUAGE = "setAcceptLanguage";
	
	//transactions
	public static final String METHOD_BEGINSTEP = "beginStep";
	public static final String METHOD_ENDSTEP = "endStep";
	
	//http requests
	public static final String METHOD_POST = "post";
	public static final String METHOD_GET = "get";
	public static final String METHOD_MULTIPART = "multipartPost";
	public static final String METHOD_NAVIGATE = "navigate";
	public static final String METHOD_POSTDATA = "postdata";
	public static final String METHOD_QUERYSTRING = "querystring";
	public static final String METHOD_PARAM = "param";
	
	//correlations
	public static final String METHOD_SOLVE_XPATH = "solveXPath";
	public static final String METHOD_SOLVE_REGEX = "solve";
	public static final String METHOD_SOLVE_HEADER = "solveRefererHeader";
	public static final String METHOD_ADD_COOKIE = "addCookie";
	
	//variables
	public static final String METHOD_GET_VARIABLES = "getVariables";
	public static final String METHOD_SET_VAR = "set";
	public static final String METHOD_EVAL = "eval";
	public static final String METHOD_NEXTDATABANK = "getNextDatabankRecord";
	public static final String METHOD_GETDATABANK = "getDatabank";
	
	//encode options
	public static final String OPTION_URL_ENCODE = "URLEncode";
	public static final String OPTION_URL_DECODE = "URLDecode";
	public static final String OPTION_XML_ENCODE = "XMLEncode";
	public static final String OPTION_XML_DECODE = "XMLDecode";
	
	public ScriptASTNodesVisitor(AbstractScriptLoader loader) {
		super(loader);
	}
	
    @Override  
    public boolean visit(FieldDeclaration node) {  
        for (Object obj: node.fragments()) {  
            VariableDeclarationFragment v = (VariableDeclarationFragment)obj;  
            System.out.println("Field:\t" + v.getName());  
        }
        return true;  
    }  
  
    @Override  
    public boolean visit(MethodDeclaration node) {  
    	String methodName = node.getName().getFullyQualifiedName();
        if(METHOD_INIT.equals(methodName) 
        		|| METHOD_RUN.equals(methodName) 
        		|| METHOD_FIN.equals(methodName) ) {
        	//Ignore the sections
        	return true;
        }
        if(loader.getVarProccessors().containsKey(methodName)) {
            if(inlineScripts.containsKey(methodName)) {
            	String code = node.getBody().toString();
            	inlineScripts.get(methodName).setCode(code);
            	return false;
            } else {
            	return false;
            }        	
        }
        System.out.println("Begin Method:\t" + methodName);
        currentActionModule = methodName;
        currentTransaction = new TransactionController(methodName);
        testPlanRoot.getTestPlan().getModuleActions().getMainController().addSubStep(currentTransaction);
        return true;  
    }  
    
    @Override  
    public void endVisit(MethodDeclaration node) {  
    	String methodName = node.getName().getFullyQualifiedName();
        if(METHOD_INIT.equals(methodName) 
        		|| METHOD_RUN.equals(methodName) 
        		|| METHOD_FIN.equals(methodName) ) {
        	//Ignore the sections
        	return;
        }
        System.out.println("End Method:\t" + methodName);
        if(currentActionModule != null && currentActionModule.equals(methodName)){
        	currentActionModule = null;
        }
        modulesToParse.remove(methodName);
        modulesParsed.add(methodName);
    }
  
    @Override  
    public boolean visit(TypeDeclaration node) {  
        System.out.println("Class:\t" + node.getName());  
        return true;
    }
    
    public boolean visit(ForStatement node) {
    	if(node.getExpression() instanceof InfixExpression 
    			&& ((InfixExpression)node.getExpression()).getRightOperand() instanceof NumberLiteral) {
    		NumberLiteral operand = (NumberLiteral)((InfixExpression)node.getExpression()).getRightOperand();
    		String operandStr = operand.toString();
    		WhileController whileCtrl = new WhileController();
    		String varLoopCount = "loop_var_"+ node.getStartPosition();
    		testPlanRoot.getTestPlan().addGlobalVariable(varLoopCount, "0");
    		whileCtrl.setLoopCondition(varLoopCount, operandStr);
    		beginTransaction(whileCtrl);
    	}
    	return true;
    }
    
    public boolean visit(IfStatement node) {
		IfController ifController = new IfController(node.getExpression().toString());
		if(node.getExpression() instanceof InfixExpression  
				&& ((InfixExpression)node.getExpression()).getLeftOperand() instanceof MethodInvocation
				&& ((MethodInvocation)((InfixExpression)node.getExpression()).getLeftOperand()).getExpression() instanceof MethodInvocation) {
			MethodInvocation leftOperandMethod = (MethodInvocation)((InfixExpression)node.getExpression()).getLeftOperand();
			MethodInvocation leftMethod = (MethodInvocation)leftOperandMethod.getExpression();
			if(METHOD_GET_VARIABLES.equals(leftMethod.getName().getFullyQualifiedName())) {
				if(leftOperandMethod.arguments().size() > 0 && leftOperandMethod.arguments().get(0) instanceof StringLiteral) {
					String varName = ((StringLiteral)leftOperandMethod.arguments().get(0)).getLiteralValue();
					ifController.setWaitForVariableCondition(varName);
				} else {
					ifController.setWaitForVariableCondition("UnknownVariable");
				}
			} else {
				ifController.setExpressionCondition(leftOperandMethod.toString());
			}
		} else if(node.getExpression() instanceof BooleanLiteral) {
			boolean value = ((BooleanLiteral)node.getExpression()).booleanValue();
			ifController.setTrueFalseCondition(value);
		} else {
			ifController.setExpressionCondition(node.getExpression().toString());
		}
		beginTransaction(ifController);
		return true;
    }
    
    public void endVisit(IfStatement node) {
    	endTransaction();
    }
    
    
    public void endVisit(ForStatement node) {
    	endTransaction();
    }
    
    public boolean visit(WhileStatement node) {
		return true;
    }
    
    public void endVisit(WhileStatement node) {
    	endTransaction();
    }
    
    public boolean visit(MethodInvocation node) {
    	String methodName = node.getName().getFullyQualifiedName();
    	if(METHOD_SETUSERAGENT.equals(methodName)) {
    		handleSetClient(node);
    		return false;
    	} else if(METHOD_SETLANGUAGE.equals(methodName)) {
    		handleSetLanguage(node);
    		return false;
    	} else if(METHOD_BEGINSTEP.equals(methodName)) {
    		handleBeginStep(node);
    		return true;
    	} else if(METHOD_ENDSTEP.equals(methodName)){
    		handleEndStep(node);
    		return false;
		} else if(METHOD_POST.equals(methodName)) {
    		currentHTTPSampler = handlePostRequest(node);
    		return false;
    	} else if(METHOD_GET.equals(methodName)) {
    		if(node.getExpression() instanceof SimpleName 
    				&& "http".equals(((SimpleName)node.getExpression()).getFullyQualifiedName())){
        		currentHTTPSampler = handleGetRequest(node);
        		return false;
    		} else {
    			handleGetVariables(node);
    			return false;
    		}
    	} else if (METHOD_NAVIGATE.equals(methodName)) {
    		currentHTTPSampler = handleNavigateRequest(node);
    		return false;
    	} else if(METHOD_MULTIPART.equals(methodName)){
    		currentHTTPSampler = handleMultipartPostRequest(node);
    		return false;
    	} else if(METHOD_SOLVE_XPATH.equalsIgnoreCase(methodName)){
    		handleSolveXpath(node);
    		return false;
    	} else if(METHOD_SOLVE_REGEX.equals(methodName)){
    		handleSolveRegex(node);
    		return false;
		} else if(METHOD_SOLVE_HEADER.equals(methodName)){
    		handleSolveHeader(node);
    		return false;
		} else if(METHOD_ADD_COOKIE.equals(methodName)){
    		handleAddCookie(node);
    		return false;
		} else if(METHOD_SET_VAR.equals(methodName)){
			if(node.getExpression() instanceof MethodInvocation 
					&& METHOD_GET_VARIABLES.equals(((MethodInvocation)node.getExpression()).getName().getFullyQualifiedName())){
				handleSetVariables(node);
				return false;
			} else {
				System.out.println("warn: unknown set api");
			}
		} else if(METHOD_NEXTDATABANK.equals(methodName)){
			handleGetNextDatabank(node);
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
    
    private void handleGetNextDatabank(MethodInvocation node) {
    	Expression expression = node.getExpression();
    	if(expression instanceof MethodInvocation 
    			&& METHOD_GETDATABANK.equals(((MethodInvocation)expression).getName().getFullyQualifiedName())) {
    		List<Expression> arguments = ((MethodInvocation)expression).arguments();
    		if(arguments.size() < 1 || !(arguments.get(0) instanceof StringLiteral)){
    			System.out.println("warn: cannot recognize databank name:" + expression.toString());
    		} else {
    			addSubStep(((ATSScriptLoader)loader).loadDatabankByName(((StringLiteral)arguments.get(0)).getLiteralValue()));
    		}
    	} else {
        	System.out.println("warn: cannot recognize databank api:" + expression.toString());
    	}
    }
    
    private void handleGetVariables(MethodInvocation node) {
    	
    }
    
    private void handleSetVariables(MethodInvocation node) {
    	List<Expression> arguments = node.arguments();
    	if(arguments.size() < 2) {
    		System.out.println("warn: arguments wrong");
    		return;
    	}
    	String varName;
    	String varValue;
    	if(arguments.get(0) instanceof StringLiteral) {
    		varName = TransformsUtil.translate(((StringLiteral)arguments.get(0)).getLiteralValue(), encodeOptions, loader.getDatabanks());
    	} else {
    		System.out.println("warn:varname cannot be parsed");
    		return;
    	}
    	if(arguments.get(1) instanceof StringLiteral) {
    		varValue = TransformsUtil.translate(((StringLiteral)arguments.get(1)).getLiteralValue(), encodeOptions, loader.getDatabanks());
    	} else {
    		System.out.println("warn:varvalue cannot be parsed");
    		return;
    	}
    	
    	if(currentHTTPSampler != null) {
        	ElementProperties properties = new ElementProperties(ElementProperties.guiclassArguments);
        	properties.addParameter(varName, varValue);
    		currentHTTPSampler.addPostProcessor(properties);
    	} else {
    		testPlanRoot.getTestPlan().addGlobalVariable(varName, varValue);
    	}
    }
    
    private void handleSolveXpath(MethodInvocation node) {
    	List<Expression> arguments = node.arguments();
    	String varName = null;
    	String xpath = null;
    	String recVal = null;
    	String translatedRegEx = null;
    	int index = 0;
    	
    	if(currentHTTPSampler == null){
    		System.out.println("warn: no http sampler for post processor");
    		return;
    	}
    	
    	if(arguments.size() < 4) {
    		System.out.println("warn:argument number not correct");
    		return;
    	}
    	
    	if(arguments.get(0) instanceof StringLiteral) {
    		varName = TransformsUtil.translate(((StringLiteral)arguments.get(0)).getLiteralValue(), encodeOptions, loader.getDatabanks());
    	} else {
    		System.out.println("warn:varname cannot be parsed");
    		return;
    	}
    	if(arguments.get(1) instanceof StringLiteral) {
    		xpath = TransformsUtil.translate(((StringLiteral)arguments.get(1)).getLiteralValue(), encodeOptions, loader.getDatabanks());
    		translatedRegEx = XPathTranslator.translateXPathToRegex(xpath);
    	} else {
    		System.out.println("warn:xpath cannot be parsed");
    		return;
    	}
    	if(arguments.get(2) instanceof StringLiteral) {
    		recVal = TransformsUtil.translate(((StringLiteral)arguments.get(2)).getLiteralValue(), encodeOptions, loader.getDatabanks());
    	} else {
    		System.out.println("warn:recVal cannot be parsed");
    		recVal="";
    	}
    	if(arguments.get(3) instanceof NumberLiteral) {
    		index = 1+ Integer.valueOf(((NumberLiteral)arguments.get(3)).getToken());
    	} else {
    		System.out.println("warn:index cannot be parsed");
    		index=0;// ZERO means random
    	}
    	
    	if(arguments.size() >4) {
    		Expression arg4 = arguments.get(4);
    		if(arg4 instanceof QualifiedName) {
    			String identifier = ((QualifiedName)arg4).getName().getIdentifier();
    			registerVariableEncodeOption(varName, identifier);
    		}
    		//EncodeOptions.None
    	}
    	if(translatedRegEx != null) {
        	PostProcessor postprocessor = new RegexExtractor(varName, translatedRegEx, "$1$", recVal, String.valueOf(index), false);
        	currentHTTPSampler.addPostProcessor(postprocessor);
    	}
    }
    
    private void handleSolveRegex(MethodInvocation node) {
    	List<Expression> arguments = node.arguments();
    	String varName = null;
    	String pattern = null;
    	boolean isOptional = false;
    	boolean isFromHeader = false;
    	String recVal = "";
    	int index = 0;
    	
    	if(currentHTTPSampler == null){
    		System.out.println("warn: no http sampler for post processor");
    		return;
    	}
    	
    	if(arguments.size() < 2) {
    		System.out.println("warn:argument number not correct");
    		return;
    	}
    	
    	if(arguments.get(0) instanceof StringLiteral) {
    		varName = TransformsUtil.translate(((StringLiteral)arguments.get(0)).getLiteralValue(), encodeOptions, loader.getDatabanks());
    	} else {
    		System.out.println("warn:varname cannot be parsed");
    		return;
    	}
    	if(arguments.get(1) instanceof StringLiteral) {
    		pattern = TransformsUtil.translate(((StringLiteral)arguments.get(1)).getLiteralValue(), encodeOptions, loader.getDatabanks());
    	} else {
    		System.out.println("warn:pattern cannot be parsed");
    		return;
    	}
    	
    	if(arguments.size() > 3 && arguments.get(3) instanceof BooleanLiteral) {
    		isOptional = ((BooleanLiteral)arguments.get(3)).booleanValue();
    	} else {
    		isOptional = false;
    	}
    	if(arguments.size() >4) {
    		Expression arg4 = arguments.get(4);
    		if(arg4 instanceof QualifiedName) {
    			String identifier = ((QualifiedName)arg4).getName().getIdentifier();
    			if("ResponseHeader".equals(identifier)) {
    				//Source.ResponseHeader
    				isFromHeader = true;
    			} else {
    				//Source.Html
    				isFromHeader = false;
    			}
    		}
    	} else {
    		isFromHeader = false;
    	}
    	
    	if(arguments.size() > 5 && arguments.get(5) instanceof NumberLiteral) {
    		index = 1+ Integer.valueOf(((NumberLiteral)arguments.get(5)).getToken());
    	} else {
    		System.out.println("warn:index cannot be parsed");
    		index=1;// first variable
    	}
    	
    	if(arguments.size() > 6 && arguments.get(6) instanceof QualifiedName) {
    		String identifier = ((QualifiedName)arguments.get(6)).getName().getIdentifier();
    		registerVariableEncodeOption(varName, identifier);
    	}
    	
    	PostProcessor postprocessor = new RegexExtractor(varName, pattern, "$1$", recVal, String.valueOf(index), isFromHeader);
    	currentHTTPSampler.addPostProcessor(postprocessor);
	}
    
    private void registerVariableEncodeOption(String variableName, String optionIdentifier){
		if("None".equals(optionIdentifier)) {
			//do nothing
		} else if(OPTION_URL_ENCODE.equals(optionIdentifier)){
			this.encodeOptions.put(variableName, "__urlencode");
		} else if(OPTION_URL_DECODE.equals(optionIdentifier)){
			this.encodeOptions.put(variableName, "__urldecode");
		} else if(OPTION_XML_ENCODE.equals(optionIdentifier)){
			this.encodeOptions.put(variableName, "__escapeHtml");
		} else if(OPTION_XML_DECODE.equals(optionIdentifier)){
			this.encodeOptions.put(variableName, "__unescapeHtml");
		}
    }
    
    private void handleSolveHeader(MethodInvocation node) {
    	List<Expression> arguments = node.arguments();
    	String varName = null;
    	String value = null;
    	if(arguments.size() < 2) {
    		System.out.println("warn: arguments for solveReferer not correct");
    		return;
    	}
    	
    	if(arguments.get(0) instanceof StringLiteral) {
    		varName = TransformsUtil.translate(((StringLiteral)arguments.get(0)).getLiteralValue(), encodeOptions, loader.getDatabanks());
    	} else {
    		System.out.println("warn:varname cannot be parsed");
    		return;
    	}
    	if(arguments.get(1) instanceof StringLiteral) {
    		value = TransformsUtil.translate(((StringLiteral)arguments.get(1)).getLiteralValue(), encodeOptions, loader.getDatabanks());
    	} else {
    		System.out.println("warn:rec value cannot be parsed");
    		return;
    	}
    	
    	String script = "var path = ctx.getCurrentSampler().getPath();vars.put(\""+varName+"\", path);";
    	PostProcessor postprocessor = new BeanShellPostProcessor(varName, "", "", false, script);
    	currentHTTPSampler.addPostProcessor(postprocessor);
    }
    
    private void handleSetClient(MethodInvocation node) {
    	commandHandleHeader(node, "User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
    }
    
    private void handleSetLanguage(MethodInvocation node) {
    	commandHandleHeader(node, "Accept-Language", "en-US");
    }
    
    private void commandHandleHeader(MethodInvocation node, String headerName, String defaultHeaderValue) {
    	List<Expression> arguments = node.arguments();
    	if(arguments.size() < 1) {
    		System.out.println("warn: argument not correct for set header");
    	}
    	String agent;
    	if(arguments.get(0) instanceof StringLiteral) {
    		agent = ((StringLiteral)arguments.get(0)).getLiteralValue();
    	} else {
    		agent = defaultHeaderValue;
    	}
    	testPlanRoot.getTestPlan().addGlobalHeader(headerName, agent);
    }
    
    private void handleAddCookie(MethodInvocation node) {
    	//todo
    }
    
    private HTTPSampler handleMultipartPostRequest(MethodInvocation node) {
    	int id = getRequestID(node);
    	HTTPSampler sampler = new HTTPSampler(id, getUrl(node), getEncoding(node), HTTPSampler.METHOD_POST, true, encodeOptions, loader);
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
    
    private HTTPSampler handleNavigateRequest(MethodInvocation node) {
    	return handleGetRequest(node);
    }
    private HTTPSampler handleGetRequest(MethodInvocation node) {
    	int id = getRequestID(node);
    	HTTPSampler sampler = new HTTPSampler(id, getUrl(node), getEncoding(node), HTTPSampler.METHOD_GET, false, encodeOptions, loader);
    	parseHeaders(node, sampler);
    	parseParams(node, sampler);
    	addSubStep(sampler);
		return sampler;
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
    	List<Expression> methodArguments = node.arguments();
    	MethodInvocation evalNode = null;
    	for(Expression arg : methodArguments){
    		if(arg instanceof MethodInvocation && METHOD_EVAL.equals(((MethodInvocation)arg).getName().getFullyQualifiedName())) {
    			evalNode = (MethodInvocation) arg;
    			break;
    		}
    	}
		List<Expression> arguments = null;
    	if(evalNode == null) {
    		arguments = node.arguments();
    	} else {
    		arguments = evalNode.arguments();
    	}
    	
		String url = null;
		for(Expression argument : arguments) {
			if(argument instanceof StringLiteral) {
				url = ((StringLiteral)argument).getLiteralValue();
				break;
			}
		}
		
		if(url == null) {
			System.out.println("Unable to parse url" + node.toString());
		}
		return url;
    }
    
    private String getEncoding(MethodInvocation node) {
    	List<Expression> arguments = node.arguments();
    	String encoding = "UTF8";
    	int argIndex = arguments.size()-2;
    	if(argIndex >=0 && arguments.get(argIndex) instanceof StringLiteral) {
    		encoding = ((StringLiteral)arguments.get(argIndex)).getLiteralValue();
    	}
    	return encoding;
    }
    
    private void parseHeaders(MethodInvocation node, HTTPSampler httpSampler){
    	List<Expression> arguments = node.arguments();
    	for(Expression argument : arguments) {
    		if(argument instanceof MethodInvocation) {
    			MethodInvocation methodInvoke = (MethodInvocation)argument;
    			if(METHOD_HEADERS.equals(methodInvoke.getName().getFullyQualifiedName())){
    				List<Expression> headerArguments = methodInvoke.arguments();
    				for(Expression headerArg : headerArguments) {
    					if(headerArg instanceof MethodInvocation) {
    						MethodInvocation headerMethodInvoke = (MethodInvocation) headerArg;
    						if(METHOD_HEADER.equals(headerMethodInvoke.getName().getFullyQualifiedName())) {
    							List<Expression> headerNameValues = headerMethodInvoke.arguments();
    							if(headerNameValues.size() >= 2 
    									&& headerNameValues.get(0) instanceof StringLiteral
    									&& headerNameValues.get(1) instanceof StringLiteral) {
    								String headerName = ((StringLiteral)headerNameValues.get(0)).getLiteralValue();
    								String headerValue = ((StringLiteral)headerNameValues.get(1)).getLiteralValue();
    								httpSampler.addHeader(
    										TransformsUtil.translate(headerName, encodeOptions, loader.getDatabanks()), 
    										TransformsUtil.translate(headerValue, encodeOptions, loader.getDatabanks()));
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    }
    	
    private void parseParams(MethodInvocation node, HTTPSampler httpSampler){
    	List<Expression> arguments = node.arguments();
    	for(Expression argument : arguments) {
    		if(argument instanceof MethodInvocation) {
    			MethodInvocation methodInvoke = (MethodInvocation)argument;
    			if(METHOD_POSTDATA.equals(methodInvoke.getName().getFullyQualifiedName())
    					|| METHOD_QUERYSTRING.equals(methodInvoke.getName().getFullyQualifiedName())){
    				List<Expression> paramArguments = methodInvoke.arguments();
    				for(Expression paramArg : paramArguments) {
    					if(paramArg instanceof MethodInvocation) {
    						MethodInvocation paramMethodInvoke = (MethodInvocation) paramArg;
    						if(METHOD_PARAM.equals(paramMethodInvoke.getName().getFullyQualifiedName())) {
    							List<Expression> paramNameValues = paramMethodInvoke.arguments();
    							if(paramNameValues.size() >= 2 
    									&& paramNameValues.get(0) instanceof StringLiteral
    									&& paramNameValues.get(1) instanceof StringLiteral) {
    								String paramname = TransformsUtil.translate(((StringLiteral)paramNameValues.get(0)).getLiteralValue(), encodeOptions, loader.getDatabanks());
    								String paramvalue = TransformsUtil.translate(((StringLiteral)paramNameValues.get(1)).getLiteralValue(), encodeOptions, loader.getDatabanks());
    								httpSampler.addParam( paramname, paramvalue);
    							} else if(paramNameValues.size() == 1 && paramNameValues.get(0) instanceof StringLiteral) {
    								String paramname = TransformsUtil.translate(((StringLiteral)paramNameValues.get(0)).getLiteralValue(), encodeOptions, loader.getDatabanks());
    								httpSampler.addParam(paramname);
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    }
    private void handleBeginStep(MethodInvocation node) {
		List<Expression> arguments = node.arguments();
		String stepName = null;
		for(Expression argument : arguments) {
			if(argument instanceof StringLiteral) {
				stepName = ((StringLiteral)argument).getLiteralValue();
				if(stepName.startsWith("[")) {
					stepName = "Tx"+stepName.replaceAll("\\[|\\]|\\s", "_");
				}
				break;
			}
		}
		beginTransaction(new TransactionController(stepName));

    }
    
    private void handleEndStep(MethodInvocation node) {
    	endTransaction();
    }
}  