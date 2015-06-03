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
import org.civilian.template.TemplateWriter;


class SourceWriter extends TemplateWriter
{
	public SourceWriter(StringWriter out)
	{
		super(out);
		buffer_ = out.getBuffer();
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

	private StringBuffer buffer_; 
}
