package ecd.perf.utilities.expression.ats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import ecd.perf.utilities.expression.CustomFunction;
import ecd.perf.utilities.expression.CustomFunctionSegment;
import ecd.perf.utilities.expression.seg.FastSegStack;
import ecd.perf.utilities.expression.seg.LiteralSegment;
import ecd.perf.utilities.expression.seg.RecordedSegmentList;
import ecd.perf.utilities.expression.seg.Replacement;
import ecd.perf.utilities.expression.seg.Replacer;
import ecd.perf.utilities.expression.seg.Segment;
import ecd.perf.utilities.expression.seg.SegmentList;
import ecd.perf.utilities.expression.seg.VariableSegment;
import ecd.perf.utilities.model.jmeter.CSVDataSet;

/**
 * Parses {{ }} formatted strings into their individual parts.
 * @since 1.0.2
 */
public class SegmentParser
{
	/**
	 * character types mapped with characters
	 */
	public static final HashMap<Character, CharacterType> RESERVE_CHARS_TYPES_MAP = new HashMap<Character, CharacterType>();
	static {
		RESERVE_CHARS_TYPES_MAP.put('{', CharacterType.LEFT_BRACE_BRACKET);
		RESERVE_CHARS_TYPES_MAP.put('}', CharacterType.RIGHT_BRACE_BRACKET);
		RESERVE_CHARS_TYPES_MAP.put('(', CharacterType.LEFT_BRACKET);
		RESERVE_CHARS_TYPES_MAP.put(')', CharacterType.RIGHT_BRACKET);
		RESERVE_CHARS_TYPES_MAP.put(',', CharacterType.COMMA);
		RESERVE_CHARS_TYPES_MAP.put('@', CharacterType.FUNCTION_PREFIX);
		RESERVE_CHARS_TYPES_MAP.put('#', CharacterType.NOTATION);
	}

	/**
	 * syntax keyword
	 */
	public final static String DOUBLE_LBB = "{{"; //begin of variable //$NON-NLS-1$

	public final static String DOUBLE_RBB = "}}"; //end of variable //$NON-NLS-1$

	public final static String FUNCTION_PREFIX = "@"; //begin of function //$NON-NLS-1$
	
	public final static String DOUBLE_LBB_FUNCTION_PREFIX = "{{@"; //begin of function //$NON-NLS-1$
	
	public final static String DOUBLE_LBB_FUNCTION_PREFIX2 = DOUBLE_LBB_FUNCTION_PREFIX.substring(1);

	public final static String DOUBLE_RBB_FUNCTION_SUFFIX = ")}}"; //end of function //$NON-NLS-1$

	private final static String RECORDED_VALUE_FUNCTION_SUFFIX = "),"; //end of function //$NON-NLS-1$

	private final static String EMPTY_BRACKET_PAIR_FUNCTION_SUFFIX = "()}}"; //end of no parametered function //$NON-NLS-1$

	/**
	 * function map
	 */
	private final static Map<String, CustomFunction> customTransformFunctions = new HashMap<String, CustomFunction>(); // of CustomTransformFunction
	
	/**
	 * get function instance by function id
	 * @param functionId
	 * @return
	 */
	public static CustomFunction getCustomTransformFunction(String functionId)
	{
		return customTransformFunctions.get(functionId);
	}

	/**
	 * register function
	 * @param function
	 */
	public static void addCustomTransformFunction(CustomFunction function)
	{
		if (customTransformFunctions.containsKey(function.getId()))
				return;
		
		customTransformFunctions.put(function.getId(), function);
	}

	/**
	 * get character type
	 * @param currentChar
	 * @return
	 */
	private static CharacterType getCharType(char currentChar)
	{
		if (RESERVE_CHARS_TYPES_MAP.containsKey(currentChar)) {
			return RESERVE_CHARS_TYPES_MAP.get(currentChar);
		}

		if (currentChar == (char) -1) {
			return CharacterType.EOF;
		}

		return CharacterType.LITERAL_CHARACTER;
	}

	/**
	 * get segment type by instance
	 * @param segment
	 * @return
	 * @throws SegmentParserException
	 */
	private static SegmentType getSegmentType(Segment segment)
	{
		if (segment == null) {
			throw new NullPointerException("segment"); //$NON-NLS-1$
		}
		if (segment instanceof LiteralSegment) {
			return SegmentType.LITERAL;
		}
		if (segment instanceof VariableSegment) {
			return SegmentType.VARIABLE;
		}
		if (segment instanceof CustomFunctionSegment) {
			return SegmentType.FUNCTION;
		}
		if (segment instanceof RecordedSegmentList) {
			return SegmentType.RECORDED_LIST;
		}
		if (segment instanceof SegmentList && !(segment instanceof RecordedSegmentList)) {
			return SegmentType.LIST;
		}
		throw new NullPointerException("segment");
	}

	/**
	 * Build the segment string.  Wraps variable with { }'s.
	 */
	public static String buildSegmentString(String varName)
	{
		return buildSegmentString(varName, (Segment) null);
	}
	
