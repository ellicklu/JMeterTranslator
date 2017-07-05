/**
 * Copyright 1997-2009 Oracle All rights reserved.
 */
package ecd.perf.utilities.expression.seg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ecd.perf.utilities.expression.IRuntimeEvaluatable;
import ecd.perf.utilities.expression.TransformsUtil;

/**
 * Maintains a thread-safe map of variable values keyed off case-insensitive variable names.
 */
public class Variables
{
	public static enum Scope{GLOBAL, PARENT, LOCAL};

	public static String[] specialVariablePrefixes = new String[]{
		"script.",//$NON-NLS-1$
		"obj.",//$NON-NLS-1$
		"system.",//$NON-NLS-1$
		"lib." //$NON-NLS-1$
	};

	public final static String resultVerificationPrefix = "result."; //$NON-NLS-1$


	private final Map<String, Object[]> m_variables;
	private final List<String> m_persistentVars;
	private Scope m_defaultScope = Scope.GLOBAL;


	/**
	 * Ineternal use only.
	 * @param vuser IteratingVUser instance to wrap.
	 */
	public Variables()
	{
		m_variables = new ConcurrentHashMap<String, Object[]>();
		m_persistentVars = new ArrayList<String>();
	}

	public void set(String variableName, String value)
	{
		set(variableName, value, getDefaultScope());
	}

	public void set(String variableName, String value, Scope scope)
	{
		if(isSpecialVariable(variableName)){
			//ignore it
		}

		value = transform(value);

		switch(scope){
		case GLOBAL:
			Variables globalVars = getGlobalVariables();
			globalVars.setAsObjectImpl(variableName, value);
			break;
		case PARENT:
			Variables parVars = getParentVariables();
			parVars.setAsObjectImpl(variableName, value);
			break;
		case LOCAL:
			this.setAsObjectImpl(variableName, value);
			break;
		}
	}

	public void setAsObject(String variableName, Object value)
	{
		setAsObject(variableName, value, getDefaultScope());
	}

	private void setAsObjectImpl(String variableName, Object value) {
		if (variableName == null)
			throw new NullPointerException("variableName"); //$NON-NLS-1$
		if (value == null) {
			remove(variableName);
			return;
		}

		int index = getIndex(variableName);
		String baseVarName = stripIndex(variableName.toLowerCase());
		Object[] vals = m_variables.get(baseVarName);

		if (vals == null) {
			vals = new Object[index + 1];
			vals[index] = value;
			m_variables.put(baseVarName, vals);
		}
		else if (index >= vals.length) {
			Object[] newVals = new Object[index + 1];
			for (int i = 0; i < vals.length; i++) {
				newVals[i] = vals[i];
			}
			newVals[index] = value;
			m_variables.put(baseVarName, newVals);
		}
		else {
			vals[index] = value;
		}
	}
	
	private void pushVariableImpl(String variableName, Object value)
	{
		if (variableName == null)
			throw new NullPointerException("variableName"); //$NON-NLS-1$
		//we already verified that var is not indexed
		Object[] vals = m_variables.get(variableName);
		Object[] newVals;
		if(vals == null){
			newVals = new Object[]{value};
		}
		else{
			int length = vals.length;
			newVals = new Object[length + 1];
			newVals[0] = value;
			System.arraycopy(vals, 0, newVals, 1, length);
		}

		m_variables.put(variableName.toLowerCase(), newVals);
	}

	public void popVariable(String variableName)
	{
		if (variableName == null)
			throw new NullPointerException("variableName"); //$NON-NLS-1$
		//we already verified that var is not indexed
		Object[] vals = m_variables.get(variableName);
		if(vals == null)
			return;

		int length = vals.length;
		if(length == 0)
			return;

		if(length == 1)
			m_variables.remove(variableName.toLowerCase());
		else{
			Object[] newVals = new Object[length -1];
			System.arraycopy(vals, 1, newVals, 0, length - 1);
			m_variables.put(variableName.toLowerCase(), newVals);
		}
	}

