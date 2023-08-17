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
package org.civilian.internal.source;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.civilian.util.Check;


/**
 * OutputFile represents a target file of a compiler
 * or generator.
 */
public class OutputFile
{
	public OutputFile(String packageName, File file)
	{
		this.packageName = packageName;
		this.file = Check.notNull(file, "file");
	}
	
	
	public boolean generate(File input)
	{
		return !file.exists() || (file.lastModified() < input.lastModified());
	}
	
			
	public void write(String encoding, String content) throws IOException
	{
		try(OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), encoding))
		{
			out.write(content);
		}
	}

	
	public final File file;
	public final String packageName;
}
