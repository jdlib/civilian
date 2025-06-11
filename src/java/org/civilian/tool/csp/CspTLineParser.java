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

import org.civilian.util.Check;
import org.civilian.util.Scanner;


/**
 * Helper class to parse template lines
 */
class CspTLineParser
{
	public enum Type
	{
		EMPTY,
		CODE,
		LITERAL;
	}
	
	
	public enum LiteralType
	{
		TEXT,
		JAVA_EXPRESSION,
		JAVA_STATEMENT,
		JAVA_CONDITION_START,
		JAVA_CONDITION_END,
		SKIPLN,
		COMPONENT,
		COMPONENT_START,
		COMPONENT_END,
	}
	
	
	private static final String HAT_SKIPLN = CspSymbols.HATSTRING + CspSymbols.SKIPLN;
	
	
	public class LiteralPart
	{
		private LiteralPart(LiteralType type, String rawValue, String value)
		{
			this.type = type;
			this.value = value;
			this.rawValue = rawValue;
		}
		
		
		@Override public String toString()
		{
			return type + "=" + value;
		}
		
		
		public final LiteralType type;
		public final String value;
		public final String rawValue;
	}
	
	
	public CspTLineParser(Scanner scanner)
	{
		scanner_ = Check.notNull(scanner, "scanner");
		if (scanner.getAutoSkipWhitespace())
		{
			// e.g. problems in parseStartSymbol/scanner_.match when parsing component symbols [
			throw new IllegalArgumentException("cannot work with autoskip whitespace scanner");
		}
	}
	
	
	public int indent()
	{
		return indent_.count;
	}
	