	/**
	 * Build the segment string.  Wraps variable and original value with { }'s.
	 * @param varName Variable name. Should not be in well-formed {{ }} format, i.e. may contain unescaped characters.
	 * @param originalValue Variable's recorded value. Should not be in well-formed {{ }} format, i.e. may contain unescaped characters.
	 * @returns a well-formed {{ }} variable expression, with all dangerous characters escaped. 
	 */
	public static String buildSegmentString(String varName, String originalValue)
	{
		return buildSegmentString(varName, buildLiteralSegment(originalValue));
	}
	
	/**
	 * Build the segment string.  Wraps variable and original value with { }'s.
	 * @param varName Variable name. Should not be in well-formed {{ }} format, i.e. may contain unescaped characters.
	 * @param originalValue Variable's recorded value as a segment.
	 * @returns a well-formed {{ }} variable expression, with all dangerous characters escaped. 
	 */
	public static String buildSegmentString(String varName, Segment originalValue)
	{
		varName = maskUnsafeBraceBracketAndSharp(varName);
		if (originalValue != null)
			return DOUBLE_LBB + varName + ',' + originalValue.getCurrentValue() + DOUBLE_RBB;
		else
			return DOUBLE_LBB + varName + DOUBLE_RBB;
	}
	
	/**
	 * Build the segment string.  Wraps function and original value with { }'s.  Functions string may contain 
	 * {{'s which we don't want escaped.  i.e. "@encode({{varName}})"
	 * @param function string. Should be in well-formed {{ }} format.  
	 * @param originalValue Variable's recorded value as a segment.
	 * @returns a well-formed {{ }} variable expression, with all dangerous characters escaped. 
	 */
	public static String buildFunctionSegmentString(String function, Segment originalValue)
	{
		if (originalValue != null)
			return DOUBLE_LBB + function + ',' + originalValue.getCurrentValue() + DOUBLE_RBB;
		else
			return DOUBLE_LBB + function + DOUBLE_RBB;
	}
	
	public static CustomFunctionSegment buildCustomFunctionSegment(String funcName, String ... args)
	{
		Segment[] argSegments = new Segment[args.length];

		for (int i = 0; i < args.length; i++)
			argSegments[i] = new LiteralSegment(maskUnsafeChars(args[i]));
	
		return buildCustomFunctionSegmentEx(funcName, argSegments);
	}
		
	public static CustomFunctionSegment buildCustomFunctionSegmentEx(String funcName, Segment ... argSegments)
	{
		CustomFunctionSegment segment = new CustomFunctionSegment();
		segment.setFunctionId(funcName);
		
		for (Segment argSegment : argSegments)
			segment.addSubSegment(argSegment);
		
		return segment;
	}

	public static LiteralSegment buildLiteralSegment(String value)
	{
		if (value == null)
			return null;
		
		return new LiteralSegment(maskUnsafeBraceBracketAndSharp(value));
	}

	/** Use before rendering a custom transform string to a user
	 *  Perform these substitutions in order:
	 	#{ should become {
	 	#} should become }
	 	#( should become (
	 	#) should become )
	 	#, should become ,
	 	#@ should become @
	 	## should become #
	 */
	public static String unmaskUnsafeChars(String sCustomTransform)
	{
		try {
			int indexOfNotation = sCustomTransform.indexOf('#');
			if (indexOfNotation == -1) {
				return sCustomTransform;
			}
			StringBuilder sb = new StringBuilder(sCustomTransform.length());
			Character nextOne;
			int previousIndexOfNotation = 0;
			while(indexOfNotation >= 0) {
				sb.append(sCustomTransform.substring(previousIndexOfNotation, indexOfNotation));
				if(indexOfNotation + 1 < sCustomTransform.length()) {
					nextOne = sCustomTransform.charAt(indexOfNotation + 1);
					if (isLegalToEscape(nextOne) == 1) {
						sb.append(nextOne);
						indexOfNotation++;
					} else {
						sb.append('#').append(nextOne);
						indexOfNotation++;
					}
					previousIndexOfNotation = indexOfNotation+1;
					indexOfNotation = sCustomTransform.indexOf('#', previousIndexOfNotation);
				} else {
					previousIndexOfNotation = indexOfNotation;
					break;
				}
			}
			sb.append(sCustomTransform.substring(previousIndexOfNotation));
			return sb.toString();
		}
		catch (Exception ex) {
			return sCustomTransform;
		}
	}
	
	/** Use before rendering a custom transform string to a user
	 *  Perform these substitutions in order:
	 *  
	 *  This should typically only be called by a recorder
	 	{ should become #{
	 	} should become #}
	 	( should become #(
	 	) should become #)
	 	, should become #,
	 	@ should become #@
	 	# should become ##
	 */
	public static String maskUnsafeChars(String value)
	{
		try {
			StringBuilder sb = new StringBuilder();
			int l = value.length();
			for (int i = 0; i < l; i++) {
				Character c = value.charAt(i);
				// Check if need to escape character
				if (isLegalToEscape(c) == 1) {
					sb.append('#') ;
				}
				sb.append(c);
			}
			return sb.toString();
		}
		catch (Exception ex) {
			return value;
		}
	}
	
