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


import java.io.StringWriter;
import org.civilian.template.CspWriter;


class SourceWriter extends CspWriter
{
	public SourceWriter(StringWriter out, boolean printSrcMap)
	{
		super(out);
		printSrcMap_ 	= printSrcMap;
		buffer_ 		= out.getBuffer();
	}
	
	/**
	 * Starts a new block.
	 */
	public void beginBlock()
	{
		write("{");
		println();
		increaseTab();
	}


	/**
	 * Ends a block.
	 */
	public void endBlock()
	{
		decreaseTab();
		if (!newLineStarted())
			println();
		write("}");
		println();
	}

	public int getColumn()
	{
		int col = 0;
		int i = buffer_.length();
		while(--i >= 0)
		{
			char c = buffer_.charAt(i);
			if ((c == '\r') || (c == '\n'))
				break;
			col += c == '\t' ? 4 : 1; 
		}
		return col;
	}
	
	
	public void printSrcCommentln(String s, int lineIndex)
	{
		if (printSrcMap_ && (s != null))
		{
			int column = getColumn();
			for (int i=column; i<=70; i++)
				print(' ');
			print(" // line ");
			print(lineIndex +  1);
			print(": ");
			print(s.trim());
		}
		println();
	}


	
	private final StringBuffer buffer_;
	private final boolean printSrcMap_;
}
