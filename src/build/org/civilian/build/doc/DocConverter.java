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
package org.civilian.build.doc;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.civilian.template.TemplateWriter;
import org.civilian.util.Arguments;
import org.civilian.util.FileType;
import org.civilian.util.IoUtil;


public class DocConverter
{
	public DocConverter(File javaDocDir)
	{
		javaDocDir_ = javaDocDir;
	}
	
	
	public void convert(File inputFile, File outputDir, boolean force) throws IOException
	{
		File outputFile = new File(outputDir, inputFile.getName());
		if (force || !outputFile.exists() || (outputFile.lastModified() < inputFile.lastModified()))
		{
			System.out.println("convert " + inputFile);
			
			List<String> input = new ArrayList<>(); 
			read(null, inputFile, "", input);	
			
			try(TemplateWriter out = new TemplateWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8)))
			{
				DocTemplate template = new DocTemplate(inputFile.getName(), input);
				template.print(out);
			}
		}
	}
	
	
	private void read(File parentFile, File inputFile, String indent, List<String> lines) throws IOException
	{
		try(Reader in = new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8))
		{
			String[] all = IoUtil.readLines(in, false);
			VarConverter varConverter = new VarConverter(javaDocDir_, parentFile, inputFile); 
			for (int i=0; i<all.length; i++)
			{
				String s = all[i];
				int p = s.indexOf("#include:");
				if (p < 0)
				{
					s = varConverter.convert(s, i);
					lines.add(indent + s);
				}
				else
				{
					String includedFileName = s.substring(p + "#include:".length()).trim();
					File includedFile = new File(inputFile.getParentFile(), includedFileName);
					read(inputFile, includedFile, s.substring(0, p), lines);
				}
			}
		}
	}
	
	
	public static void main(String[] arguments) throws IOException
	{
		Arguments args 	= new Arguments(arguments);
		boolean force   = args.consume("-force");
		File javaDocDir	= args.nextFile("javadoc-dir", FileType.EXISTENT);
		File input 		= args.nextFile("input", FileType.EXISTENT);
		File outputDir 	= args.nextFile("output-dir", FileType.EXISTENT_DIR);
		DocConverter converter = new DocConverter(javaDocDir);
		
		if (input.isDirectory())
		{
			File[] files = input.listFiles();
			if (files != null)
			{
				for (File file : files)
				{
					if (file.getName().endsWith(".html"))
						converter.convert(file, outputDir, force);
				}
			}
		}
		else
			converter.convert(input, outputDir, force);
	}
	
	
	private File javaDocDir_;
}