	/** 
	 * See Bug #8737332 - 	don't escape @ ( ) , # unless it is necessary to do so
	 * Use when rendering a literal string outside variable segments or custom function segments.
	 *  Perform these substitutions in order:
	 *  
	 *  This should typically only be called when the string is a literal segment 
	 *  and is outside variable segments or custom function segments
	 	{ should become #{
	 	} should become #}
	 	( should remain
	 	) should remain
	 	, should remain
	 	@ should remain
	 	# should become ##
	 */
	public static String maskUnsafeBraceBracketAndSharp(String value)
	{
		try {
			StringBuilder sb = new StringBuilder(value.length());
			int l = value.length();
			for (int i = 0; i < l; i++) {
				char c = value.charAt(i);
				// Check if need to escape character
				CharacterType type = getCharType(c);
				if (type == CharacterType.LEFT_BRACE_BRACKET
						|| type == CharacterType.RIGHT_BRACE_BRACKET
						|| type == CharacterType.NOTATION) {
					sb.append('#') ;
				}
				sb.append(c);
			}
			return sb.toString();
		}
		catch (Exception ex) {
			return value;
		}
	}
	
	public static String maskAfterCreateRegex(String value)
	{//TODO: modify the value with REGEX for JMETER
//		value=SegmentParser.maskUnsafeBraceBracketAndSharp(MatchUtil.createNewLineRegex
//				(SegmentParser.unmaskUnsafeChars(value)));
		return value;
	}

	/**
	 * check the following character after '#', if it is legal, return the character number should be escaped
	 * @param followingChar 0:not legal, 1:'#{','#}','#(','#)','#,','#@','##' reserved keyword, 2: '\AE' hex-character
	 * @return
	 */
	public static int isLegalToEscape(char... followingChar)
	{
		if (followingChar == null || followingChar.length == 0) {
			return 0;
		}
		CharacterType type = getCharType(followingChar[0]);
		if (type != CharacterType.LITERAL_CHARACTER && type != CharacterType.EOF) {
			return 1;
		}

		if (followingChar.length > 1) {
			char char1 = Character.toUpperCase(followingChar[0]);
			char char2 = Character.toUpperCase(followingChar[1]);
			if (((char1 >= 'A' && char1 <= 'F') || (char1 >= '0' && char1 <= '9'))
					&& ((char2 >= 'A' && char2 <= 'F') || (char2 >= '0' && char2 <= '9'))) {
				return 2;
			}
		}

		return 0;
	}

