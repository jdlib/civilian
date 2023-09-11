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
		JAVA_EXPR,
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
	
	
	public int indent()
	{
		return indent_.count;
	}
	

	public void parse(Scanner scanner)
	{
		this.original = scanner.getLine();
		this.literalParts.clear();
		
		indent_.parse(scanner);
		if (scanner.hasMoreChars())
		{
			type 	= parseStartSymbol(scanner, CspSymbols.code) ? Type.CODE : Type.LITERAL;
			content = scanner.getRest().trim();
			if (type == Type.LITERAL)
				parseLiteralContent(scanner);
		}
		else
		{
			type  	= Type.EMPTY;
			content = "";
		}
	}
	
	
	private void parseLiteralContent(Scanner scanner) 
	{
		if (parseStartSymbol(scanner, CspSymbols.componentEnd))
		{
			addLiteralPart(LiteralType.COMPONENT_END, CspSymbols.componentEnd);
			return;
		}
		if (parseStartSymbol(scanner, CspSymbols.componentStart))
		{
			int pEnd = scanner.indexOf(CspSymbols.componentEnd);
			if (pEnd < 0)
			{
				addLiteralPart(LiteralType.COMPONENT_START, scanner.getRest().trim());
				return;
			}
			else
			{
				String component = scanner.consumeUpto(pEnd).trim();
				scanner.skip(CspSymbols.componentEnd.length());
				addLiteralPart(LiteralType.COMPONENT, component, component);
			}
		}
		
		String line = scanner.getRest().trim();
		int length = line.length();
		int start = 0;

		while(start < length)
		{
			int pHat = line.indexOf(CspSymbols.hat, start);
			int pLtPercent = line.indexOf(CspSymbols.exprStart, start);
			if ((pHat != -1) && (pLtPercent != -1))
			{
				if (pHat < pLtPercent)
					pLtPercent = -1;
				else
					pHat = -1;
			}
			
			if (pHat >= 0)
			{
				if (start < pHat)
					addLiteralPart(LiteralType.TEXT, line.substring(start, pHat));
				
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
					scanner.exception("not yet implemented");
			}
			else if (pLtPercent >= 0)
			{
				if (start < pLtPercent)
					addLiteralPart(LiteralType.TEXT, line.substring(start, pLtPercent));

				int q = line.indexOf(CspSymbols.exprEnd, pLtPercent);

				// code end signal not found
				if (q == -1)
					scanner.exception("closing '" + CspSymbols.exprEnd + "' not found");

				if (q > pLtPercent + 2)
				{
					String snippetRaw  = line.substring(pLtPercent, q + 2);
					String snippetCode = line.substring(pLtPercent + 2, q).trim();

					addLiteralPart(LiteralType.JAVA_EXPR, snippetRaw, snippetCode);
				}
				start = q + 2;
			}
			else
				break;
		}
		if (start < length)
			addLiteralPart(LiteralType.TEXT, line.substring(start, length));
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
	private boolean parseStartSymbol(Scanner scanner, String symbol)
	{
		return scanner.next(symbol) && !scanner.hasNext(symbol);
	}
	
	
	public Type type;
	public String content;
	public String original;
	public final List<LiteralPart> literalParts = new ArrayList<>();
	private final CspIndent indent_ = new CspIndent();
}
