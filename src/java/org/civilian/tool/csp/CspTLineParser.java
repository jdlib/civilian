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
import org.civilian.util.StringUtil;


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
			type 	= parseStartSymbol(CspSymbols.code) ? Type.CODE : Type.LITERAL;
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
		if (parseStartSymbol(CspSymbols.componentEnd))
		{
			addLiteralPart(LiteralType.COMPONENT_END, CspSymbols.componentEnd);
			return;
		}
		if (parseStartSymbol(CspSymbols.componentStart))
		{
			int pEnd = scanner_.indexOf(CspSymbols.componentEnd);
			if (pEnd < 0)
			{
				addLiteralPart(LiteralType.COMPONENT_START, scanner_.getRest().trim());
				return;
			}
			else
			{
				String component = scanner_.consumeUpto(pEnd).trim();
				scanner_.skip(CspSymbols.componentEnd.length());
				addLiteralPart(LiteralType.COMPONENT, component, component);
			}
		}
		
		parseLiteralParts(scanner_.getRest().trim());
	}
	
	
	private void parseLiteralParts(String line)
	{
		int length = line.length();
		int start = 0;

		while(start < length)
		{
			int pHat = line.indexOf(CspSymbols.hat, start);
			if (pHat >= 0)
			{
				if (start < pHat)
				{
					addLiteralPart(LiteralType.TEXT, line.substring(start, pHat));
					start = pHat;
				}
				
				int nextChar = pHat + 1 < length ? line.charAt(pHat + 1) : -1;
				if (nextChar == CspSymbols.hat)
				{
					// ^^ detected: output a literal ^
					addLiteralPart(LiteralType.TEXT, CspSymbols.hatString);
					start = pHat + 2;
				}
				else if (nextChar == CspSymbols.noop)
				{
					// ^': produces not content, but useful at line start or to preserve lines
					start = pHat + 2;
				}
				else if (nextChar == CspSymbols.skipln)
				{
					// ^\: skip println, ignore rest of line
					addLiteralPart(LiteralType.SKIPLN, CspSymbols.hatString + CspSymbols.skipln);
					return;
				}
				else
				{
					// (^<name> | ^<name>? | ^{<expr>} | ^{<expr?} | ^{stmt;}
					start = parseCodeSnippet(line, start);
				}
			}
			else
				break;
		}
		if (start < length)
			addLiteralPart(LiteralType.TEXT, line.substring(start, length));
	}
	
	
	/**
	 * Parse (^<name> | ^<name>? | ^{<expr>} | ^{<expr?} | ^{stmt;}
	 * The leading ^ symbol has not been consumed
	 * @param line 
	 * @param start
	 * @return the next start
	 */
	private int parseCodeSnippet(String line, int start)
	{
		Scanner sc = new Scanner(line).setPos(start).setAutoSkipWhitespace(false);
		sc.skip();
		sc.setErrorHandler(scanner_.getErrorHandler());
		
		String snippet;
		String snippetRaw;
		boolean allowStmt;
		boolean isCondition = sc.next('?');
		if (sc.next('{'))
		{
			snippet = StringUtil.norm(sc.consumeUpto("}", false, true, true));
			if (snippet == null)
				scanner_.exception("missing closing '}' at '" + line + "'");
			allowStmt = !isCondition;
		}
		else
		{
			snippet = sc.consumeIdentifier();
			if (snippet == null)
				scanner_.exception("no valid Java identifier found at '" + line + "'");
			allowStmt = false;
		}
		
		snippetRaw = line.substring(start, sc.getPos());
		
		if (allowStmt && snippet.endsWith(";"))
		{
			addLiteralPart(LiteralType.JAVA_STATEMENT, snippetRaw, snippet);
			return sc.getPos();
		}
		else if (isCondition)
		{
			// start of a condition
			addLiteralPart(LiteralType.JAVA_CONDITION_START, snippetRaw, snippet);
			if (!sc.hasMoreChars(3))
				sc.exception("expect at least 3 more chars");
			char openSep = (char)sc.current();
			sc.skip();
			char closeSep;
			switch(openSep)
			{
				case '(': closeSep = ')'; 		break;
				case '[': closeSep = ']'; 		break;
				case '{': closeSep = '}'; 		break;
				case '<': closeSep = '>';		break;
				default:  closeSep = openSep; 	break;
			}
			String conditioned = sc.consumeUpto(String.valueOf(closeSep), false, true, true);
			parseLiteralParts(conditioned);
			addLiteralPart(LiteralType.JAVA_CONDITION_END, "");
			return sc.getPos();
		}
		else
		{
			addLiteralPart(LiteralType.JAVA_EXPRESSION, snippetRaw, snippet);
			return sc.getPos();
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