	/**
	 * Parsing the input string into segments
	 * @param input A properly escaped, parsable string. This value must NOT contain invalid transform syntax.
	 *   Valid inputs:
	 *     ABC#@DEF#{
	 *   Invalid inputs:
	 *     ABC@DEF{
	 * @return parsed segment
	 * @throws Exception 
	 * @throws SegmentParserException if <code>input</code> cannot be parsed
	 */
	public static Segment parse(String input, final HashMap<String, CSVDataSet> databanks) throws Exception
	{
		SegmentParserStatus status = new SegmentParserStatus(input);
		//run the status machine
		PARSING_LOOP:while (status.m_currentStatus != ParserStatus.END && status.m_currentStatus != ParserStatus.ERROR) {
			switch (status.m_currentStatus) {
				case START: //before segment begins
				{
					status.nextChar();
					if (status.m_currentCharType == CharacterType.EOF) {
						//EOF, end parser
						status.m_currentStatus = ParserStatus.END;
					}
					else if (status.m_currentCharType == CharacterType.LEFT_BRACE_BRACKET) {
						//first LBB, peek second char to see if it should be literal
						if (getCharType(status.peek()) != CharacterType.LEFT_BRACE_BRACKET) {
							//not begin of variable or function, push literal segment
							status.pushSegment(new LiteralSegment());
							status.m_currentStatus = ParserStatus.LITERAL_BEGIN;
						}
						else {
							//two RBB found, peek third char to see if it should be function
							if (status.m_currentChar == DOUBLE_LBB_FUNCTION_PREFIX.charAt(0) && status.peekSyntax(DOUBLE_LBB_FUNCTION_PREFIX2)){
								//function prefix found, push function segment
								status.pushSegment(new CustomFunctionSegment());
								status.skipChar(2);//skip {{@
								status.m_currentStatus = ParserStatus.FUNCTION_BEGIN;
							}
							else {
								//variable prefix found, push variable segment
								status.pushSegment(new VariableSegment(databanks));
								status.skipChar(1);//skip {{
								status.m_currentStatus = ParserStatus.VARIABLE_BEGIN;
							}
						}
					}
					else {
						//not begin of variable or function, push literal segment
						status.pushSegment(new LiteralSegment());
						status.m_currentStatus = ParserStatus.LITERAL_BEGIN;
					}
					break;
				}
				case LITERAL_BEGIN: //parsing literal
				{
					CharacterType next_char_type = getCharType(status.peek());
					if (next_char_type == CharacterType.EOF || next_char_type == CharacterType.COMMA
							|| next_char_type == CharacterType.RIGHT_BRACKET || status.peekSyntax(DOUBLE_LBB)
							|| status.peekSyntax(DOUBLE_RBB)) {
						//when meeting with EOF, ',' , ')' , '{{', finish current segment and popup
						status.popSegment();
					}
					else {
						//read next character
						status.m_currentStatus = ParserStatus.LITERAL_BEGIN;
						status.nextChar();
					}
					break;
				}
				case VARIABLE_BEGIN: //parsing variable name
				{
					if (getCharType(status.peek()) == CharacterType.EOF) {
						//EOF is not expected
						status.m_currentStatus = ParserStatus.END;
						throw new Exception(status.m_inputString);
					}
					else if (status.peekSyntax(DOUBLE_RBB)) {
						//meeting with '}}' , variable should be finished and popped up
						status.popSegment();
						status.skipChar(2);
					}
					else if (getCharType(status.peek()) == CharacterType.COMMA) {
						//meeting with ',' , should parse recorded value
						//set recorded value to variable segment
						status.pushSegment(new RecordedSegmentList());
						status.m_currentStatus = ParserStatus.RECORD;
						status.skipChar(1);
					}
					else {
						//continue to parse its child segments (variable name segments)
						status.m_currentStatus = ParserStatus.START;
					}
					break;
				}
				case RECORD: //parsing variable recorded value
				{
					if (status.peekSyntax(DOUBLE_LBB)) {
						if(status.m_bufferedChar.length() > 0){
							//Add previous literal segment
							status.peekSegment().addSubSegment(new LiteralSegment(status.m_bufferedChar.toString()));
						}
						status.m_currentStatus = ParserStatus.START;
					} 
					else if (status.peekSyntax(DOUBLE_RBB)) {
						if(status.m_bufferedChar.length() > 0){
							//Add last literal segment
							status.peekSegment().addSubSegment(new LiteralSegment(status.m_bufferedChar.toString()));
						}
						status.popSegment();
						if (status.peekSegment() instanceof CustomFunctionSegment) {
							status.popSegment();
							status.skipChar(2);
						}
					}
					else {
						//read next character
						status.m_currentStatus = ParserStatus.RECORD;
						status.nextChar();
						if(status.m_currentCharType == CharacterType.EOF)
							throw new Exception(status.m_charPointer + status.m_inputString);
					}
					break;
				}
				case FUNCTION_BEGIN: //parsing function name
				{
					if (getCharType(status.peek()) == CharacterType.EOF) {//EOF
						status.m_currentStatus = ParserStatus.END;
						throw new Exception(status.m_inputString);
					}
					else if (status.peekSyntax(DOUBLE_RBB)) {
						//function end without parameters
						setFunctionId(status.peekSegment(), status.m_bufferedChar.toString(), input);
						status.popSegment();
						status.skipChar(2);
					}
					else if (getCharType(status.peek()) == CharacterType.LEFT_BRACKET) {
						//parameters in function begin with a '('
						if (status.peekSyntax(EMPTY_BRACKET_PAIR_FUNCTION_SUFFIX)) {
							//function end with the pattern : "{{@function_name()}}"
							setFunctionId(status.peekSegment(), status.m_bufferedChar.toString(), input);
							status.popSegment();
							status.skipChar(4);
						}
						else {
							//parameters begin
							setFunctionId(status.peekSegment(), status.m_bufferedChar.toString(), input);
							status.m_currentStatus = ParserStatus.FUNCTION_PARAMETER;
						}
					}
					else if (getCharType(status.peek()) == CharacterType.COMMA) {
						//recorded data begin
						setFunctionId(status.peekSegment(), status.m_bufferedChar.toString(), input);
						status.pushSegment(new RecordedSegmentList());
						status.m_currentStatus = ParserStatus.RECORD;
						status.skipChar(1);
					}
					else {
						//read next character for function name
						status.m_currentStatus = ParserStatus.FUNCTION_BEGIN;
						status.nextChar();
						if(status.m_currentCharType == CharacterType.EOF)
							break PARSING_LOOP;
					}
					break;
				}
				case FUNCTION_PARAMETER: //parsing function parameters
				{
					if (status.peekSyntax(DOUBLE_RBB_FUNCTION_SUFFIX)) {
						//function end with suffix ')}}'
						status.popSegment(); //pop segment list as a parameter
						status.popSegment(); //pop function element
						status.skipChar(3);
					}
					else if (status.peekSyntax(RECORDED_VALUE_FUNCTION_SUFFIX)) {
						//function parameters end with suffix '),'
						status.popSegment(); //pop segment list as a parameter
						status.pushSegment(new RecordedSegmentList());
						status.m_currentStatus = ParserStatus.RECORD;
						status.skipChar(2);
					}
					else if (getCharType(status.peek()) != CharacterType.RIGHT_BRACKET) {
						if (getCharType(status.peek()) == CharacterType.COMMA) {
							//when meet a comma, another parameter will begin
							status.skipChar(1);
							status.popSegment(); //pop segment list as a parameter
							status.pushSegment(new SegmentList()); //push a new segment list as a parameter
						}
						else if (getCharType(status.peek()) == CharacterType.LEFT_BRACKET) {
							//when meet a '(', first parameter will begin
							status.skipChar(1);
							status.m_currentStatus = ParserStatus.START;
							status.pushSegment(new SegmentList()); //push first segment list as a parameter
						}
						else if(getCharType(status.peek()) == CharacterType.EOF) {
							throw new Exception(status.m_charPointer + status.m_inputString);
						}
						else {
							//goto start to parse sub segments
							status.m_currentStatus = ParserStatus.START;
						}
					}
					else {
						//function is not ended properly
						throw new Exception(status.m_charPointer + status.m_inputString);
					}
					break;
				}
			}
		}
		return status.m_rootSegment;
	}
	
