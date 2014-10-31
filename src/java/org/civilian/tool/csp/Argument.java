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
			throw new CspException("argument '" + type_ + "' needs a name and type", scanner);
		
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
			throw new CspException("missing argument type", scanner);
		
		// add inner classes
		while(true)
		{
			if (scanner.match("..."))
				break;
			else if (scanner.next("."))
			{
				type += '.';
				String subType = scanner.consumeIdentifier();
				if (subType == null)
					throw new CspException("incomplete type '" + type_ + "'", scanner);
				type += subType;
			}
			else
				break;
		}
		
		// add generic parameter
		if (scanner.next("<"))
			type += parseGenericType(scanner);

		// add arrays
		while (scanner.next("[]"))
			type += "[]";
		
		if (scanner.next("..."))
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
		out.append(";");
	}
	
	
	public void fieldDecl(Appendable out) throws IOException
	{
		out.append("private ");
		out.append(type_);
		if (varArgs_)
			out.append("[]");
		out.append(' ');
		out.append(name_);
		out.append(";");
	}

	
	public String type_;
	public boolean varArgs_;
	public  String name_;
}
