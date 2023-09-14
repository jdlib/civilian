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


import java.io.IOException;
import org.civilian.util.Scanner;


class Argument
{
	public Argument(Scanner scanner)
	{
		type_ = parseType(scanner);

		name_ = scanner.consumeToken(",)");
		if (name_ == null)
			scanner.exception("argument '" + type_ + "' needs a name and type");
		
		while (name_.endsWith("[]"))
		{
			type_ += "[]";
			name_ = name_.substring(0, name_.length()-2).trim();
		}
	}
	
	
	private String parseType(Scanner scanner)
	{
		String type = scanner.consumeIdentifier();
		if (type == null)
			scanner.exception("missing argument type");
		
		// add inner classes
		while(true)
		{
			if (scanner.match("..."))
				break;
			else if (scanner.consume('.'))
			{
				type += '.';
				String subType = scanner.consumeIdentifier();
				if (subType == null)
					scanner.exception("incomplete type '" + type_ + "'");
				type += subType;
			}
			else
				break;
		}
		
		// add generic parameter
		if (scanner.consume('<'))
			type += parseGenericType(scanner);

		// add arrays
		while (scanner.consume("[]"))
			type += "[]";
		
		if (scanner.consume("..."))
			varArgs_ = true;
		
		return type;
	}
	
	
	private String parseGenericType(Scanner scanner)
	{
		StringBuilder gt = new StringBuilder("<");
		int level = 1;
		while(level > 0)
		{
			String s = scanner.consumeUpto("<>", false, true, false);
			
			gt.append(s);
			char delim = (char)scanner.current();
			gt.append(delim);
			level += delim == '<' ? 1 : -1;
			scanner.skip();
		}
		return gt.toString();
	}
	
	
	public void ctorArg(Appendable out) throws IOException
	{
		out.append(type_);
		if (varArgs_)
			out.append("...");
		out.append(' ');
		out.append(name_);
	}
	
	
	public void fieldAssign(Appendable out) throws IOException
	{
		out.append("this.");
		out.append(name_);
		out.append(" = ");
		out.append(name_);
		out.append(';');
	}
	
	
	public void fieldDecl(Appendable out) throws IOException
	{
		out.append("protected ");
		out.append(type_);
		if (varArgs_)
			out.append("[]");
		out.append(' ');
		out.append(name_);
		out.append(';');
	}

	
	public String type_;
	public boolean varArgs_;
	public String name_;
}
