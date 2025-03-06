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


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.civilian.util.IoUtil;
import org.civilian.util.StringUtil;


class TemplateInput
{
	public final File file;

	
	public TemplateInput(File file)
	{
		this.file = file;
	}
	
		
	public String[] readLines(String encoding) throws IOException
	{
		try(Reader reader = new InputStreamReader(new FileInputStream(file), encoding))
		{
			String[] lines = IoUtil.readLines(reader);
			for (int i=0; i<lines.length; i++)
				lines[i] = StringUtil.rtrim(lines[i]);
			return lines;
		}
	}

	public String readAll(String encoding) throws IOException
	{
		try(Reader reader = new InputStreamReader(new FileInputStream(file), encoding))
		{
			return IoUtil.readString(reader);
		}
	}
}