	public void setAsObject(String variableName, Object value, Scope scope)
	{
		if(isSpecialVariable(variableName)){
			//ignore
		}

		switch(scope){
		case GLOBAL:
			Variables globalVars = getGlobalVariables();
			globalVars.setAsObjectImpl(variableName, value);
			break;
		case PARENT:
			Variables parVars = getParentVariables();
			parVars.setAsObjectImpl(variableName, value);
			break;
		case LOCAL:
			this.setAsObjectImpl(variableName, value);
			break;
		}
	}

	public void pushVariable(String variableName, Object value, Scope scope)
	{
		if(isSpecialVariable(variableName)){
			//ignore
		}

		if(hasIndex(variableName)){
			return;
		}
		switch(scope){
		case GLOBAL:
			Variables globalVars = getGlobalVariables();
			globalVars.pushVariableImpl(variableName, value);
			break;
		case PARENT:
			Variables parVars = getParentVariables();
			parVars.pushVariableImpl(variableName, value);
			break;
		case LOCAL:
			this.pushVariableImpl(variableName, value);
			break;
		}
	}

	public void setAll(String variableName, String[] values)
	{
		setAllAsObject(variableName, values);
	}

	public void setAll(String variableName, String[] values, Scope scope)
	{
		setAllAsObject(variableName, values, scope);
	}

	public void setAllAsObject(String variableName, Object[] values)
	{
		setAllAsObject(variableName, values, getDefaultScope());
	}

	public void setAllAsObject(String variableName, Object[] values, Scope scope)
	{
		if(isSpecialVariable(variableName)){
			//ignore
		}

		switch(scope){
		case GLOBAL:
			Variables globalVars = getGlobalVariables();
			globalVars.setAllAsObjectImpl(variableName, values);
			break;
		case PARENT:
			Variables parVars = getParentVariables();
			parVars.setAllAsObjectImpl(variableName, values);
			break;
		case LOCAL:
			this.setAllAsObjectImpl(variableName, values);
			break;
		}
	}

	private void setAllAsObjectImpl(String variableName, Object[] values)
	{
		if (variableName == null)
			throw new NullPointerException("variableName"); //$NON-NLS-1$
		String baseVarName = stripIndex(variableName.toLowerCase());
		if (values == null) {
			remove(baseVarName);
			return;
		}
		m_variables.put(baseVarName, values);
	}

	private String getStringValueOfObject(Object obj)
	{
		if(obj == null)
			return null;

		if(obj instanceof IRuntimeEvaluatable) {
			//Evaluate object using runtime variables
			return ((IRuntimeEvaluatable)obj).evaluate(this);
		} else {
			return obj.toString();
		}
	}

	public String get(String variableName)
	{
		if(isSpecialVariable(variableName))
			return getSpecialVariable(variableName);

		Object value = getAsObject(variableName);
		return getStringValueOfObject(value);
	}

	private String getSpecialVariable(String variableName)
	{
		Object value = getSpecialVariableAsObject(variableName);
		return getStringValueOfObject(value);
	}

	private String getSpecialVariable(String variableName, Scope scope)
	{
		Object value = getSpecialVariableAsObject(variableName, scope);
		return getStringValueOfObject(value);
	}

	private Object getSpecialVariableAsObject(String variableName)
	{
		return null;
	}

	private Object getSpecialVariableAsObject(String variableName, Scope scope)
	{
		return null;
	}

	public String get(String variableName, Scope scope)
	{
		if(isSpecialVariable(variableName))
			return getSpecialVariable(variableName, scope);

		Object value = getAsObject(variableName, scope);
		return getStringValueOfObject(value);
	}