	/**
	 * set Function ID into FunctionSegment
	 * @param segment
	 * @throws Exception 
	 * @throws SegmentParserException
	 */
	private static void setFunctionId(Segment segment, String functionID, String inputString) throws Exception
	{
		String functionId = unmaskUnsafeChars(functionID);
		if (segment instanceof CustomFunctionSegment) {
			((CustomFunctionSegment) segment).setFunctionId(functionId);
		}
		else {
			throw new Exception(segment + inputString);
		}
	}
	
	/** Use to remove useless words for var name
	  *	!@#$%^&*()_-=+{}[]|\:;'"/?>.<, `~ are those words
	  * 
	  * Instead of returning a null string, we will return an empty string
	  * doing this if the caller appends the value returned by this function 
	  * to another string the appended string will not contain a literal "null"
	 */
	public static String formatVarName(String original)
	{
		if (original == null) {
			return ""; //$NON-NLS-1$
		}
		StringBuilder result = new StringBuilder();
		String upcaseStr = original.toUpperCase();
		char curChar;
		for (int i = 0; i < original.length(); i++) {
			curChar = upcaseStr.charAt(i);
			if (((curChar >= 'A' && curChar <= 'Z') || (curChar >= '0' && curChar <= '9')) && result.length() <= 25) {
				result.append(original.charAt(i));
			}
		}
		return result.toString();
	}
	
	/**
	 * This works like the split function of String class. The difference is this takes a Segment and splits it 
	 * at each token string within LiteralSegments, returns a list of SegmentList. The main intended usage is to split 
	 * a postdata Segment or a "url with query string" Segment.
	 * The reason we can't simply convert segment to string, and then use string split function to do the 
	 * work is because non-LiteralSegment may contain the token, in which case we should not split the segment.
	 * <p>
	 * For example: <br>
	 * a=b&c={{myVar1,You&Me}}&d=x    <br>
	 * should be split into 3 NV pairs: a=b, c={{myVar1,You&Me}}, d=x  <br>
	 * String split will give us 4 NV pairs.
	 * <p>
	 * If the token exists at the end, i.e.  <br>
	 * a=b&c=d&  <br>
	 * then this method will add an empty string literal segment to the end of the result.
	 * 
	 * @param segment The Segment that contains the string we want to split
	 * @param sToken The delimiter.
	 * @return The split list of Segment.
	 */
	public static List<Segment> splitSegmentList(Segment segment, String sToken) 
	{
		// Flatten the segment, so that there is no token hiding under multiple layers of SegmentList
		List<Segment> lFlatSegList = flattenSegment(segment);

		// The list of SegmentList to hold each parsed value
		List<Segment> result = new ArrayList<Segment>();
		
		// Holds the current value we are parsing
		SegmentList curResult = new SegmentList();
		
		final int sTokenLen = sToken.length();
		for (Segment subSeg : lFlatSegList) {
			if (!(subSeg instanceof LiteralSegment)) {
				// Var or Function seg
				curResult.addSubSegment(subSeg);
			}
			else { // subSeg instanceof LiteralSegment
				String searchString = ((LiteralSegment)subSeg).getLiteralValue(); // i.e. return ABC@DEF{GHI, don't return ABC#@DEF#{GHI
				
				int beginIndex = 0;
				int foundPos = searchString.indexOf(sToken, beginIndex);
				if (foundPos < 0) {
					curResult.addSubSegment(subSeg);
					continue;
				}
				
				while(foundPos >= 0) {
					// i.e. sToken is &, and searchString is ABC@DEF{GHI=A@BC&Param2=@@@Value@@@&a=b
					// the leftSrc is ABC@DEF{GHI=A@BC
					// leftSrc is Param1= which will be segment1 (literalsegment)
					// restOfString = &Param2=@@@Value@@@ which will be segment3 (literalsegment)
					String leftSrc = searchString.substring(beginIndex, foundPos); // i.e. ABC@DEF{GHI=A@BC
					
					curResult.addSubSegment(new LiteralSegment(maskUnsafeChars(leftSrc))); // make ABC@DEF{GHI look like ABC#@DEF#{GHI
					// curResult is finished
					result.add(curResult);
					curResult = new SegmentList();
					
					// Find the next result
					beginIndex = foundPos + sTokenLen;
					foundPos = searchString.indexOf(sToken, beginIndex);
				}
				
				if (beginIndex == searchString.length()) {
					// The token appeared at the end of the string
					// That means there is a "" result after the token
					curResult.addSubSegment(new LiteralSegment("")); //$NON-NLS-1$
				}
				else if (beginIndex <= searchString.length()) {
					// Some more text is left at the end
					curResult.addSubSegment(new LiteralSegment(maskUnsafeChars(searchString.substring(beginIndex))));
				}
			}
		}
		
		if (curResult.size() > 0) {
			result.add(curResult);
		}
		
		return result;
	}
	
