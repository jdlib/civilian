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
package org.civilian.tool.scaffold;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.regex.Pattern;
import org.civilian.internal.source.OutputLocation;
import org.civilian.tool.csp.CspCompiler;
import org.civilian.tool.csp.CspException;
import org.civilian.util.Arguments;
import org.civilian.util.Check;
import org.civilian.util.StringUtil;


/**
 * The scaffold command-line tool.
 * Run without arguments to obtain a detailed help message.
 */
public class Scaffold
{
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final Pattern LIST_SPLT = Pattern.compile("\\s*,\\s*");
	
	
	public static void main(String[] s) throws Exception
	{
		Arguments args = new Arguments(s);
		if (!args.hasMore())
		{
			printHelp();
			return;
		}
		else
			run(args);
	}
	
	
	private static void printHelp()
	{
		System.out.println("Usage:");
		System.out.println("java " + Scaffold.class.getName() + " [<parameter>]* <dir> <app-prefix>");
		System.out.println();
		System.out.println("Arguments");
		System.out.println("dir                  the project directory");
		System.out.println("app-prefix           (short) prefix of core application classes");
		System.out.println();
		System.out.println("Parameters:                                            default:");
		System.out.println("-eclipse             create eclipse project files");
		System.out.println("-enc <encoding>      encoding of generated files       " + DEFAULT_ENCODING);
		System.out.println("-libs <list>         jar files to copy to WEB-INF/lib");
		System.out.println("-locales <list>      comma-sep. list of sup. locales   en");
		System.out.println("-min                 create a minimal scaffold project");
		System.out.println("-package <package>   base application package          based on app prefix");
		System.out.println("-tomcat              create tomcat server.xml snippet");
		System.out.println("-text                creates initial msgbundles");
		System.out.println("-v                   verbose");
	}
	
	
	private static void run(Arguments args) throws Exception
	{
		Options options = new Options();
		
		while(args.startsWith("-"))
		{
			if (args.consume("-eclipse"))
				options.eclipse = true;
			else if (args.consume("-enc"))
				options.encoding = args.next("encoding");
			else if (args.consume("-libs"))
				options.libs = args.next("libs");
			else if (args.consume("-locales"))
				options.locales = args.next("locales");
			else if (args.consume("-min"))
				options.minimal = true;
			else if (args.consume("-package"))
				options.basePackage = args.next("package");
			else if (args.consume("-text"))
				options.text = true;
			else if (args.consume("-tomcat"))
				options.tomcat = true;
			else if (args.consume("-v"))
				options.verbose = true;
			else
				throw new IllegalArgumentException("unknown option " + args.next());
		}
		
		options.dir 		= args.nextFile("directory").getAbsoluteFile();
		options.appPrefix 	= args.next("app-prefix");
		
		Scaffold scaffold 	= new Scaffold(options);
		scaffold.run();
	}

	
	public Scaffold(Options options)
	{
		options_ = Check.notNull(options, "options");
		options_.complete();
		Log.setVerbose(options.verbose);
		project_ = new Project(options.dir, options.encoding, options_.basePackage);
	}
	
	
	public void run() throws Exception
	{
		Log.print("creating project in " + project_.root);
		createVariables();
		writeStandardFiles();
		if (options_.eclipse)
			writeEclipseFiles();
		if (options_.text)
			writeTextFiles();
		if (options_.tomcat)
			writeTomcatFiles();
		Log.print("done");
	}


