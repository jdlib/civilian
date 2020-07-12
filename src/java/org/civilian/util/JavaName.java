package org.civilian.util;


public class JavaName
{
	public String make(String name)
	{
		return make(name, null, null);
	}
	
	
	public String makeClass(String name)
	{
		return make(name, null, Boolean.FALSE);
	}

	
	public String makeVar(String name)
	{
		return make(name, null, Boolean.TRUE);
	}

	
	public String makeParamClass(String name)
	{
		return make(name, "$", Boolean.FALSE);
	}

	
	public String makeParamVar(String name)
	{
		return make(name, "$", Boolean.TRUE);
	}

	
	private String make(String name, String prefix, Boolean lowercase)
	{
		if (name == null)
			return null;
		
		int length = name.length();
		if (length == 0)
			return null;

		char start = name.charAt(0);
		if (!Character.isJavaIdentifierStart(start))
			return null;
		
		builder_.setLength(0);
		if (prefix != null)
			builder_.append(prefix);
		
		if (lowercase == null)
			builder_.append(start);
		else if (lowercase.booleanValue())
			builder_.append(Character.toLowerCase(start));
		else
			builder_.append(Character.toUpperCase(start));
		
		for (int i=1; i<length; i++)
		{
			char c = name.charAt(i);
			builder_.append(Character.isJavaIdentifierPart(c) ? c : '_');
		}
		return builder_.toString();
	}

	
	private final StringBuilder builder_ = new StringBuilder(); 
}
