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
package org.civilian.tool.source;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.civilian.util.Check;
import org.civilian.util.StringUtil;


/**
 * JavaPackageDetector helps to detect the java package for a source folder.
 * It scans the files in the folder and if there is a Java file,
 * it tries to extract the package from it.
 * If this fails the parent directory is searched.
 * If a parent directory has the name of a known Java source folders (e.g. src,
 * src.* or java) the search also terminates.
 */
public class JavaPackageDetector
{
	public static final JavaPackageDetector DEFAULT = of("src", "java", "src\\.[.]+");  
	private static final Pattern PACKAGE_PATTERN = Pattern.compile("^package\\s+(.*);$");
	
	
	public static JavaPackageDetector of(String... rootPatterns)
	{
		Pattern[] patterns = new Pattern[rootPatterns.length];
		for (int i=0; i<rootPatterns.length; i++)
			patterns[i] = Pattern.compile(rootPatterns[i]);
		return new JavaPackageDetector(patterns);
	}

	
	public JavaPackageDetector(Pattern... rootPatterns)
	{
		rootPatterns_ = Check.notNull(rootPatterns, "rootPatterns");
	}

	
	public String detect(File directory)
	{
		Check.notNull(directory, "directory");
		if (!directory.isDirectory())
			directory = directory.getParentFile();
		
		return detectPackage(directory, "");
	}
	
	
	private String detectPackage(File directory, String suffix)
	{
		while (directory != null)
		{
			if (isRootDir(directory))
				return StringUtil.cutLeft(suffix, ".");
			
			File[] files = directory.listFiles();
			if (files != null)
			{
				for (File file : files)
				{
					if (file.isFile() && file.getName().endsWith(".java"))
					{
						String p = extractFromJavaFile(file);
						if (p != null)
							return p.trim() + suffix;
					}
				}
			}
			suffix = "." + directory.getName() + suffix;
			directory = directory.getParentFile();
		}
		return null; 
	}
	
	
	private String extractFromJavaFile(File file)
	{
		try
		{
			try (BufferedReader r = new BufferedReader(new FileReader(file)))
			{
				String line;
				while((line = r.readLine()) != null)
				{
					Matcher matcher = PACKAGE_PATTERN.matcher(line.trim());
					if (matcher.find())
						return matcher.group(1);
				}
			}
		}
		catch(IOException e)
		{
			// ignore silently
		}
		return null;
	}
	
	
	private boolean isRootDir(File dir)
	{
		String name = dir.getName();
		for (Pattern p : rootPatterns_)
		{
			if (p.matcher(name).matches())
				return true;
		}
		return false;
	}


	private final Pattern[] rootPatterns_;
}
