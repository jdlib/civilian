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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import org.civilian.Controller;
import org.civilian.Template;
import org.civilian.internal.source.OutputFile;
import org.civilian.internal.source.OutputLocation;
import org.civilian.internal.source.PackageDetector;
import org.civilian.response.ResponseWriter;
import org.civilian.template.ComponentBuilder;
import org.civilian.template.TemplateWriter;
import org.civilian.template.mixin.FormTableMixin;
import org.civilian.template.mixin.HtmlMixin;
import org.civilian.template.mixin.LangMixin;
import org.civilian.template.mixin.TableMixin;
import org.civilian.util.Arguments;
import org.civilian.util.ClassUtil;
import org.civilian.util.DateTime;
import org.civilian.util.FileType;
import org.civilian.util.IoUtil;
import org.civilian.util.Scanner;
import org.civilian.util.StringUtil;


/**
 * CspCompiler is a TemplateCompiler which compiles
 * Civilian Server Pages (CSP) into Java template classes. 
 */
public class CspCompiler
{
	private static final String START_TEMPLATE_SECTION = "{{";
	private static final String END_TEMPLATE_SECTION = "}}";
	
	
	/**
	 * The default extension of CSP files.
	 */
	public static final String EXTENSION = "csp";

	
	/**
	 * The default encoding used for template files and generated files.
	 */
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	
	/**
	 * Runs the CspCompiler from the command-line
	 */
	public static void main(String[] args) throws CspException, IOException
	{
		compile(args);
	}

	
	/**
	 * Creates a new CspCompiler.
	 */
	public CspCompiler()
	{
		this(null);
	}
	
	
	/**
	 * Creates a new CspCompiler.
	 */
	public CspCompiler(Options options)
	{
		options_ = options != null ? options : new Options();
		registerMixin(new MixinField(HtmlMixin.class, "html"));
		registerMixin(new MixinField(LangMixin.class, "lang"));
		registerMixin(new MixinField(TableMixin.class, "table"));
		registerMixin(new MixinField(FormTableMixin.class, "formTable"));
	}
	
	
	/**
	 * Returns the options of the compiler.
	 */
	public Options options()
	{
		return options_;
	}
	
	
	/**
	 * Configures the compiler with the command line arguments and then runs the compiler.
	 * @param args command line arguments.
	 * @throws CspException if a compiler error occurs
	 * @throws IOException if an io error occurs
	 */
	public static synchronized void compile(String[] args) throws CspException, IOException
	{
		compile(new Arguments(args));
	}
	
	
	/**
	 * Configures the compiler with the command line arguments and then runs the compiler.
	 * @param args command line arguments, wrapped in a Arguments object
	 * @throws CspException if a compiler error occurs
	 * @throws IOException if an io error occurs
	 */
	public static void compile(Arguments args) throws CspException, IOException
	{
		if (!args.hasMore())
		{
			printHelp();
			return;
		}
		
		Options options = new Options();
		while(args.startsWith("-"))
		{
			if (args.consume("-force"))
				options.force = true;
			else if (args.consume("-r"))
				options.recursive = args.nextBoolean("recursive mode");
			else if (args.consume("-srcmap"))
				options.srcMap = args.nextBoolean("srcmap mode");
			else if (args.consume("-enc"))
				options.setEncoding(args.next("encoding"));
			else if (args.consume("-enc:in"))
				options.encodingIn = args.next("input encoding");
			else if (args.consume("-enc:out"))
				options.encodingOut = args.next("output encoding");
			else if (args.startsWith("-out:"))
				options.outputLocation = OutputLocation.parse(args, true, true);
			else if (args.consume("-ts"))
				options.timestamp = args.nextBoolean("timestamp mode");
			else if (args.consume("-v"))
				options.verbose = args.nextInt("verbose level");
			else 
				throw new IllegalArgumentException("unknown option " + args.next());
		}
		
		File input = args.nextFile("input file", FileType.EXISTENT);

		CspCompiler compiler = new CspCompiler(options);
		compiler.compile(input);
	}
	
	
	/**
	 * Prints a help screen.
	 */
	private static void printHelp()
	{
		System.out.println("usage:");
		System.out.println("java " + CspCompiler.class.getName() + " [<parameter>]* <input>");
		System.out.println();
		System.out.println("input:               either a single file to compile");
		System.out.println("                     or a directory whose files are compiled");
		System.out.println("output:              provide one of the -out parameters to");
		System.out.println("                     control the output. If no output parameter");
		System.out.println("                     is specified the output is placed into the");
		System.out.println("                     same directory than the template file");
		System.out.println();
		System.out.println("parameters:                                                      default:");
		System.out.println("-enc <v>             encoding of template and generated files    " + DEFAULT_ENCODING);
		System.out.println("-enc:in <v>          encoding of template files                  " + DEFAULT_ENCODING);
		System.out.println("-enc:out <v>         encoding of generated files                 " + DEFAULT_ENCODING);
		System.out.println("-ext <v>             extension of template files                 " + EXTENSION);
		System.out.println("-force               force compilation (ignore timestamps)");
		System.out.println("-srcmap <true|false> print source map comments                   true");
		OutputLocation.printHelp(true);
		System.out.println("-r <true|false>      recurse subdirectories                      true");
		System.out.println("-ts <true|false>     print generation timestamp into file        false");
		System.out.println("-v 0|1|2             verbose                                     0");
	}

	
	public synchronized void compile(File input) throws CspException, IOException
	{
		FileType.EXISTENT.check(input);
		options_.complete();
		
		log(1, "run csp compiler");

		if (input.isDirectory())
			compileDirectory(input);
		else if (acceptExtension(input))
			compileFile(input);
		else
			System.err.println("not a template file: " + input);
	}
	
	
	/**
	 * Compiles a directory.
	 */
	private void compileDirectory(File dir) throws CspException, IOException
	{
		log(2, "scan", dir);
		File files[] = dir.listFiles();
		for (int i=0; i<files.length; i++)
		{
			File file = files[i];
			if (!file.isDirectory())
			{
				if (acceptExtension(file))
					compileFile(file);
			}
			else if (options_.recursive)
				compileDirectory(file);
		}
	}
	
	
	private boolean acceptExtension(File templateFile)
	{
		String extension = IoUtil.getExtension(templateFile);
		return EXTENSION.equals(extension);
	}
	