	/**
	 * Takes in a Segment object, and make it a list of segments, in which the first level segments 
	 * are non-SegmentList segments.
	 * <p>
	 * For example: <br>
	 * param1={{var1, ABC}}&param2=OPQ{{var2, RST}}XYZ <br>
	 * When this string is parsed into a SegmentList using the parse() method, you will get three top level Segments: <br>
	 * 1. param1=                        (LiteralSegment)
	 * 2. {{var1, ABC}}                  (VariableSegment)
	 * 3. &param2=OPQ{{var2, RST}}XYZ    (SegmentList)
	 * <p>
	 * The last SegmentList has three 2nd level Segment nested in it: <br>
	 * 1. &param2=OPQ                    (LiteralSegment)
	 * 2. {{var2, RST}}                  (VariableSegment)
	 * 3. XYZ                            (LiteralSegment)
	 * <p>
	 * When we need to operate on the Segment object, we'd prefer to only deal with top level Segments to
	 * minimize the complexity of the code. But if we only operate on the top level segments, we 
	 * are likely to miss the contents inside the top level SegmentList. This method is used to flatten 
	 * the Segment before operating on it. For example, after calling this method on the example Segment, it 
	 * eliminates the SegmentList and returns a list of 5 Segments: <br>
	 * 1. param1=                        (LiteralSegment)
	 * 2. {{var1, ABC}}                  (VariableSegment)
	 * 3. &param2=OPQ                    (LiteralSegment)
	 * 4. {{var2, RST}}                  (VariableSegment)
	 * 5. XYZ                            (LiteralSegment)
	 *  
	 * @param segment
	 * @return
	 */
	public static List<Segment> flattenSegment(Segment segment)
	{
		ArrayList<Segment> aSegments = new ArrayList<Segment>();
		if (segment instanceof LiteralSegment ||
			segment instanceof VariableSegment ||
			segment instanceof CustomFunctionSegment){
			aSegments.add(segment);
		}
		else if (segment instanceof SegmentList) {
			for(Segment subSeg: ((SegmentList) segment).getSegments()) {
				aSegments.addAll(flattenSegment(subSeg));
			}
		}
		
		return aSegments;
	}
	
	/**
	 * Find/replace data inside all LiteralSegments of a Segment list.
	 * @param replacer Specifies the logic for what to replace
	 * @param segList SegmentList to modify
	 * @return True if anything was found and replaced. False if nothing was found and replaced.
	 */
	public static boolean literalFindAndReplace(Replacer replacer, SegmentList segList)
	{
		return literalFindAndReplace(replacer, segList, false);
	}
	
	/**
	 * Find/replace data inside all LiteralSegments of a Segment list.
	 * @param replacer Specifies the logic for what to replace
	 * @param segList SegmentList to modify
	 * @param isFound Starts at False. Used to track state during recursive calls.
	 * @return True if anything was found and replaced. False if nothing was found and replaced.
	 */
	private static boolean literalFindAndReplace(Replacer replacer, SegmentList segList, boolean isFound)
	{
		boolean findResult = isFound;

		if (segList.getSegments().size() < 1)
			return false;

		Iterator<Segment> ite = segList.getSegments().iterator();
		while (ite.hasNext()) {
			Segment subSeg = (Segment) ite.next();
			if (subSeg instanceof SegmentList) {
				findResult = literalFindAndReplace(replacer, (SegmentList) subSeg, findResult);
			}
			else if (subSeg instanceof LiteralSegment) {
				String toTransStr = ((LiteralSegment)subSeg).getLiteralValue(); // i.e. return ABC@DEF{GHI, don't return ABC#@DEF#{GHI

				SortedSet<Replacement> replacements = replacer.findReplacements(toTransStr);
				
				if (replacements.size() == 0)
					continue;
				
				int beginIndex = 0;
				SegmentList subList = new SegmentList();
				
				for(Replacement replacement : replacements) {
					// Apply each replacement to the input and construct a new SegmentList containing the result

					// Use start and end offsets for each replacement
					// toTransStr = Param1=A@BC&Param2=@@@Value@@@&a=b
					// leftSrc is Param1= which will be segment1 (literalsegment)
					// replacement = {{pound.rule,A#@BC}} this is segment2 (VariableSegment)
					// restOfString = &Param2=@@@Value@@@ which will be segment3 (literalsegment)
					
					//findText is A@BC, startOffset is 7, endOffset is 11
					int findTextLen = replacement.getEndOffset() - replacement.getStartOffset();
					
					Segment replaceSegment = replacement.getReplacement();
					
					int foundPos = replacement.getStartOffset();
					
					//leftSrc is Param1=
					String leftSrc = toTransStr.substring(beginIndex, foundPos); // i.e. ABC@DEF{GHI
					
					//Add segment1(Param1=) and then segment2({{pound.rule,A#@BC}})
					subList.addSubSegment(new LiteralSegment(maskUnsafeBraceBracketAndSharp(leftSrc))); // make ABC@DEF{GHI look like ABC@DEF#{GHI
					subList.addSubSegment(replaceSegment);

					//Add remaining request as a literal to subList(Param2=@@@Value@@@)
					beginIndex = foundPos + findTextLen;
				}
					
				if (beginIndex < toTransStr.length()) {
					// Some more text is left at the end
					subList.addSubSegment(new LiteralSegment(maskUnsafeBraceBracketAndSharp(toTransStr.substring(beginIndex))));
				}
				
				// Finally replace the original segment with the newly created one
				// and set the return boolean to true indicating request was modified
				segList.replaceSubSegment(subSeg, subList);
				findResult = true;
			}
		}
		return findResult;
	}
	