	@SuppressWarnings("unused")
	private String getVariableByReflection(String variableName, Scope scope)
	{
		if(isSpecialVariable(variableName))
			return getSpecialVariable(variableName, scope);

		//Get the variable instance based on scope
		Variables scopedVariables = null;
		switch(scope)
		{
			case GLOBAL:
				scopedVariables = getGlobalVariables();
				break;
			case PARENT:
				scopedVariables = getParentVariables();
				break;
			case LOCAL:
			default:
				scopedVariables = this;
				break;
		}

		//Get the variable value
		Object[] vars = scopedVariables.m_variables.get(stripIndex(variableName.toLowerCase()));
		if (vars == null || vars.length == 0)
			return null;
		int index = getIndex(variableName);
		if (index < vars.length) {
			return getStringValueOfObject(vars[index]);
		} else {
			return null;
		}
	}

	/**
	 * Same as {@link #get(String)}, but returns an arbitrary Java object instead of a String.
	 * @see Variables {@link #get(String)}
	 * This method used bubbling search to find a Variable; it starts with Local scope,
	 * bubbles through parent scopes up to global scope.
	 * @param variableName Case-insensitive variable name for the value to retrieve.
	 * @return Object value of the given variable, or NULL if no value could be found in any scope.
	 * @throws AbstractScriptException
	 */
	public Object getAsObject(String variableName)
	{
		if(isSpecialVariable(variableName)){
			return getSpecialVariableAsObject(variableName);
		}

		Object ret = null;
		Variables defaultScopeVars = getDefaultScopeVariables();
		for(Variables currentVars = this; currentVars != defaultScopeVars;
							currentVars = currentVars.getParentVariables()){
			ret = currentVars.getAsObjectImpl(variableName);
			if(ret != null)
				break;
		}

		if(ret != null)
			return ret;
		//try global level as a last resort
		return defaultScopeVars.getAsObjectImpl(variableName);
	}

	public Object getAsObject(String variableName, Scope scope)
	{
		if(isSpecialVariable(variableName)){
			return getSpecialVariableAsObject(variableName, scope);
		}

		return getAsObjectByScope(variableName, scope);
	}

	private Object getAsObjectByScope(String variableName, Scope scope)
	{
		Object ret = null;
		switch(scope)
		{
			case GLOBAL:
				Variables globalVars = getGlobalVariables();
				ret = globalVars.getAsObjectImpl(variableName);
				break;
			case PARENT:
				Variables parVars = getParentVariables();
				ret = parVars.getAsObjectImpl(variableName);
				break;
			case LOCAL:
				ret = this.getAsObjectImpl(variableName);
				break;
		}
		return ret;
	}

	private Object getAsObjectImpl(String variableName)
	{
		if (variableName == null)
			throw new NullPointerException("variableName"); //$NON-NLS-1$

		Object[] vars = m_variables.get(stripIndex(variableName.toLowerCase()));
		if (vars == null || vars.length == 0)
			return null;
		int index = getIndex(variableName);
		if (index < vars.length) {
			onVariableRetrieved(variableName, vars[index]);
			return vars[index];
		}
		else {
			return null;
		}
	}

	public String[] getAll(String variableName)
	{
		String[] ret = null;
		Variables defaultScopeVars = getDefaultScopeVariables();
		for(Variables currentVars = this; currentVars != defaultScopeVars;
							currentVars = currentVars.getParentVariables()){
			ret = currentVars.getAllImpl(variableName);
			if(ret != null)
				break;
		}

		if(ret != null)
			return ret;
		//try global level as a last resort
		return defaultScopeVars.getAllImpl(variableName);
	}

	protected String[] debugGetAll()
	{
		String[] names =  getAllVariableNames();
		if(names == null || names.length == 0)
			return new String[]{""}; //$NON-NLS-1$

		String[] ret = new String[names.length];
		int i = 0;
		for (String name : names){
			String value = ""; //$NON-NLS-1$
			String[] current = getAllImpl(name);
			if(current != null && current.length != 0)
				value = current[0];
			ret[i] = name + "=" + value; //$NON-NLS-1$
			i++;
		}
		return ret;
	}