	/**
	 * Compiles a template file.
	 */
	private void compileFile(File templateFile) throws CspException, IOException
	{
		log(2, "check", templateFile);

		String fileName 		= IoUtil.cutExtension(templateFile.getName());
		String className		= StringUtil.startUpperCase(fileName);
		OutputFile outputFile	= options_.outputLocation.getOutputFile(null /*don't know the package yet*/, className + ".java", templateFile);
		
		if (outputFile == null)
			log(2, "skip", templateFile);
		else
		{
			if (options_.force || outputFile.generate(templateFile)) 
			{
				TemplateInput input		= new TemplateInput(templateFile);

				JavaOutput output		= new JavaOutput();
				output.file				= outputFile.file; 
				output.className		= className;
				output.assumedPackage	= outputFile.packageName;
				
				log(1, "generate", outputFile.file.getAbsolutePath());
				compileFile(input, output);
			}
		}
	}
	
	
	private synchronized void compileFile(TemplateInput input, JavaOutput output) throws CspException, IOException
	{
		try
		{
			compile(input, output);
			
			// phase 2: write output, in order to not produce a null output if reading fails
			BufferedWriter out = null;
			try
			{
				File dir = output.file.getParentFile();
				IoUtil.mkdirs(dir);
				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output.file), options_.encodingOut));
				out.write(output_);
			}
			finally
			{
				IoUtil.close(out);
			}
		}
		catch(CspException e)
		{
			throw e;
		}
		catch(IOException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new CspException("exception in compiler run", e, scanner_);
		}
		finally
		{
			scanner_ = null;
		}
	}	
	
	
	private void compile(TemplateInput input, JavaOutput output) throws CspException, IOException
	{
		scanner_ = new Scanner(input.readLines(options_.encodingIn));
		scanner_.setSource(input.file.getName());
		scanner_.setErrorHandler(new ErrorHandler());
		
		if (scanner_.nextKeyword("encoding"))
		{
			String encoding = nextScannerToken("encoding");
			
			if (!options_.encodingIn.equalsIgnoreCase(encoding))
			{
				scanner_.init(input.readLines(encoding));
				scanner_.nextKeyword("encoding");
				nextScannerToken("encoding");
			}
		}
		
		compile(input.file, output);
	}
	
	
	private void compile(File templFile, JavaOutput output) throws CspException, IOException
	{
		StringWriter sw  = new StringWriter();
		SourceWriter out = new SourceWriter(sw);
		
		if (scanner_.nextKeyword("java"))
		{
			// pure java mode: csp file is essentially a Java class
			// with template snippets
			scanner_.nextLine();
			compileJavaLines(out, false);
		}
		else
		{
			// csp mode: parse commands which describe the generated
			// java class
			classData_ = new ClassData(output.className);
			parsePackageCmd(templFile, output.assumedPackage);
			parseImportCmds();
			parsePrologCmds();
			parseTemplateCmd();
	
			// we compile the body and write it to another temporary writer
			// since the template body can contain a super-call which
			// we need in printClassData
			StringWriter swBody	 = new StringWriter();
			SourceWriter outBody = new SourceWriter(swBody);
			outBody.increaseTab();
			compileJavaLines(outBody, true);
			
			printClass(out, templFile);
			int tab = out.getTabCount();
			out.setTabCount(0);
			out.print(swBody.toString());
			out.setTabCount(tab);
			if ((classData_.args != null) || (classData_.mixins != null) || classData_.standalone)
				printFields(out);
			
			out.endBlock(); // class block, started in compileClassData
		}
		out.flush();
			
		output_ = sw.toString();
	}


	private void parsePackageCmd(File templFile, String assumedPackage) throws CspException
	{
		if (scanner_.nextKeyword("package"))
		{
			classData_.packageName = StringUtil.cutRight(nextScannerToken("package"), ";"); 
			if ((assumedPackage != null) && !assumedPackage.equals(classData_.packageName))
				throw new CspException("package was set by compiler parameters to '" + assumedPackage + ", but the template specified '" + classData_.packageName + "'"); 
		}
		else if (assumedPackage != null)
			classData_.packageName = assumedPackage;
		else
		{
			PackageDetector detector = new PackageDetector();
			String p = detector.detect(templFile);
			if (p == null)
				throw new CspException("cannot detect package for template '" + templFile.getName() + "', please provide a explicit package in the template", scanner_);
			classData_.packageName = p;
		}
	}
	
	
	private void parseImportCmds() throws CspException
	{
		while(scanner_.nextKeyword("import"))
		{
			String s = StringUtil.cutRight(nextScannerToken("import"), ";");
			s = resolveRelativeImport(s);
			classData_.imports.add(s);
		}
	}
	
	
	private String resolveRelativeImport(String s) throws CspException
	{
		if (s.startsWith("./"))
			return classData_.packageName + '.' + s.substring(2);
		else if (s.startsWith("../"))
		{
			String t = s;
			String prefix = classData_.packageName;
			do
			{
				int p = prefix.lastIndexOf('.');
				if (p < 0)
					throw new CspException("relative import '" + s + "' can't be applied to package '" + classData_.packageName + "'", scanner_);
				prefix = prefix.substring(0, p);
				t = t.substring(3);
			}
			while(t.startsWith("../"));
			return prefix + '.' + t;
		}
		else
			return s;
	}
	
	
	private void parsePrologCmds() throws CspException
	{
		while(scanner_.nextKeyword("prolog"))
			classData_.prolog.add(scanner_.consumeRest());
	}
	
	
	private void parseTemplateCmd() throws CspException, IOException
	{
		//-------------------------------------
		// "template"
		if (!scanner_.nextKeyword("template"))
			throw new CspException("expected the template command, but reached end of file", scanner_);
		parseTemplateArgs();

		//-------------------------------------
		// "package-access"
		if (scanner_.nextKeyword("package-access"))
			classData_.isPublic = false; 
		
		//-------------------------------------
		// "extends"
		if (scanner_.nextKeyword("extends"))
		{
			if (scanner_.next("-"))
			{
				classData_.standalone = true;
				
				String writerClass = ResponseWriter.class.getSimpleName();
				if (scanner_.nextKeyword("using"))
					writerClass = nextScannerToken("using");
				
				if ("ResponseWriter".equals(writerClass))
					writerClass = ResponseWriter.class.getName();
				else if ("TemplateWriter".equals(writerClass))
					writerClass = TemplateWriter.class.getName(); 
						
				classData_.writerClass = writerClass;
				classData_.writerClassSimple = ClassUtil.cutPackageName(writerClass);
				classData_.imports.add(writerClass);
			}
			else
				classData_.extendsClass = nextScannerToken("extends");
			
		}
		if ((classData_.extendsClass == null) && !classData_.standalone)
		{
			classData_.imports.add(Template.class);
			classData_.extendsClass = Template.class.getSimpleName();
		}
		
		//-------------------------------------
		// "implements"
		if (scanner_.nextKeyword("implements"))
			classData_.implementsList = parseClassList();
		
		//-------------------------------------
		// "mixin"
		if (scanner_.nextKeyword("mixin"))
			classData_.mixins = parseMixins(scanner_, classData_.mixins);
		if (classData_.mixins != null)
		{
			for (MixinField mixin : classData_.mixins)
				classData_.imports.add(mixin.className);
		}
		
		//-------------------------------------
		// "throws"
		if (scanner_.nextKeyword("throws"))
		{
			classData_.exception = parseClassList();
			if ("-".equals(classData_.exception))
				classData_.exception = null;
		}
		
		if (scanner_.getPos() > 0)
			throw new CspException("invalid input: '" + scanner_.getRest() + "'", scanner_);
	}
	
		
	private void parseTemplateArgs() throws CspException, IOException
	{
		if (!scanner_.next("("))
			return;
		if (scanner_.next(")"))
			return;
		
		StringBuilder argsString	= new StringBuilder();
		classData_.arguments 		= new ArrayList<>();
		
		while(true)
		{
			Argument argument = new Argument(scanner_);
			classData_.arguments.add(argument);
			
			if (argsString.length() > 0)
				argsString.append(", ");
			argument.ctorArg(argsString);
				
			if (scanner_.next(")"))
				break;
			if (!scanner_.next(","))
				throw new CspException("expected closing bracket ')' of template argument list", scanner_);
		}
		
		classData_.args = argsString.toString();
	}
	

	private String parseClassList() throws CspException
	{
		StringBuffer list = new StringBuffer();
		while(true)
		{
			String type = nextScannerToken("implements", ",");
			if (list.length() > 0)
				list.append(", ");
			list.append(type);
			if (!scanner_.next(","))
				break;
		}
		return list.toString();
	}
	
	
	private ArrayList<MixinField> parseMixins(Scanner scanner, ArrayList<MixinField> list) throws CspException
	{
		do
		{
			String def = nextScannerToken("mixin def", ",");
			String[] parts = def.split(":", 2);
			if (parts.length > 0)
			{
				String className = parts[0];
				String fieldName = parts.length > 1 ? parts[1] : null;
				list = parseMixin(className, fieldName, list); 
			}
		}
		while(scanner.next(","));
		return list;
	}
	
	
	private ArrayList<MixinField> parseMixin(String className, String fieldName, ArrayList<MixinField> list)
	{
		MixinField registeredMixin = registeredMixins_.get(className);
		if (registeredMixin != null)
		{
			if (fieldName == null)
				fieldName = registeredMixin.fieldName;
			className = registeredMixin.className;
		}
		
		if (list == null)
			list = new ArrayList<>();
		list.add(new MixinField(className, fieldName));
		return list;
	}
	
	
	private void registerMixin(MixinField mixin)
	{
		registeredMixins_.put(mixin.fieldName, mixin);
		registeredMixins_.put(mixin.className, mixin);
	}

	
	private void printClass(SourceWriter out, File templFile) throws IOException
	{
		out.println("/**");
		out.print(" * Generated from " + templFile.getName());
		if (options_.timestamp)
			out.print(" at " + new DateTime());
		out.println();
		out.println(" * Do not edit.");
		out.println(" */");
		out.print("package ");
		out.print(classData_.packageName);
		out.println(";");
		out.println();
		out.println();
		if (classData_.imports.write(out, ClassUtil.getPackageName(Controller.class)))
		{
			out.println();
			out.println();
		}
		for (String prolog : classData_.prolog)
			out.println(prolog);
		
		if (classData_.isPublic)
			out.print("public ");
		out.print("class ");
		out.print(classData_.className);
		if (classData_.extendsClass != null)
		{
			out.print(" extends ");
			out.print(classData_.extendsClass);
		}
		if (classData_.implementsList != null)
		{
			out.print(" implements ");
			out.print(classData_.implementsList);
		}
		out.println();
		out.beginBlock();
		
		if (classData_.needsCtor())
			printClassCtor(out);

		if (classData_.standalone)
			printClassPublicPrintMethod(out);
		
		if (classData_.mixins != null)
			printClassInitMethod(out);

		if (classData_.extendsClass != null)
			out.print("@Override ");
		out.print("protected void print()");
		if (classData_.exception != null)
		{
			out.print(" throws ");
			out.print(classData_.exception);
		}
		out.println();
	}
	
	
	private void printClassCtor(SourceWriter out) throws IOException
	{
		out.print("public ");
		out.print(classData_.className);
		out.print("(");
		if (classData_.args != null)
			out.print(classData_.args);
		out.println(")");
		out.beginBlock();
		if (classData_.superCall != null)
		{
			out.print(classData_.superCall);
			printSrcMapComment(out, classData_.superCall, classData_.superCallLine);				
		}
		if (classData_.args != null)
		{
			for (Argument arg : classData_.arguments)
			{
				arg.fieldAssign(out);
				out.println();
			}
		}
		out.endBlock();
		out.println();	
		out.println();	
	}
	

	private void printClassPublicPrintMethod(SourceWriter out)
	{
		out.print("public synchronized void print(");
		out.print(classData_.writerClassSimple);
		out.print(" out)");
		if (classData_.exception != null)
		{
			out.print(" throws ");
			out.print(classData_.exception);
		}
		out.println();
		out.beginBlock();
			out.println("if (out == null)");
			out.increaseTab();
				out.println("throw new IllegalArgumentException(\"out is null\");");
			out.decreaseTab();
			out.beginBlock();
				out.println("this.out = out;");
				out.println("init();");
				out.println("print();");
			out.endBlock();
		out.endBlock();
		out.println();
		out.println();
	}
	
	private void printClassInitMethod(SourceWriter out)
	{
		out.println("@Override protected void init()");
		out.beginBlock();
			out.println("super.init();");
			for (MixinField mixin : classData_.mixins)
			{
				out.print(mixin.fieldName);
				out.print(" = new ");
				out.print(mixin.simpleName);
				out.println("(out);");
			}
		out.endBlock();
		out.println();
		out.println();
	}
	
	
	private void printFields(SourceWriter out) throws CspException, IOException
	{
		out.println();
		out.println();
		if (classData_.args != null)
		{
			for (Argument arg : classData_.arguments)
			{
				arg.fieldDecl(out);
				out.println();
			}
		}
		if (classData_.mixins != null)
		{
			for (MixinField mixin : classData_.mixins)
			{
				out.print("private ");
				out.print(mixin.simpleName);
				out.print(" ");
				out.print(mixin.fieldName);
				out.println(";");
			}
		}
		if (classData_.standalone)
		{
			out.print("protected ");
			out.print(classData_.writerClassSimple);
			out.println(" out;");
		}
	}
	
	
	private void compileJavaLines(SourceWriter out, boolean expectMainTemplate) throws CspException, IOException
	{
		do
		{
			String line = scanner_.getLine();
			
			if (line.trim().equals(START_TEMPLATE_SECTION))
			{
				compileTemplateLines(out, expectMainTemplate);
				expectMainTemplate = false;
			}
			else if (expectMainTemplate)
				throw new CspException("expected main template body", scanner_);
			else
				out.println(line);
		}
		while(scanner_.nextLine());
	}
	
	
	private void compileTemplateLines(SourceWriter out, boolean isMainTemplate) throws CspException, IOException
	{
		TemplateLine tline = new TemplateLine();
		
		// current line is the template start "{{"
		parse(scanner_.getLine(), tline);
		int tabBase1 = out.getTabCount();
		while(out.getTabCount() < tline.indent)
			out.increaseTab();
		
		int tabBase2 = out.getTabCount();
		out.beginBlock();
		
		int componentLevel = -1;
		int maxCompLevel = -1;
		
		boolean canHaveSuperCall = isMainTemplate; 
		
		Block block = null;
		while(true)
		{
			if (!scanner_.nextLine())
				throw new CspException("template end '" + END_TEMPLATE_SECTION + "' expected", scanner_);
			
			String line = scanner_.getLine();
			parse(line, tline);

			if (END_TEMPLATE_SECTION.equals(tline.content))
				break;

			if (tline.type == TemplateLine.Type.empty) 
				out.println("out.println();");
			else
			{
				if (block == null)
					block = new Block(null, tline.indent);
				block = adjustTemplateIndent(block, tline, out);
				block.isCodeBlock = false; 
				
				if (tline.type == TemplateLine.Type.code)
				{
					if (canHaveSuperCall && tline.content.startsWith("super("))
					{
						classData_.superCall = tline.content;
						classData_.superCallLine = scanner_.getLineIndex();
					}
					else
					{
						block.isCodeBlock = true; 
						out.print(tline.content);
						printSrcMapComment(out, tline.original);
					}
				}
				else if (tline.type == TemplateLine.Type.literal)
				{
					printTemplateLiteralLine(out, tline.content, true);
				}
				else if (tline.type == TemplateLine.Type.componentStart) 
				{
					boolean declare = false;
					if (++componentLevel > maxCompLevel)
					{
						maxCompLevel = componentLevel;
						declare = true;
					}
						
					int p = tline.content.indexOf(']');
					
					if (p < 0)
					{
						printTemplateComponentStart(out, componentLevel, tline.content, true, declare);
					}
					else
					{
						String cbExpr = tline.content.substring(0, p).trim();  
						printTemplateComponentStart(out, componentLevel, cbExpr, false, declare);
						printTemplateLiteralLine(out, tline.content.substring(p + 1).trim(), false);
						printTemplateComponentEnd(out, componentLevel--, false, null);
					}
				}
				else if (tline.type == TemplateLine.Type.componentEnd)
				{
					if (componentLevel < 0)
						throw new CspException("unmatched component end", scanner_);
					printTemplateComponentEnd(out, componentLevel--, true, tline.original);
				}
				else
					throw new CspException("unexpected line type " + tline.type, scanner_);
				
				canHaveSuperCall = false;
			}
		}

		// close all open blocks
		while(out.getTabCount() > tabBase2)
			out.endBlock();
		while(out.getTabCount() > tabBase1)
			out.decreaseTab();
	}
	
	
	private void parse(String line, TemplateLine tline) throws CspException
	{
		if (!tline.parse(line))
			throw new CspException(tline.error, scanner_);
	}
	

	private Block adjustTemplateIndent(Block block, TemplateLine tline, SourceWriter out) throws CspException
	{
		if (tline.indent == block.indent)
			return block;
		if (tline.indent > block.indent)
		{
			if (block.isCodeBlock)
				out.beginBlock();
			else
				out.println("out.increaseTab();") ;
			block = new Block(block, tline.indent);
		}
		else // (tline.indent < block.indent)
		{
			while(tline.indent < block.indent)
			{
				block = block.prev;
				if (block == null)
					throw new CspException("end of template marker '{{' expected", scanner_);
				if (tline.indent > block.indent)
					throw new CspException("inconsistent indent", scanner_);
				if (block.isCodeBlock)
					out.endBlock();
				else
					out.println("out.decreaseTab();") ;
			}
		}
		return block;
	}

	
	private void printTemplateComponentStart(SourceWriter out, int level, String cbExpr, boolean multiLine, boolean declare)
	{
		String var = getCbVar(level);
		if (declare)
		{
			out.print(ComponentBuilder.class.getName());
			out.print(' ');
		}
		out.print(var);
		out.print(" = ");
		out.print(cbExpr);
		out.println(";");
		out.print(var);
		out.print(".startComponent(");
		out.print(multiLine);
		out.print(");");
		
		printSrcMapComment(out, cbExpr);
	}

	
	private void printTemplateComponentEnd(SourceWriter out, int level, boolean multiLine, String comment)
	{
		String var = getCbVar(level);
		out.print(var);
		out.print(".endComponent(");
		out.print(multiLine);
		out.print(");"); // ends the wrapper block
		
		if (comment != null)
			printSrcMapComment(out, comment);
		else
			out.println();
	}
	
	
	private void printTemplateLiteralLine(SourceWriter out, String line, boolean usePrintln) throws CspException
	{
		int length = line.length();
		int start = 0;
		int p = 0;
		boolean lastPartWasCode = false;
		
		while((start < length) && ((p = line.indexOf("<%", start)) != -1))
		{
			lastPartWasCode = false;
			if (line.regionMatches(p, "<%%", 0, 3))
			{
				if ((p + 3 < length) && (line.charAt(p + 3) == '>'))
				{
					// <%%> detected
					if (start < p)
						printTemplateText(out, line, start, p, false);
					start = p + 4;
				}
				else
				{
					// <%% detected: print literal
					printTemplateText(out, line, start, p+2, false);
					start = p + 3;
				}
			}
			else
			{
				if (start < p)
					printTemplateText(out, line, start, p, false);
				
				int q = line.indexOf("%>", p);
				
				// code end signal not found
				if (q == -1)
					throw new CspException("closing '%>' not found", scanner_);
				
				// ignore empty code segments <%%>
				if (q > p + 2)
				{
					// line end signal <%/%> found
					if ((q == p + 3) && (line.charAt(p + 2) == '/'))
						return;
					
					String snippetRaw  = line.substring(p, q + 2);
					String snippetCode = line.substring(p +2, q).trim();
					
					printTemplateSnippet(out, snippetRaw, snippetCode);
					lastPartWasCode = true;
				}
				start = q + 2;
			}
		}
		if (start < length)
			printTemplateText(out, line, start, length, usePrintln);
		else if (usePrintln)
		{
			if (lastPartWasCode)
				out.println("out.printlnIfNotEmpty();");
			else
				out.println("out.println();");
		}
	}
	
	
	private void printTemplateText(SourceWriter out, String content, int start, int end, boolean usePrintln)
	{
		out.print(usePrintln ? "out.println(\"" : "out.print(\"");
		for (int i=start; i<end; i++)
		{
			char c = content.charAt(i);
			switch(c)
			{
				case '\t':
					out.print("\\t");
					break;
				case '\\':
					out.print("\\\\");
					break;
				case '"':
					out.print("\\\"");
					break;
				default:
					out.print(c);
					break;
			}
		}
		out.print("\");");
		printSrcMapComment(out, content.substring(start, end));
	}
	
	
	/**
	 * Prints a template code segment embedded in a literal line between "<%" and "%>".
	 * @param raw the snippet including the boundaries "<%" and "%>".
	 * @param code the code content, trimmed.
	 */
	private void printTemplateSnippet(SourceWriter out, String raw, String code) throws CspException
	{
		if (code.charAt(0) == '?')
		{
			if (code.length() != 1)
			{
				out.print("if (");
				out.print(code.substring(1).trim());
				out.print(")");
				printSrcMapComment(out, raw);
				out.beginBlock();
			}
			else
				out.endBlock();
		}
		else if (code.endsWith(";"))
		{
			out.print(code);
			printSrcMapComment(out, raw);
		}
		else
		{
			out.print("out.print(");
			out.print(code);
			out.print(");");
			printSrcMapComment(out, raw);
		}
	}
	
	
	private void printSrcMapComment(SourceWriter out, String s)
	{
		printSrcMapComment(out, s, scanner_.getLineIndex());
	}
	
	
	private void printSrcMapComment(SourceWriter out, String s, int lineIndex)
	{
		if (options_.srcMap)
		{
			int column = out.getColumn();
			for (int i=column; i<=70; i++)
				out.print(' ');
			out.print(" // line ");
			out.print(lineIndex +  1);
			out.print(": ");
			out.print(s.trim());
		}
		out.println();
	}
	
	
	private String nextScannerToken(String what) throws CspException
	{
		return nextScannerToken(what, "");
	}

	
	private String nextScannerToken(String what, String delims) throws CspException
	{
		String token = scanner_.consumeToken(delims);
		if (token == null)
			throw new CspException("missing " + what + "-value", scanner_);
		return token;
	}

	
	private static String getCbVar(int level)
	{
		return "cspCb" + level;
	}
	
	
	/**
	 * Writes progress information to System.out.
	 */
	private void log(int level, String action, Object text)
	{
		if (options_.verbose >= level)
		{
			System.out.print(StringUtil.fillRight(action, 9));
			System.out.println(text.toString());
		}
	}
	
	
	private void log(int level, String text)
	{
		if (options_.verbose >= level)
			System.out.println(text.toString());
	}
	
	
	/**
	 * Options for the CspCompiler.
	 */
	public static class Options
	{
		/**
		 * Flag that determines if the compiler always generates the template regardless
		 * of timestamps of template file and compiler target.
		 * The default is false.
		 */
		public boolean force;
		
		
		/**
		 * If the compiler input is a directory, the recursive flag
		 * tells if the compiler should recurse in sub-directories
		 * to find template files. By default recursive processing
		 * is on.
		 */
		public boolean recursive = true;

		
		/**
		 * Determines if a timestamp is printed into the generated file.
		 * The default is false.
		 */
		public boolean timestamp;

		
		/**
		 * Determines if the compiler should print line comments
		 * in the generated class which show the source line.
		 * The default is true.
		 */
		public boolean srcMap = true;
		
		
		/**
		 * The encoding of input template files.
		 * By default it is UTF-8.
		 */
		public String encodingIn;

		
		/**
		 * The encoding of output template files.
		 * By default it is UTF-8.
		 */
		public String encodingOut;

		/**
		 * The verbose level (0-2) determines the verbosity of compiler messages.
		 */
		public int verbose = 0;

		/**
		 * The OutputLocation determines where
		 * to write the generated output. By default the output
		 * is placed in the same directory as the input file.
		 */
		public OutputLocation outputLocation;
		
		
		/**
		 * Sets the encoding of of input and of generated files.
		 */
		public void setEncoding(String encoding)
		{
			encodingIn = encodingOut = encoding;
		}
		
		
		private void complete()
		{
			if (encodingIn == null)
				encodingIn = DEFAULT_ENCODING;
			if (encodingOut == null)
				encodingOut = DEFAULT_ENCODING;
			if (outputLocation == null)
				outputLocation = OutputLocation.OUTPUT_TO_INPUT_DIR; 
		}
	}
	
	
	private Options options_;
	private Scanner scanner_;
	private ClassData classData_;
	private String output_;
	private HashMap<String,MixinField> registeredMixins_ = new HashMap<>();
}
