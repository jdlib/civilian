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
import org.civilian.util.Scanner;
import org.civilian.util.StringUtil;


/**
 * Helper class to parse template lines
 */
class TemplateLine
{
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
		this.original	= line;
		this.content 	= "";
		this.indent		= 0;
		this.type 		= Type.EMPTY;
		this.literalParts.clear();
	}

	
	public void parse(Scanner scanner)
	{
		reset(scanner.getLine());
		indent_.parse(scanner);
		indent = indent_.count;
		if (scanner.hasMoreChars())
			parseContent(scanner.getRest().trim());
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
	
	
	public Type type;
	public String content;
	public int indent;
	public String original;
	public final List<LiteralPart> literalParts = new ArrayList<>();
	private final CspIndent indent_ = new CspIndent();
}
