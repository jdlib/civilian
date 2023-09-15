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
package org.civilian.util.http;


import org.civilian.util.Scanner;


/**
 * HeaderParser helps to parse values of HTTP-Headers,
 * according the following scheme:<p>
 * header ::= header-item (, header-item)*<p>
 * header-item ::= value (; param)*<p>
 * param ::= name = quoted-value | value<p>
 * quoted-value = '"' value + '"'
 */
public class HeaderParser
{
	public enum Token
	{
		START,
		ITEM,
		PARAM,
		END
	}
	

	/**
	 * Creates a new HeaderParser. 
	 */
	public HeaderParser()
	{
		init(null);
	}


	/**
	 * Creates a new HeaderParser. 
	 */
	public HeaderParser(String header)
	{
		init(header);
	}
	
	
	/**
	 * (Re-)init the HeaderParser. 
	 */
	public void init(String header)
	{
		scanner_.input(header);
		scanParams_ = false;
		token_ = Token.START;
	}
	
	
	public Token getToken()
	{
		return token_;
	}
	

	public boolean hasItem(String item)
	{
		return (this.item != null) && this.item.equals(item);
	}
	

	public boolean hasItemIgnoreCase(String item)
	{
		return (this.item != null) && this.item.equalsIgnoreCase(item);
	}

	
	public boolean hasParam(String name)
	{
		return (this.paramName != null) && this.paramName.equals(name);
	}
	

	public boolean hasParamIgnoreCase(String name)
	{
		return (this.paramName != null) && this.paramName.equalsIgnoreCase(name);
	}

	
	/**
	 * Parse the next token.
	 * @return a *_TOKEN constant
	 */
	public Token next()
	{
		scanner_.skipWhitespace();
		if (!scanner_.hasMoreChars())
		{
			item = null;
			return (token_ = Token.END);
		}
		
		if (scanParams_)
		{
			if (scanner_.next(';'))
			{
				// parse parameter
				paramName  = scanner_.consumeUpto("=", false, true, true);
				scanner_.skipWhitespace();
				if (scanner_.current() == '"')
					paramValue = scanner_.nextQuotedString();
				else
					paramValue = scanner_.consumeUpto(" ;,", false, false, false);
				return (token_ = Token.PARAM);
			}
			else
			{	
				scanParams_ = false;
				scanner_.next(',');
			}
		}
		item 		= scanner_.consumeUpto(" ;,", false, false, false);
		scanParams_	= true;
		paramName	= null;
		return (token_ = Token.ITEM);
	}

	
	public String item;
	public String paramName;
	public String paramValue;
	public Token token_;
	private Scanner scanner_ = new Scanner();
	private boolean scanParams_;
}
