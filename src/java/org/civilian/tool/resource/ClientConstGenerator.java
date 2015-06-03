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
package org.civilian.tool.resource;


import java.io.StringWriter;
import org.civilian.Application;
import org.civilian.Resource;
import org.civilian.internal.source.OutputFile;
import org.civilian.internal.source.OutputLocation;
import org.civilian.template.TemplateWriter;
import org.civilian.util.Arguments;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;


/**
 * ClientResConstGenerator is a tool class to generate
 * a Java class which defines constants for all resources of a
 * Civilian application and which can be used in a client program. 
 */
public class ClientConstGenerator
{
	public static final String DEFAULT_ENCODING = "UTF-8";
	private enum Language
	{
		JAVA,
		JAVASCRIPT,
	}
	
	
	public static void main(String[] args) throws Exception
	{
		if (args.length == 0)
			printHelp();
		else
			run(new Arguments(args));
	}
	

	/**
	 * Prints a help screen.
	 */
	private static void printHelp()
	{
		System.out.println("Usage:");
		System.out.println("java " + ClientConstGenerator.class.getName() + " [<parameter>]* <app-class>");
		System.out.println();
		System.out.println("Parameters:                                                      default:");
		System.out.println("-enc <enc>              encoding of generated file               " + DEFAULT_ENCODING);
		System.out.println("-ts <true|false>        print generation timestamp into file     false");
		System.out.println("-v                      turn on verbose messages                 false");
		System.out.println();
		System.out.println("If you want to generate a Java class, then add");
		System.out.println("-java <classname>       generate a Java class for the resource tree");
		System.out.println("-javaout:dir <dir>      write to directory, or");
		System.out.println("-javaout:package <dir>  write to package subdirectory below dir");
//		System.out.println();
//		System.out.println("If you want to generate a Javascript file, then add");
//		System.out.println("-js                     generate a Javascript file");
//		System.out.println("-jsout <file>           write to file");
	}
	

	public static void run(Arguments args) throws Exception
	{
		Options options = new Options();
		while(args.startsWith("-"))
		{
			if (args.consume("-enc"))
				options.encoding = args.next("encoding");
			else if (args.consume("-ts"))
				options.timestamp = args.nextBoolean("timestamp mode");
			else if (args.consume("-v"))
				options.verbose = true;
			else if (args.consume("-java"))
			{
				options.language = Language.JAVA;
				options.javaClass = args.next("java class name");
			}
			else if (args.startsWith("-javaout:"))
			{
				args.replace("-" + args.get().substring(5));
				options.javaOutputLoc = OutputLocation.parse(args, true, true);
			}
			else if (args.consume("-js"))
				options.language = Language.JAVASCRIPT;
			else if (args.consume("-jsout"))
				options.jsOutputLoc = new OutputLocation(OutputLocation.Type.TO_FILE, args.nextFile("script file"));
			else 
				throw new IllegalArgumentException("unknown option " + args.next());
		}
		
		options.setApplication(args.next("application class"));
		
		run(options);
	}
	
	
	public static void run(Options options) throws Exception
	{
		if (options.app == null)
			throw new IllegalArgumentException("missing -app parameter for application class");
		options.complete();
		
		ClientConstGenerator generator = new ClientConstGenerator(options);
		generator.run();
	}

	
	private ClientConstGenerator(Options options) 
	{
		options_ = options;
	}
	
	
	private void run() throws Exception
	{
		log("scanning resources");
		Resource root = options_.app.generateResourceTree(null);
		
		TemplateWriter out = new TemplateWriter(new StringWriter());
		switch(options_.language)
		{
			case JAVA:
				writeJava(root, out);
				break;
			case JAVASCRIPT:
				writeJavascript(root, out);
				break;
		}
	}
	
	
	private void writeJava(Resource root, TemplateWriter out) throws Exception
	{
		String javaPackage 		= ClassUtil.getPackageName(options_.javaClass);
		String javaSimpleClass	= ClassUtil.cutPackageName(options_.javaClass);

		// generate in memory
		ClientJavaTemplate t = new ClientJavaTemplate(root, 
			javaPackage, 
			javaSimpleClass, 
			options_.app, 
			options_.timestamp);
		t.print(out);

		// write to class file
		OutputFile outputFile = options_.javaOutputLoc.getOutputFile(javaPackage, javaSimpleClass + ".java");
		log("writing " + outputFile.file.getAbsolutePath());		
		outputFile.write(options_.encoding, out.toString());
	}
	
	
	private void writeJavascript(Resource root, TemplateWriter out)
	{
		throw new UnsupportedOperationException("not yet implemented");
	}
	

	private void log(String message)
	{
		if (options_.verbose)
			System.out.println(message);
	}
		
	
	/**
	 * Holds options for a generator run.
	 */
	public static class Options
	{
		public Application app;
		public String encoding;
		public boolean timestamp;
		public boolean verbose;
		public Language language;
		public OutputLocation javaOutputLoc;
		public String javaClass;
		public OutputLocation jsOutputLoc;
		
		
		public void setApplication(String className) throws Exception
		{
			Check.notNull(className, "application class name");
			app = ClassUtil.getClass(className, Application.class, null).newInstance();
		}
		
		
		public void complete()
		{
			Check.notNull(app, "app");
			
			if (encoding == null)
				encoding = DEFAULT_ENCODING;

			if (language == null)
				throw new IllegalArgumentException("no language specified");
			switch(language)
			{
				case JAVA:
					if (javaOutputLoc == null)
						throw new IllegalArgumentException("no java output location specified");
					if (javaClass == null)
						throw new IllegalArgumentException("no java class specified");
					break;
				case JAVASCRIPT:
					if (jsOutputLoc == null)
						throw new IllegalArgumentException("no javascript file specified");
					break;
			}
		}
	}
	
	
	private Options options_;
}
