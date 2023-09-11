package org.civilian.tool.csp;


public class CspSymbols
{
	public static final String START_TEMPLATE_SECTION = "{{";
	public static final String END_TEMPLATE_SECTION = "}}";

	public static final String code = "@"; 
	public static final String componentStart = "["; 
	public static final String componentEnd = "]";
	public static final String exprStart = "<%";
	public static final String exprStartEscape = "%";
	public static final String exprNoopSuffix = "%>";
	public static final String exprEnd = "%>";
	public static final char hat = '^';
	public static final String hatString = String.valueOf(hat);
	public static final char noop = '\'';
	public static final char skipln = '\\';
	public static final char conditional = '?';
}

