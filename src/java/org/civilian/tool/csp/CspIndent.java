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


import org.civilian.util.Scanner;


public class CspIndent
{
	private enum Char
	{
		TAB,
		SPACE,
		DETECT;
		
		
		private boolean differsFrom(Char other)
		{
			return (this != DETECT) && (other != DETECT) && (this != other); 
		}

		
		@Override public String toString()
		{
			return name().toLowerCase();
		}
	}
	
	
	public void parse(Scanner scanner)
	{
		count 		 = 0;
		currentChar_ = Char.DETECT;
		
		//scanner.
	}
	
	
	private boolean addIndent(Char c)
	{
		if (prevChar_.differsFrom(c) || currentChar_.differsFrom(c))
		{
			error = "template indent may not contain a mix of tab and space chars";
			if (!currentChar_.differsFrom(c))
				error += ": line uses a " + c + " indent character, but previous lines used " + prevChar_ + " characters";
			return false;
		}
		else
		{
			prevChar_ = currentChar_ = c;
			count++;
			return true;
		}
	}

	
	public int count;
	public String error;
	private Char currentChar_;				// char of current line
	private Char prevChar_ = Char.DETECT;	// char used in previous lines
}
