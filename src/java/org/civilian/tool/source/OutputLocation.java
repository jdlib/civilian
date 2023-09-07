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


import java.io.File;
import org.civilian.util.Arguments;
import org.civilian.util.Check;
import org.civilian.util.FileType;


/**
 * Helper class to implement various ways to locate
 * the output file of a Java class generated for a input file (e.g. a template).
 * These types are supported:
 * <ul>
 * <li>write output to the same directory as the input file
 * <li>write output to a specific file
 * <li>write output to a specific directory 
 * <li>write output to the package directory of the class located under
 * 		a specific source root directory
 * </ul>
 */
public class OutputLocation
{
	public static final OutputLocation OUTPUT_TO_INPUT_DIR 	= new OutputLocation(Type.TO_INPUT_DIR);

	
	public static void printHelp(boolean allowSingleFile)
	{
		System.out.println("-out:input           write to directory of the input file        yes");
		System.out.println("-out:dir <dir>       write to directory");
		if (allowSingleFile)
		System.out.println("-out:file <file>     write to file");
		System.out.println("-out:package <dir>   write to package subdirectory below dir");
	}
	
		
	/**
	 * Classifies OutputLocations.
	 */
	public enum Type
	{
		TO_INPUT_DIR (null, "input"),
		TO_FILE      (FileType.FILE,	"file"),
		TO_DIR       (FileType.DIR,		"dir"),
		TO_PACKAGEDIR(FileType.DIR,		"package");
		
		Type(FileType fileType, String arg)
		{
			this.fileType = fileType;
			this.arg = "-out:" + arg;
		}
		
		FileType fileType;
		String arg;
	}
	
	
	public static OutputLocation parse(Arguments args, boolean required, boolean allowSingleFiles)
	{
		Type type = parseType(args);
		
		if ((type == Type.TO_FILE) && !allowSingleFiles)
			type = null;
		
		if (type != null)
		{
			File paramFile = null;
			if (type.fileType != null)
				paramFile = args.nextFile("output argument", type.fileType);
			return new OutputLocation(type, paramFile);
		}
		else if (required)
			throw new IllegalArgumentException("unknown option " + args.next());
		else
			return null;
	}
	
	
	private static Type parseType(Arguments args)
	{
		for (Type type : Type.values())
		{
			if (args.consume(type.arg))
				return type;
		}
		return null;
	}
	
	
	public OutputLocation(Type type)
	{
		this(type, null);
	}
	
	
	public OutputLocation(Type type, File paramFile)
	{
		type_		= Check.notNull(type, "type");
		paramFile_  = paramFile;
		
		if (paramFile != null)
		{
			if (type_.fileType == null)
				throw new IllegalArgumentException("output " + type + " does not have a parameter file");
			else
				type_.fileType.check(paramFile);
		}
		else if (type_.fileType != null)
			throw new IllegalArgumentException("output " + type + " needs parameter file");
	}
	
	
	public boolean needsPackage()
	{
		return type_ == Type.TO_PACKAGEDIR;
	}

	
	public OutputFile getOutputFile(String packageName, String fileName)
	{
		return getOutputFile(packageName, fileName, null);
	}
	
	
	public OutputFile getOutputFile(String packageName, String fileName, File inputFile)
	{
		if (type_ == Type.TO_PACKAGEDIR)
			return getPackageDirOutput(packageName, fileName, inputFile);
		
		File file;
		switch(type_)
		{
			case TO_INPUT_DIR:	if (inputFile == null)		
									throw new IllegalArgumentException("inputFile is null");
								File dir = inputFile.isDirectory() ? inputFile : inputFile.getParentFile();
								file = new File(dir, fileName);
								break;
									
			case TO_FILE:		file = paramFile_;
								break;
									
			case TO_DIR:		file = new File(paramFile_, fileName);
								break;
									
			default:			throw new IllegalStateException("unknown: " + type_);
		}
		return new OutputFile(packageName, file);
	}
	
	
	private OutputFile getPackageDirOutput(String packageName, String fileName, File inputFile)
	{
		if (packageName == null)
		{
			if (inputFile == null)
				throw new IllegalArgumentException("inputFile is null");
			packageName = PackageDetector.DEFAULT.detect(inputFile);
		}
		
		if (packageName != null)
		{
			String subDir 	= packageName.replace('.', '/');
			File packageDir = new File(paramFile_, subDir);
			File file		= new File(packageDir, fileName);
			return new OutputFile(packageName, file);
		}
		else
			return null;
	}
	
	
	private Type type_;
	private File paramFile_;
}
