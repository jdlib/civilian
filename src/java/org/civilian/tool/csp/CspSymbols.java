package org.civilian.tool.csp;


public class CspSymbols
{
	public static final String START_TEMPLATE_SECTION = "{{";
	public static final String END_TEMPLATE_SECTION = "}}";

	public static final String CODE = "@"; 
	public static final String COMPONENT_START = "["; 
	public static final String COMPONENT_END = "]";
	public static final char HAT = '^';
	public static final String HATSTRING = String.valueOf(HAT);
	public static final char NOOP = '\'';
	public static final char SKIPLN = '\\';
	public static final char CONDITIONAL = '?';
}

