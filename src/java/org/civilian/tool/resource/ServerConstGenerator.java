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


import java.io.IOException;
import java.io.StringWriter;
import org.civilian.application.Application;
import org.civilian.resource.scan.ResourceInfo;
import org.civilian.resource.scan.ResourceScan;
import org.civilian.resource.scan.ScanException;
import org.civilian.template.TemplateWriter;
import org.civilian.tool.source.OutputFile;
import org.civilian.tool.source.OutputLocation;
import org.civilian.util.Arguments;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;
import org.civilian.util.StringUtil;


/**
 * A command line tool to generate a class 
 * which defines constants for all resources of a Civilian application. 
 * Run the tool without any arguments to print a detailed help message.
 */
public class ServerConstGenerator
{
	/**
	 * The default encodig of the generated file.
	 */
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	
	/**
	 * Runs the ResConstGenerator.
	 */
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
		System.out.println("Scans the classpath for controller classes and builds the resource tree.");
		System.out.println("If an -out parameter is provided, a Java class file which builds the resource tree");
		System.out.println("is generated.");
		System.out.println();
		System.out.println("Usage:");
		System.out.println("java " + ServerConstGenerator.class.getName() + " [<parameters>]* <app-class>");
		System.out.println();
		System.out.println("Parameters:                                                      default:");
		System.out.println("-name <name|qname>   name of generated class                     (derived from app name)");
		System.out.println("-enc                 encoding of generated file                  " + DEFAULT_ENCODING);
		System.out.println("-out:dir <dir>       write to directory, or");
		System.out.println("-out:package <dir>   write to package subdirectory below dir");
		System.out.println("-out:file <file>     write to file");
		System.out.println("-ts                  add comment with generation timestamp");
		System.out.println("-v                   turn on verbose messages");
	}
	

	public static void run(Arguments args) throws Exception
	{
		Options options = new Options();
		while(args.startsWith("-"))
		{
			if (args.consume("-name"))
				options.outputClass = args.next("generated class");
			else if (args.consume("-enc"))
				options.encoding = args.next("encoding");
			else if (args.startsWith("-out:"))
				options.outputLocation = OutputLocation.parse(args, true, true);
			else if (args.consume("-ts"))
				options.timestamp = true;
			else if (args.consume("-v"))
				options.verbose = true;
			else 
				throw new IllegalArgumentException("unknown option " + args.next());
		}
		
		options.setApplication(args.next("application class"));
		
		run(options);
	}
	
	
	public static void run(Options options) throws IOException
	{
		if (options.app == null)
			throw new IllegalArgumentException("application not set");
		options.complete();
		
		ServerConstGenerator generator = new ServerConstGenerator(options);
		generator.run();
	}
	
	
	private ServerConstGenerator(Options options) 
		throws ScanException
	{
		options_ = options;
	}
	
	
	private void run() throws IOException, ScanException
	{
		ResourceScan scan = new ResourceScan(
			options_.app.getControllerConfig(),
			null,
			options_.verbose);
		ResourceInfo root = scan.getRootInfo();
		if (options_.outputLocation != null)
			generate(root);
	}
	
	
	private void generate(ResourceInfo root) throws IOException
	{
		// generate in memory
		ServerTemplate t = new ServerTemplate(root, 
			options_.outputPackage,
			options_.outputName,
			options_.app, 
			options_.timestamp);
		StringWriter out = new StringWriter();
		t.print(new TemplateWriter(out));

		// write to class file
		OutputFile outputFile = options_.outputLocation.getOutputFile(options_.outputPackage, options_.outputName + ".java");
		if (options_.verbose)
			ResourceScan.log("writing " + outputFile.file.getAbsolutePath());		
		outputFile.write(options_.encoding, out.toString());
	}
	
	
	/**
	 * Holds the options for a run of {@link ServerConstGenerator}.
	 */
	public static class Options
	{
		public Application app;
		public String encoding;
		public OutputLocation outputLocation;
		public boolean timestamp;
		public boolean verbose;
		public String outputClass;
		String outputPackage;
		String outputName;
		
		
		public void setApplication(String className)
		{
			Check.notNull(className, "application class");
			try
			{
				app = ClassUtil.createObject(className, Application.class, null);
			}
			catch (Exception e)
			{
				throw new ScanException("cannot create application '" + className + "'"); 
			}
		}
		
		
		public void complete()
		{
			Check.notNull(app, "app");
			Check.notNull(outputLocation, "outputLocation");
			
			if (encoding == null)
				encoding = DEFAULT_ENCODING;
			
			String appPackage	= ClassUtil.getPackageName(app.getClass());
			String appName 		= ClassUtil.cutPackageName(app.getClass().getName());
			String appPrefix	= StringUtil.cutRight(StringUtil.cutRight(appName, "Application"), "App");
			
			if (outputClass == null)
				outputClass = appPackage + '.' + appPrefix + "Resources";
			
			outputPackage = ClassUtil.getPackageName(outputClass); 
			outputName    = ClassUtil.cutPackageName(outputClass);  				
		}
	}
	
	
	private Options options_;
}