	public static List<VariableSegment> parseVariables(Segment segment)
	{
		List<VariableSegment> variables = new ArrayList<VariableSegment>();
		parseVariables(segment, variables);
		return variables;
	}
	
	private static void parseVariables(Segment segment, List<VariableSegment> variables)
	{
		if (segment == null)
			return;
		
		if (segment instanceof VariableSegment) {
			VariableSegment varSegment = (VariableSegment) segment;
			variables.add(varSegment);
			
			parseVariables(varSegment.getRecordedSegment(), variables);
		}

		else if (segment instanceof SegmentList) {
			for (Segment childSegment : ((SegmentList) segment).getSegments())
				parseVariables(childSegment, variables);
		}
		
		else if (segment instanceof CustomFunctionSegment) {
			CustomFunctionSegment functionSegment = (CustomFunctionSegment) segment;
			for (Segment childSegment : functionSegment.getParameters())
				parseVariables(childSegment, variables);
			
			parseVariables(functionSegment.getRecordedSegment(), variables);
		}
	}
	
	public static enum CharacterType
	{
		LEFT_BRACE_BRACKET, //{
		RIGHT_BRACE_BRACKET, //}
		LITERAL_CHARACTER, //a-z,0-9 and so on
		LEFT_BRACKET, //(
		RIGHT_BRACKET, //)
		COMMA, //,
		FUNCTION_PREFIX, //@
		NOTATION, // #
		EOF
		//end
	}

	/**
	 * Parser status
	 * @author Ellick
	 *
	 */
	public static enum ParserStatus
	{
		START, //before segment begins
		LITERAL_BEGIN, //parsing literal
		VARIABLE_BEGIN, //parsing variable name
		RECORD, //parsing variable recorded value
		FUNCTION_BEGIN, //parsing function name
		FUNCTION_PARAMETER, //parsing function parameters
		ERROR, //error
		END
		//end
	}
	
	/**
	 * Segment types
	 * @author Ellick
	 *
	 */
	public static enum SegmentType
	{
		LITERAL, //literal segment
		VARIABLE, //custom variable segment
		FUNCTION, //function segment 
		RECORDED_LIST, //recorded segment list
		LIST //list segment
	}

	private static class SegmentParserStatus {
		//original input string
		private final String m_inputString;
		//buffered characters
		private final StringBuilder m_bufferedChar;
		//stack of segments
		private final FastSegStack m_segStack;
		//root segment of parse result
		private final SegmentList m_rootSegment;

		//parser status
		private ParserStatus m_currentStatus;
		//current parsing character
		private char m_currentChar;
		//the char type of current parsing character
		private CharacterType m_currentCharType;
		//offset of current character in the original input string
		private int m_charPointer;
		
		private SegmentParserStatus(String input) {
			//set input string
			m_inputString = input;

			//initialize parser status
			m_currentStatus = ParserStatus.START;
			m_charPointer = 0;
			
			//initialize buffered char
			m_bufferedChar = new StringBuilder();

			//initialize the segments stack
			m_segStack = new FastSegStack(10);
			m_rootSegment = new SegmentList();
			pushSegment(m_rootSegment);
		}
		
		/**
		 * read next character into buffer and increase the pointer
		 * @throws SegmentParserException 
		 *
		 */
		private void nextChar()
		{
			m_currentChar = getCharAt(m_charPointer++);
			m_currentCharType = getCharType(m_currentChar);
			m_bufferedChar.append(m_currentChar);
			checkAndSkipNotation();//escape special words such as '\\', '\@', '\AE'
		}

		/**
		 * get the character in the string by offset
		 * @param offset
		 * @return
		 */
		private char getCharAt(int offset)
		{
			if (m_inputString == null || m_inputString.length() == 0)
				return (char) -1;
			if (offset < 0 || offset >= m_inputString.length())
				return (char) -1;

			return m_inputString.charAt(offset);
		}
		
		/**
		 * check the notation, and skip it
		 * @throws SegmentParserException
		 */
		private void checkAndSkipNotation()
		{
			if (m_currentCharType == CharacterType.NOTATION) {
				int skip_num;
				if(m_inputString == null 
						|| m_inputString.length() == 0
						|| m_charPointer >= m_inputString.length() )
					skip_num = 0;
				else if(m_charPointer + 2 > m_inputString.length())
					skip_num = isLegalToEscape(peek());
				else
					skip_num = isLegalToEscape(peek(), getCharAt(m_charPointer+1));
					
				if (skip_num  != 0) {
					for (int i = 0; i < skip_num; i++) {
						m_currentChar = getCharAt(m_charPointer++);
						m_bufferedChar.append(m_currentChar);
					}
					m_currentCharType = CharacterType.LITERAL_CHARACTER;
				}
				else {
					//throw SegmentParserException.createInvalidCharactersInSegment(peek(2));
					//do nothing
				}
			}
		}
		
		/**
		 * skip one char without parsing its type and storing it into buffer
		 * @throws SegmentParserException 
		 *
		 */
		private void skipChar(int count)
		{
			if (count <= 0)
				return;

			for (int i = 0; i < count; i++) {
				nextChar();
			}
			//Originally 'm_bufferedChar = new StringBuilder();'
			m_bufferedChar.delete(0, m_bufferedChar.length());
		}
		