	public void parse()
	{
		this.original = scanner_.getLine();
		this.literalParts.clear();
		
		indent_.parse(scanner_);
		if (scanner_.hasMoreChars())
		{
			type 	= parseStartSymbol(CspSymbols.CODE) ? Type.CODE : Type.LITERAL;
			content = scanner_.getRest().trim();
			if (type == Type.LITERAL)
				parseLiteralLine();
		}
		else
		{
			type  	= Type.EMPTY;
			content = "";
		}
	}
	
	
	private void parseLiteralLine() 
	{
		if (parseStartSymbol(CspSymbols.COMPONENT_END)) // ]
		{
			addLiteralPart(LiteralType.COMPONENT_END, CspSymbols.COMPONENT_END);
			return;
		}
		if (parseStartSymbol(CspSymbols.COMPONENT_START)) // [componentBuilder...
		{
			int pEnd = scanner_.indexOf(CspSymbols.COMPONENT_END); 
			if (pEnd < 0) // [componentBuilder EOL
			{
				addLiteralPart(LiteralType.COMPONENT_START, scanner_.getRest().trim());
				return;
			} 
			else // [componentBuilder] literalParts 
			{
				String component = scanner_.nextUptoPos(pEnd).trim();
				scanner_.skip(CspSymbols.COMPONENT_END.length());
				scanner_.skipWhitespace();
				addLiteralPart(LiteralType.COMPONENT, component, component);
			}
		}
		
		parseLiteralParts();
	}
	
	
	private void parseLiteralParts()
	{
		while(scanner_.hasMoreChars())
		{
			// finde the next position of a '^' character
			int pHat = scanner_.indexOf(CspSymbols.HAT);
			if (pHat >= 0)
			{
				// consume any preceding text
				String literalText = scanner_.nextUptoPos(pHat);
				if (literalText != null)
					addLiteralPart(LiteralType.TEXT, literalText);
				
				// consume '^'
				scanner_.skip();
			
				int nextChar = scanner_.current();
				if (nextChar == CspSymbols.HAT)
				{
					// ^^ seen: output a literal ^
					scanner_.skip();
					addLiteralPart(LiteralType.TEXT, CspSymbols.HATSTRING);
				}
				else if (nextChar == CspSymbols.NOOP)
				{
					// ^' seen: produces no content, but useful at line start or to preserve lines
					scanner_.skip();
				}
				else if (nextChar == CspSymbols.SKIPLN)
				{
					// ^\ seen: skip println, ignore rest of line
					scanner_.skip();
					addLiteralPart(LiteralType.SKIPLN, HAT_SKIPLN);
					return;
				}
				else
				{
					parseCodeSnippet();
				}
			}
			else
				break;
		}
		if (scanner_.hasMoreChars())
		{
			// must not use getRest(), since we want to consume it
			// conditional set a artifical length and later increase it again
			addLiteralPart(LiteralType.TEXT, scanner_.nextRest());
		}
	}
	
	
	/**
	 * Parse an embedded code snippet:
	 * <code>
	 * <ul>
	 * <li>snippet := '^' ( snippet-expr | snippet-statement | snippet-conditional )
	 * <li>snippet-expression := java-variable-name | '{' java-expression '}'
	 * <li>snippet-statement := '{' (java-statement ';')+ '}'
	 * <li>snippet-conditional := '?' (boolean-java-variable-name | '{' boolean-java-expression '}'
	 * </ul>
	 * </code>
	 * The leading ^ symbol has already been consumed
	 * @param line 
	 * @param start
	 */
	private void parseCodeSnippet()
	{
		int hatPos = scanner_.getPos() - 1;
		String snippet;
		String snippetRaw;
		boolean allowStmt;
		boolean isCondition = scanner_.next('?');
		if (scanner_.next('{'))
		{
			snippet = scanner_.expect().nextUpto("}", false, true, true, true);
			allowStmt = !isCondition;
		}
		else
		{
			snippet = scanner_.expect().nextIdentifier();
			allowStmt = false;
		}
		
		snippetRaw = scanner_.getLine().substring(hatPos, scanner_.getPos());
		
		if (allowStmt && snippet.endsWith(";"))
		{
			addLiteralPart(LiteralType.JAVA_STATEMENT, snippetRaw, snippet);
		}
		else if (isCondition)
		{
			// start of a condition
			addLiteralPart(LiteralType.JAVA_CONDITION_START, snippetRaw, snippet);
			if (!scanner_.hasMoreChars(3))
				scanner_.exception("expect at least 3 more chars");
			char openSep = (char)scanner_.current();
			scanner_.skip();
			char closeSep;
			switch(openSep)
			{
				case '(': closeSep = ')'; 		break;
				case '[': closeSep = ']'; 		break;
				case '{': closeSep = '}'; 		break;
				case '<': closeSep = '>';		break;
				default:  closeSep = openSep; 	break;
			}
			int closePos = scanner_.indexOf(closeSep);
			int oldLength = scanner_.getLength();
			scanner_.setLength(closePos);
			parseLiteralParts();
			addLiteralPart(LiteralType.JAVA_CONDITION_END, "");
			scanner_.setLength(oldLength);
			scanner_.skip(); // closeSep
		}
		else
		{
			addLiteralPart(LiteralType.JAVA_EXPRESSION, snippetRaw, snippet);
		}
	}
	
	
	private void addLiteralPart(LiteralType type, String value)
	{
		literalParts.add(new LiteralPart(type, value, value));
	}
	
	
	private void addLiteralPart(LiteralType type, String rawValue, String value)
	{
		literalParts.add(new LiteralPart(type, rawValue, value));
	}
	
	
	/**
	 * Tests if the line starts with the given symbol and consumes it if so.
	 * But if the symbol is repeated twice it means that it should not act as a start symbol
	 * but should be printed literally.
	 */
	private boolean parseStartSymbol(String symbol)
	{
		return scanner_.next(symbol) && !scanner_.match(symbol);
	}
	
	
	public Type type;
	public String content;
	public String original;
	public final List<LiteralPart> literalParts = new ArrayList<>();
	private final Scanner scanner_;
	private final CspIndent indent_ = new CspIndent();
}
