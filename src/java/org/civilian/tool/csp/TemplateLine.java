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


/**
 * Helper class to 
 */
class TemplateLine
{
	private enum IndentChar
	{
		TAB,
		SPACE,
		DETECT
	}
	
	public enum Type
	{
		EMPTY,
		CODE,
		LITERAL,
		COMPONENT_START,
		COMPONENT_END,
	}

	
	public boolean parse(String line)
	{
		this.original		= line;
		this.content 		= "";
		this.error   		= null;
		this.indent	 		= 0;
		this.indentChar_ 	= IndentChar.DETECT;
		this.type 			= Type.EMPTY;
		
		int n = line.length();
		for (int i=0; i<n; i++)
		{
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
					setContent(line.substring(i).trim());
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
	
	
	private void setContent(String line)
	{
		if (line.startsWith("@@"))
		{
			// @@ is a way for template generating templates to start a literal line with a @
			type 	= Type.LITERAL;
			content = line.substring(1);
		}
		else if (line.startsWith("@"))
		{
			type 	= Type.CODE;
			content = line.substring(1);
		}
		else if (line.startsWith("["))
		{
			type 	= Type.COMPONENT_START;
			content = line.substring(1);
		}
		else if (line.startsWith("]"))
		{
			type 	= Type.COMPONENT_END;
			content = line.substring(1).trim();
		}
		else
		{
			type 	= Type.LITERAL;
			content = line;
		}
	}
	
	
	private boolean indentCharDiffers(IndentChar expected, IndentChar actual)
	{
		return (expected != IndentChar.DETECT) && (expected != actual); 
	}
	
	
	public Type type;
	public String error;
	public String content;
	public int indent;
	public String original;
	private IndentChar indentChar_;							// indent char of current line
	private IndentChar prevIndentChar_ = IndentChar.DETECT;	// indent char used in previous lines
}
