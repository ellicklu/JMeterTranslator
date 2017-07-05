/**
 * Copyright 1997-2009 Oracle All rights reserved.
 */
package ecd.perf.utilities.expression;

import java.util.List;
import java.util.Map;

import ecd.perf.utilities.expression.ats.SegmentParserOptions;
import ecd.perf.utilities.expression.seg.Variables;

public abstract class CustomFunction
{
	@Deprecated
	public class MethodArgument
	{
		private String m_argumentName;
		private String m_argumentDescription;
		private boolean m_isOptional;

		public MethodArgument()
		{
		}

		public MethodArgument(String argumentName, String argumentDescription)
		{
			setArgumentName(argumentName);
			setArgumentDescription(argumentDescription);
		}

		public void setArgumentName(String argumentName)
		{
			m_argumentName = argumentName;
		}

		public void setArgumentDescription(String argumentDescription)
		{
			m_argumentDescription = argumentDescription;
		}

		public String getArgumentName()
		{
			return m_argumentName;
		}

		public String getArgumentDescription()
		{
			return m_argumentDescription;
		}

		public boolean isOptional()
		{
			return m_isOptional;
		}

		public void setIsOptional(boolean isOptional)
		{
			m_isOptional = isOptional;
		}
	}

	private String m_id;
	private String m_solverClassName;
	private int m_maxArguments = -1;
	private int m_minArguments = -1;
	private MethodArgument[] m_arguments;

	@Deprecated
	public void setId(String id)
	{
		m_id = id;
	}

	public String getId()
	{
		return m_id;
	}
	public abstract byte[] compute(Variables variables, List<String> customArguments, SegmentParserOptions options);
	
	public static String translateWithEscapeParam(String param){
		if(param != null && param.contains(",")){
			param = param.replaceAll(",", "\\,");
		}
		return param;
	}
	public abstract String translate(List<String> paraValues, Map<String,String> encodeOptions);
	
	public String computeString(Variables variables, List<String> customArguments, SegmentParserOptions options)
	{
		return null;
	}	
}