	public String[] getAll(String variableName, Scope scope)
	{
		String[] ret = null;
		switch(scope){
		case GLOBAL:
			Variables globalVars = getGlobalVariables();
			ret = globalVars.getAllImpl(variableName);
			break;
		case PARENT:
			Variables parVars = getParentVariables();
			ret = parVars.getAllImpl(variableName);
			break;
		case LOCAL:
			ret = this.getAllImpl(variableName);
			break;
		}

		return ret;
	}

	private String[] getAllImpl(String variableName)
	{
		Object[] vals = getAllAsObject(variableName);
		if (vals == null)
			return null;
		String[] sVals = new String[vals.length];
		for (int i = 0; i < vals.length; i++) {
			sVals[i] = getStringValueOfObject(vals[i]);
		}
		return sVals;
	}

	public Object[] getAllAsObject(String variableName)
	{
		Object[] ret = null;
		Variables defaultScopeVars = getDefaultScopeVariables();
		for(Variables currentVars = this; currentVars != defaultScopeVars;
							currentVars = currentVars.getParentVariables()){
			ret = currentVars.getAllAsObjectImpl(variableName);
			if(ret != null)
				break;
		}

		if(ret != null)
			return ret;
		//try global level as a last resort
		return defaultScopeVars.getAllAsObjectImpl(variableName);
	}

	public Object[] getAllAsObject(String variableName, Scope scope)
	{
		Object[] ret = null;
		switch(scope){
		case GLOBAL:
			Variables globalVars = getGlobalVariables();
			ret = globalVars.getAllAsObjectImpl(variableName);
			break;
		case PARENT:
			Variables parVars = getParentVariables();
			ret = parVars.getAllAsObjectImpl(variableName);
			break;
		case LOCAL:
			ret = this.getAllAsObjectImpl(variableName);
			break;
		}

		return ret;
	}

	private Variables getDefaultScopeVariables()
	{
		Scope scope = getDefaultScope();
		switch(scope){
		case GLOBAL:
			return getGlobalVariables();
		case PARENT:
			return getParentVariables();
		case LOCAL:
			return this;
		default:
			return getGlobalVariables();
		}
	}

	private Object[] getAllAsObjectImpl(String variableName)
	{
		if (variableName == null)
			throw new NullPointerException("variableName"); //$NON-NLS-1$

		Object[] vals = m_variables.get(stripIndex(variableName.toLowerCase()));
		if (vals == null || vals.length == 0)
			return null;
		onVariableArrayRetrieved(variableName, vals);
		return vals;
	}

	public String[] getAllVariableNames()
	{
		Set<String> keys = m_variables.keySet();
		return keys.toArray(new String[m_variables.size()]);
	}

	public String[] getAllVariableNames(Scope scope){
		switch(scope){
		case GLOBAL:
			return getGlobalVariables().getAllVariableNames();
		case PARENT:
			return getParentVariables().getAllVariableNames();
		case LOCAL:
			return this.getAllVariableNames();
		default:
			return getGlobalVariables().getAllVariableNames();
		}
	}

	public void addVariables(Variables variables) {
		if(variables == null)
			throw new NullPointerException("variables"); //$NON-NLS-1$
		 m_variables.putAll(variables.m_variables);
	}

	@Deprecated
	public void restoreVariables(Map<String, Object[]> backupVariables)
	{
		if(backupVariables == null)
			return;
		Iterator<String> keyIterator = backupVariables.keySet().iterator();
		while(keyIterator.hasNext()){
			String key = keyIterator.next();
			Object[] values = backupVariables.get(key);
			setAllAsObject(key, values);
		}
	}