		/**
		 * peek next character
		 * @return
		 */
		private char peek()
		{
			return getCharAt(m_charPointer);
		}
		
		/**
		 * peek whether next several characters match the given syntax
		 * @param checkSyntax Syntax to be matched
		 * @return TRUE: if the given syntax were matched, FALSE: if the given syntax was not matched or the input string was empty
		 */
		private boolean peekSyntax(String checkSyntax)
		{
			if (m_inputString == null || m_inputString.length() == 0)
				return false;

			if(checkSyntax == null || checkSyntax.length() < 1)
				return false;
			
			if (m_charPointer >= m_inputString.length())
				return false;

			if (m_charPointer + checkSyntax.length() > m_inputString.length())
				return false;

			for(int i = 0; i < checkSyntax.length(); i++) {
				if(m_inputString.charAt(m_charPointer + i) != checkSyntax.charAt(i))
					return false;
			}
			return true;
		}
		
		/**
		 * set the segment value and pop up it from stack and set the next status of parser
		 * @return next status
		 * @throws Exception 
		 * @throws SegmentParserException
		 */
		private void popSegment() throws Exception
		{
			//create instance
			Segment segment = m_segStack.pop();
			SegmentType type = getSegmentType(segment);

			switch (type) {
				case LITERAL:
					((LiteralSegment) segment).setLiteralValue(m_bufferedChar.toString());
					break;
				case VARIABLE:
					break;
				case FUNCTION:
					break;
				case LIST:
					break;
			}

			//clear char buffer //Originally 'm_bufferedChar = new StringBuilder()'
			m_bufferedChar.delete(0, m_bufferedChar.length());

			//put the poped segment into its parent segment
			Segment parent = peekSegment();
			/*
			 * If current is Literal Segment, try to find the last previous Sibling. 
			 * If it is also Literal Segment, try to combine them instead of add a new sub-segment.
			 * Because ')' ',' '(' '@' are not escaped out of {{ }} now, so String with ) would be separated.
			 * Such as "http://demo/imart/system(2f)security(2f)user(2f)600(2f)main.jssps" would be divided into 5 sub-segments which break the correlation rule.
			 */
			if (getSegmentType(parent) == SegmentType.LIST && type == SegmentType.LITERAL) {
				/*
				 * Get last segment in segment list and check if it's Literal segment.
				 * If last segment is literal, combine current literal and last literal, don't add new sub segment.
				 */
				List<Segment> segList = ((SegmentList) parent).getSegments();
				Segment lastSeg = segList.size() > 0 ? segList.get(segList.size() - 1) : null;
				if (lastSeg != null && getSegmentType(lastSeg) == SegmentType.LITERAL) {
//					String parsableString = ((LiteralSegment) lastSeg).getCurrentValue() + ((LiteralSegment) segment).getCurrentValue();
					String lastSegStr = ((LiteralSegment) lastSeg).getCurrentValue();
					String segStr = ((LiteralSegment) segment).getCurrentValue();
					String parsableString = new StringBuilder(lastSegStr.length() + segStr.length()).append(lastSegStr).append(segStr).toString();// give a specific length to improve performance
					LiteralSegment newSeg = new LiteralSegment(parsableString);
					((SegmentList) parent).replaceSubSegment(lastSeg, newSeg);
				} else {
					if(type == SegmentType.RECORDED_LIST){
						if(getSegmentType(parent) == SegmentType.VARIABLE){
							((VariableSegment)parent).setRecordedValue(segment);
						} else if(getSegmentType(parent) == SegmentType.FUNCTION){
							((CustomFunctionSegment)parent).setRecordedValue(segment);
						}
					} else {
						parent.addSubSegment(segment);
					}
				}	
			}
			else {
				if(type == SegmentType.RECORDED_LIST){
					if(getSegmentType(parent) == SegmentType.VARIABLE){
						((VariableSegment)parent).setRecordedValue(segment);
					} else if(getSegmentType(parent) == SegmentType.FUNCTION){
						((CustomFunctionSegment)parent).setRecordedValue(segment);
					}
				} else {
					parent.addSubSegment(segment);
				}
			}

			//set next status
			switch (getSegmentType(parent)) {
				case LITERAL:
					throw new Exception(parent + m_inputString);
				case VARIABLE:
					m_currentStatus = ParserStatus.VARIABLE_BEGIN;
					break;
				case FUNCTION:
					m_currentStatus = ParserStatus.FUNCTION_PARAMETER;
					break;
				case RECORDED_LIST:
					m_currentStatus = ParserStatus.RECORD;
					break;
				case LIST:
					if (m_segStack.size() >= 2 && getSegmentType(m_segStack.get(m_segStack.size() - 2)) == SegmentType.FUNCTION) {
						m_currentStatus = ParserStatus.FUNCTION_PARAMETER;
					} else {
						m_currentStatus = ParserStatus.START;
					}
					break;
				default:
					m_currentStatus = ParserStatus.START;
			}
		}
		
		/**
		 * peek segment
		 * @return segment on the top of the stack
		 */
		private Segment peekSegment() {
			return m_segStack.peek();
		}
		
		/**
		 * push segment
		 * @param segment push to the top of stack
		 */
		private void pushSegment(Segment segment) {
			m_segStack.push(segment);
		}
	}
}
