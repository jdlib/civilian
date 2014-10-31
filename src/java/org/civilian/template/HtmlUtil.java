/*
 * Copyright (C) 2014 Civilian Framework.
 *
 * Licensed under the Civilian License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.civilian-framework.org/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.civilian.template;


import java.io.PrintWriter;
import org.civilian.util.Charset;
import org.civilian.util.StringUtil;


/**
 * Provides utility methods to print HTML attributes and text
 */
public class HtmlUtil
{
	/**
	 * Prints a String attribute.
	 * The output is: ' ' name '="' escaped-value '"'.
	 */
	public static void attr(PrintWriter out,String name, String value)
	{
		attr(out, name, value, true);
	}


	/**
	 * Prints an attribute.
	 * The output is: ' ' name '="'value '"'.
	 * @param name the attribute name
	 * @param value the attribute value
	 * @param escape should the attribute value be escaped. Only pass false, if you
	 * 		know that escaping is not needed.
	 */
	public static void attr(PrintWriter out, String name, String value, boolean escape)
	{
		if (name != null)
		{
			out.print(' ');
			out.print(name);
			out.print("=\"");
			if (value != null)
			{
				if (escape)
					escape(out, value, true);
				else
					out.print(value);
			}
			out.print('"');
		}
	}
	
	
	/**
	 * Prints a integer attribute.
	 * The output is: ' ' name '="' value '"'.
	 */
	public static void attr(PrintWriter out,String name, int value)
	{
		attr(out, name, String.valueOf(value), false);
	}
	
	
	/**
	 * Prints attributes.
	 * @param attrs a list of attribute pairs, i.e. a name string immediately followed by its value string.
	 */
	public static void attrs(PrintWriter out, String... attrs)
	{
		if (attrs != null)
		{
			for (int i=0; i<attrs.length; )
			{
				String name 	= attrs[i++];
				String value 	= i < attrs.length ? attrs[i++] : ""; 
				attr(out, name, value);
			}
		}
	}


	/**
	 * Escapes and print the text.
	 */
	public static void text(PrintWriter out, String text)
	{
		escape(out, text, false);
	}


	/**
	 * Escapes a string.
	 * @param out receives the escaped string
	 * @param input the string
	 * @param isAttribute true, if escape rules for attribute, false if for text values should be applied
	 */
	public static void escape(PrintWriter out, String input, boolean isAttribute)
	{
		escape(out, input, isAttribute, null);
	}
	
	
	/**
	 * Escapes a string.
	 * @param out receives the escaped string
	 * @param input the string
	 * @param isAttribute true, if escape rules for attribute, false if for text values should be applied
	 * @param charset if not null, the charset is used to determine if a character is printable
	 * 		in the current encoding. If not a HTML character is printed instead. 
	 */
	public static void escape(PrintWriter out, String input, boolean isAttribute, Charset charset)
	{
		if (input == null)
			return;
		
		int length = input.length();

		for (int i=0; i<length; i++)
		{
			char c = input.charAt(i);
			switch(c)
			{
				case '"': out.print("&quot;");	break;
				case '&': out.print("&amp;");	break;
				case '<': out.print("&lt;");	break;
				case '>': out.print("&gt;");	break;
				case '\n':
					if (isAttribute)
						out.print("&#10;");
					else
						out.print(c);
					break;
				case '\r':
					if (!isAttribute)
						out.print(c);
					continue;
				default:
					if ((charset != null) && !charset.isPrintable(c))
						out.print("&#" + String.valueOf((int)c) + ';');
					else
						out.print(c);
					break;
			}
		}
	}


	/**
	 * Prints a JavaScript string which is embedded in a HTML page.
	 * It escapes \', \n, \r \t and \\ characters and converts
	 * character which are not printable in the current encoding to
	 * a HTML character reference. It prints null, if the text is null
	 * @param text the string
	 * @param addQuotes adds single quote character around the string if true. 
	 */
	public static void jsString(PrintWriter out, String text, boolean addQuotes)
	{
		if (text == null)
			out.print("null");
		else
		{
			if (addQuotes)
				out.print("'");
		
			int length = text.length();
			int start  = 0;
			boolean printCharRef = false;
			String ref = null;
	
			for (int i=0; i<length; i++)
			{
				char c = text.charAt(i);
				switch(c)
				{
					case '\'': ref = "\\\'";	break;
					case '\n': ref = "\\n";		break;
					case '\r': ref = "\\r";		break;
					case '\t': ref = "\\t";		break;
					case '\\': ref = "\\\\";	break;
					default:
						continue;
				}
				out.write(text, start, i - start);
				if (printCharRef)
				{
					out.print("\\u");
					out.print(StringUtil.fillLeft(c, 4));
					out.print(';');
					printCharRef = false;
				}
				else
					out.print(ref);
				start = i+1;
			}	
			out.write(text, start, length - start);
			if (addQuotes)
				out.print("'");
		}
	}
}