	public void clearVariables() {
		// remove non-persistent variables
		Iterator<String> keyIterator = m_variables.keySet().iterator();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			if (!isPersistent(key)) {
				remove(key);
			}
		}
	}

	public Variables getVariablesCopy()
	{
		HashMap<String, Object[]> preservedMap = new HashMap<String, Object[]>();
		preservedMap.putAll(m_variables);

		Variables variables = new Variables();

		Iterator<String> keyIterator = preservedMap.keySet().iterator();
		while(keyIterator.hasNext()){
			String key = keyIterator.next();
			Object[] values = preservedMap.get(key);
			//bug 9526468. When Copy vars from this vars for follow restoration
			//set scope to Local to save them in proper level
			variables.setAllAsObject(key, values, Scope.LOCAL);
		}
		return variables;
	}

	@Deprecated
	public Map<String, Object[]> preserveVariables() {
		HashMap<String, Object[]> preservedMap = new HashMap<String, Object[]>();
		preservedMap.putAll(m_variables);
		return preservedMap;
	}

	public void remove(String variableName)
	{
		remove(variableName, getDefaultScope());
	}

	/**
	 *
	 * @param variableName
	 * @param Scope
	 */
	public void remove(String variableName,Scope scope){
		if (variableName == null) {
			throw new NullPointerException("variableName"); //$NON-NLS-1$
		}

		if (hasIndex(variableName)) {
			int index = getIndex(variableName);
			Object[] vars = getAllAsObject(variableName, scope);
			if (vars != null && index < vars.length) {
				vars[index] = null;
			}

		}
		else {
			// Remove all occurrences of the variable
			switch (scope) {
				case GLOBAL:
					Variables globalVars = getGlobalVariables();
					globalVars.m_variables.remove(variableName.toLowerCase());
					break;
				case PARENT:
					Variables parVars = getParentVariables();
					parVars.m_variables.remove(variableName.toLowerCase());
					break;
				case LOCAL:
					m_variables.remove(variableName.toLowerCase());
					break;
			}
		}
	}

	public void setPersistent(String variableName, boolean isPersistent)
	{
		if(variableName == null){
			return;
		}
		if (!isPersistent && m_persistentVars.contains(variableName)) {
			m_persistentVars.remove(variableName);
		}
		else if (isPersistent && !m_persistentVars.contains(variableName)) {
			m_persistentVars.add(variableName);
		}
	}

	public boolean isPersistent(String variableName)
	{
		return m_persistentVars.contains(variableName);
	}

	public void setDefaultScope(Scope scope)
	{
		m_defaultScope = scope;
	}

	public Scope getDefaultScope()
	{
		return m_defaultScope;
	}

	private Variables getParentVariables()
	{
		return this;
	}

	private Variables getGlobalVariables()
	{
		return this;
	}

	private String stripIndex(String variableName)
	{
		int leftPos = variableName.indexOf('[');
		if (leftPos > 0) {
			int rightPos = variableName.indexOf(']', leftPos + 1);
			if (rightPos > 0) {
				return variableName.substring(0, leftPos);
			}
		}
		return variableName;
	}

	private boolean hasIndex(String variableName)
	{
		int leftPos = variableName.indexOf('[');
		if (leftPos > 0) {
			int rightPos = variableName.indexOf(']', leftPos + 1);
			if (rightPos > 0) {
				return true;
			}
		}
		return false;
	}

	private int getIndex(String variableName)
	{
		try {
			int leftPos = variableName.indexOf('[');
			if (leftPos > 0) {
				int rightPos = variableName.indexOf(']', leftPos + 1);
				if (rightPos > 0) {
					return Integer.parseInt(variableName.substring(leftPos + 1, rightPos));
				}
			}
		}
		catch (Exception e) {
		}
		return 0;
	}

	private String transform(String s)
	{
		if (s == null) {
			return null;
		}
		return TransformsUtil.transform(s, true, false);
	}

	private void onVariableRetrieved(String variableName, Object value) {
		// no action to perform when a variable is retrieved.
	}

	private void onVariableArrayRetrieved(String variableName, Object[] values) {
		// no action to perform when a variable array is retrieved.
	}

	public static boolean isSpecialVariable(String varName)
	{
		if(varName == null)
			return false;

		for(String prefix : specialVariablePrefixes){
				if(varName.startsWith(prefix))
					return true;
		}

		return false;
	}

	public static boolean isResultVerificationVariable(String varName)
	{
		if(varName == null)
			return false;

		if(varName.startsWith(resultVerificationPrefix))
			return true;

		return false;
	}
}

