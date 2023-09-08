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
package org.civilian.tool.csp;


import java.util.ArrayList;
import java.util.List;
import org.civilian.util.StringUtil;


/**
 * Helper class to parse template lines
 */
class TemplateLine
{
	private enum IndentChar
	{
		TAB,
		SPACE,
		DETECT;
		
		@Override public String toString()
		{
			return name().toLowerCase();
		}
	}
	
	public enum Type
	{
		EMPTY,
		CODE,
		LITERAL,
		COMPONENT_START,
		COMPONENT_END,
	}
	
	
	public enum LiteralType
	{
		TEXT,
		JAVA_EXPR,
		JAVA_STMT,
		JAVA_IF_START,
		JAVA_IF_END,
		SKIPLN
	}
	
	
	public class LiteralPart
	{
		private LiteralPart(LiteralType type, String rawValue, String value)
		{
			this.type = type;
			this.value = value;
			this.rawValue = rawValue;
		}
		
		
		public final LiteralType type;
		public final String value;
		public final String rawValue;
	}
	

	private void reset(String line)
	{
		this.original		= line;
		this.content 		= "";
		this.error   		= null;
		this.indent	= 0;
		this.indentChar_ 	= IndentChar.DETECT;
		this.type 			= Type.EMPTY;
		this.literalParts.clear();
	}

	
	public boolean parse(String line)
	{
		reset(line);
		
		int n = line.length();
		for (int i=0; i<n; i++)
		{
			char c = line.charAt(i);
			switch(line.charAt(i))
			{
				case '\t':
					if (!addIndent(IndentChar.TAB))
						return false; // error
					break;
				case ' ':
					if (!addIndent(IndentChar.SPACE))
						return false; // error
					break;
				default:
					// line has non whitespace content
					parseContent(line.substring(i).trim());
					i = n;
					break;
			}
		}

		return true; // ok
	}
	
	
	private boolean addIndent(IndentChar c)
	{
		if (indentCharDiffers(prevIndentChar_, c) || indentCharDiffers(indentChar_, c))
		{
			error = "template indent may not contain a mix of tab and space chars";
			if (!indentCharDiffers(indentChar_, c))
				error += ": line uses a " + c + " indent character, but previous lines used " + prevIndentChar_ + " characters";
			return false;
		}
		else
		{
			prevIndentChar_ = indentChar_ = c;
			indent++;
			return true;
		}
	}
	
	
	private void parseContent(String line)
	{
		if (tryParseType(line, Type.CODE, CspSymbols.code))
			return;
		if (tryParseType(line, Type.COMPONENT_START, CspSymbols.componentStart))
			return;
		if (tryParseType(line, Type.COMPONENT_END, CspSymbols.componentEnd))
			return;
		
		type 	= Type.LITERAL;
		content = line;
		parseLiteralLine(line);
	}
	
	
	private boolean tryParseType(String line, Type type, String symbol)
	{
		if (line.startsWith(symbol))
		{
			line = StringUtil.cutLeft(line, symbol);
			if (line.startsWith(symbol))
			{
				line = StringUtil.cutLeft(line, symbol);
				type = Type.LITERAL;
			}
			this.type    = type;
			this.content = line;
			return true;
		}
		else
			return false;
	}
	
	
	private void parseLiteralLine(String line)
	{
	}
	
	
	private static boolean indentCharDiffers(IndentChar expected, IndentChar actual)
	{
		return (expected != IndentChar.DETECT) && (expected != actual); 
	}
	
	
	public Type type;
	public String error;
	public String content;
	public int indent;
	public String original;
	public final List<LiteralPart> literalParts = new ArrayList<>();
	private IndentChar indentChar_;							// indent char of current line
	private IndentChar prevIndentChar_ = IndentChar.DETECT;	// indent char used in previous lines
}