	private void createVariables()
	{
		String appId = options_.appPrefix.toLowerCase();
		
		vars_ = new VariableMap();
		appClass_ 			= vars_.put(new ClassVar("appClass", 		options_.appPrefix + "App"));  
		pathParamsClass_ 	= vars_.put(new ClassVar("pathParamsClass", options_.appPrefix + "PathParams"));  
		resourcesClass_ 	= vars_.put(new ClassVar("resourcesClass",	options_.appPrefix + "Resources"));
		appCtrlClass_ 		= vars_.put(new ClassVar("appController",	options_.appPrefix + "Controller"));
		vars_.put(new Variable("encoding", 		options_.encoding));
		vars_.put(new Variable("appPrefix", 	options_.appPrefix));
		vars_.put(new Variable("appId", 		appId));
		vars_.put(new Variable("webPackage", 	project_.srcWeb.packageName));
		vars_.put(new Variable("locales", 		options_.locales));
		vars_.put(new Variable("minComment", 	options_.minimal ? "// " : ""));
		vars_.put(new Variable("appMsgsIni", 	options_.text ?
			"app." + appId + ".messages = resbundle:" + project_.srcText.packagePath + "/msg\n" :
			"\n"));
	}
		
	
	private void writeStandardFiles() throws Exception
	{
		Log.print("write standard files");
		ResourceDir dir = new ResourceDir(vars_);
		dir.resource("build.xml").writeTo(project_.root);
		dir.resource("build.properties").writeTo(project_.root);
		dir.resource("ivy.xml").writeTo(project_.root);
		dir.resource("web.xml").writeTo(project_.webInf, "UTF-8");
		dir.resource("simplelogger.properties").writeTo(project_.webInfClasses);
		dir.resource("App.java.res").setFile(appClass_).writeTo(project_.srcWeb);
		dir.resource("AppPathParams.java.res").setFile(pathParamsClass_.file).writeTo(project_.srcWeb);
		dir.resource("AppController.java.res").setFile(appCtrlClass_.file).writeTo(project_.srcWeb);
		project_.bin.makeDir();

		if (!options_.minimal)
		{
			dir.resource("civilian.ini").writeTo(project_.webInf);
			dir.resource("style.css").writeTo(project_.web);
			dir.resource("AppResources.java.res").setFile(resourcesClass_.file).writeTo(project_.srcWeb);
			dir.resource("IndexController.java.res").writeTo(project_.srcWeb);
			dir.resource("IndexTemplate.csp.res").writeTo(project_.srcWeb);
			dir.resource("users$UsersController.java.res").writeTo(project_.srcWebUsers);
			dir.resource("users$IndexController.java.res").writeTo(project_.srcWebUsers);
			dir.resource("userId$UserController.java.res").writeTo(project_.srcWebUserId);
			dir.resource("userId$IndexController.java.res").writeTo(project_.srcWebUserId);
			dir.resource("userId$package-info.java.res").writeTo(project_.srcWebUserId);
		}
		else
		{
			dir.resource("civilian.ini.min").writeTo(project_.webInf);
			dir.resource("AppResources.java.min").setFile(resourcesClass_.file).writeTo(project_.srcWeb);
			dir.resource("IndexController.java.min").writeTo(project_.srcWeb);
		}
		
		
		copyLibs();

		runCspCompiler();
	}
	
	
	private void copyLibs() throws IOException
	{
		// copy all jars from the lib-directory
		if (options_.libs != null)
		{
			String[] entries = LIST_SPLT.split(options_.libs);
			for (String entry : entries)
				copyLib(new File(entry));
		}

		// copy the jar file if the Scaffold class is inside a jar file
		URL selfUrl = getClass().getResource(getClass().getSimpleName() + ".class");
		if (selfUrl != null)
		{
			String selfPath = selfUrl.getPath();
			if (selfPath.startsWith("file:"))
			{
				File jarFile = new File(selfPath.substring(5, selfPath.lastIndexOf("!")));
				if (jarFile.exists())
					copyLib(jarFile);
			}
		}
	}
	
	
	private void copyLib(File fileOrDir) throws IOException
	{
		if (!fileOrDir.exists())
			throw new IOException("lib " + fileOrDir.getAbsolutePath() + " does not exist");
		else if (fileOrDir.isFile())
		{
			project_.lib.makeDir();
			File target = new File(project_.lib, fileOrDir.getName());
			if (!target.exists())
				Files.copy(fileOrDir.toPath(), target.toPath());
		}
		else 
		{
			File[] files = fileOrDir.listFiles();
			if (files != null)
			{
				for (File file : files)
				{
					if (file.isFile() && file.getName().endsWith(".jar"))
						copyLib(file);
				}
			}
		}
	}

	
	private void runCspCompiler() throws CspException, IOException
	{
		CspCompiler compiler = new CspCompiler();

		CspCompiler.Options options = compiler.options();
		options.force = true;
		options.verbose	= options_.verbose ? 1 : 0;
		options.setEncoding(options_.encoding);
		options.outputLocation = OutputLocation.OUTPUT_TO_INPUT_DIR;
		
		compiler.compile(project_.src);
	}
	
	
	private void writeEclipseFiles() throws IOException
	{
		Log.print("write ecplise files");
		
		ResourceDir eclipseDir = new ResourceDir(vars_, "eclipse");
		
		Resource cpFile = eclipseDir.resource(".classpath");
		StringBuilder libs = new StringBuilder();
		String path = project_.webInfLib.getRelativePath() + "/";
		File[] libFiles = project_.webInfLib.listFiles();
		if (libFiles != null)
		{
			for (File file : libFiles)
			{
				if (file.getName().endsWith(".jar"))
					libs.append("\t<classpathentry kind=\"lib\" path=\"" + path + file.getName() + "\"/>\n"); 
			}
		}
		cpFile.replace("$libs", libs.toString());
		cpFile.replace("$src", project_.src.getRelativePath());
		cpFile.replace("$bin", project_.bin.getRelativePath());
		cpFile.writeTo(project_.root, "UTF-8");
		
		eclipseDir.resource(".project").writeTo(project_.root, "UTF-8");
		eclipseDir.resource("org.eclipse.core.resources.prefs").writeTo(project_.settings);
	}

	
	private void writeTextFiles() throws Exception
	{
		Log.print("write text files");
		ResourceDir textDir = new ResourceDir(vars_, "text");
		Resource resource   = textDir.resource("msg.properties");
		String[] locales    = LIST_SPLT.split(options_.locales);
		
		for (int i=0; i<locales.length; i++)
			resource.setFile("msg_" + locales[i] + ".properties").writeTo(project_.srcText, "ISO-8859-1");
	}
	
	
	private void writeTomcatFiles() throws Exception
	{
		Log.print("write tomcat files");
		ResourceDir tomcatDir = new ResourceDir(vars_, "tomcat");
		Resource resource   = tomcatDir.resource("tomcat.server.xml");
		resource.replace("$docbase", project_.web.getAbsolutePath());
		resource.replace("$classpath", project_.bin.getAbsolutePath());
		resource.writeTo(project_.root);
	}
	
	
	private static class Options
	{
		public String appPrefix;
		public String basePackage;
		public File dir;
		public boolean eclipse;
		public String encoding;
		public String libs;
		public String locales;
		public boolean minimal;
		public boolean text;
		public boolean tomcat;
		public boolean verbose;
		
		
		private void complete()
		{
			Check.notNull(dir, "directory");
			checkPrefix();
			if (encoding == null)
				encoding = DEFAULT_ENCODING;
			if (basePackage == null)
				basePackage = "com." + appPrefix.toLowerCase();
			if (locales == null)
				locales = "en";
		}
		
		
		private void checkPrefix()
		{
			Check.notEmpty(appPrefix, "appPrefix");
			appPrefix = StringUtil.startUpperCase(appPrefix);
			if (!isValidJavaPrefix(appPrefix))
				throw new IllegalArgumentException("appPrefix '" + appPrefix + "' is not a valid Java class prefix");
		}
		
		
		private boolean isValidJavaPrefix(String appPrefix)
		{
			if (!Character.isJavaIdentifierStart(appPrefix.charAt(0)))
				return false;
			for (int i=1; i<appPrefix.length(); i++)
			{
				if (!Character.isJavaIdentifierPart(appPrefix.charAt(i)))
					return false;
			}
			return true;
		}
	}
	
	
	private Options options_;
	private Project project_;
	private VariableMap vars_;
	private ClassVar appClass_;  
	private ClassVar pathParamsClass_;  
	private ClassVar resourcesClass_;
	private ClassVar appCtrlClass_;
}
