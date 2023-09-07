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
package org.civilian.tool.resbundle;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.civilian.template.CspWriter;
import org.civilian.text.msg.MsgId;
import org.civilian.tool.source.OutputLocation;
import org.civilian.tool.source.PackageDetector;
import org.civilian.util.Arguments;
import org.civilian.util.FileType;
import org.civilian.util.IoUtil;
import org.civilian.util.StringUtil;


/**
 * ResBundleCompiler allows you to store all your localized message texts
 * in an excel file. The compiler can then generate the resource
 * bundle property files from the excel file. It can also generate
 * an additional class which defines constants for the message ids. 
 * The ResBundleCompiler uses Apache POI to read the excel file.
 */
public class ResBundleCompiler
{
	private static final String PROPERTIES_CHARSET = "ISO-8859-1";
	private static final String DEFAULT_JAVA_CHARSET = "UTF-8";
	
	
	/**
	 * Command line entry point.
	 */
	public static void main(String[] stringArgs) throws IOException
	{
		Arguments args = new Arguments(stringArgs);
		if (!args.hasMore() || args.consume("-?") || args.consume("-help"))
		{
			printHelp();
			return;
		}
		
		Config config = new Config();
		
		while(args.startsWith("-"))
		{
			if (args.consume("-constClass"))
			{
				config.constClass = args.next("constClass");
			}
			else if (args.consume("-idClass"))
			{
				config.idClass = args.next("idClass");
				if ("#msgId".equals(config.idClass))
				{
					config.idClass = MsgId.class.getName();
					config.inlineIdClass = false;
				}
				else if ("#inline".equals(config.idClass))
				{
					config.idClass = "Id";
					config.inlineIdClass = true;
				}
			}
			else if (args.consume("-enc"))
				config.encoding = args.next("encoding");
			else if (args.startsWith("-out:"))
				config.outputType = OutputLocation.parse(args, true, false);
			else if (args.consume("-javadoc"))
				config.javadoc = true;
			else if (args.consume("-v"))
				config.verbose = true;
			else 
				throw new IllegalArgumentException("unknown option " + args.next());
		}
		
		config.excelFile = args.nextFile("excel message file", FileType.EXISTENT_FILE);

		if ("#file".equals(config.constClass))
			config.constClass = StringUtil.startUpperCase(IoUtil.cutExtension(config.excelFile));

		ResBundleCompiler c = new ResBundleCompiler();
		c.run(config);
	}
	
	
	/**
	 * Prints a help text.
	 */
	private static void printHelp()
	{
		System.out.println("usage:");
		System.out.println("java " + ResBundleCompiler.class.getName() + " [<parameter>]* <excel>");
		System.out.println();
		System.out.println("parameters:                                                      default:");
		System.out.println("-constClass <class>  generate class for message id constants");
		System.out.println("                     use #file to name the class like the excel file");
		System.out.println("-enc <encoding>      encoding of generated Java files            " + DEFAULT_JAVA_CHARSET);
		System.out.println("-idClass <class>     use the class instead of string message id");
		System.out.println("                     use #msgId to use org.civilian.text.msg.MsgId");
		System.out.println("                     use #inline to generate a inline id class");
		System.out.println("-javadoc             add translations as javadoc to constants");
		System.out.println("-v                   print progress");
		OutputLocation.printHelp(false);
	}	
	
	
	/**
	 * Runs the compiler.
	 */
	public boolean run(Config config) throws IOException 
	{
		config_ 		= config;
		generationTime_	= LocalDateTime.now();
		
		log("reading excel text definition " + config_.excelFile); 
		reader_ = new ExcelReader(config_.excelFile);
		if (!reader_.inputOk)
		{
			log("error: the input file does not seem to be a excel file");
			return false;
		}
		
		String packageName 	= null;
		if (config.outputType.needsPackage() || (config.constClass != null))
			packageName	= PackageDetector.DEFAULT.detect(config.excelFile);
		
		createLanguageOutputs(packageName, config_.excelFile);
		
		log("compiling");
		
		List<Translation> translations = compileBundles();
		if (config.constClass != null)
		{
			File constantsFile	= config_.outputType.getOutputFile(packageName, config.constClass + ".java", config.excelFile).file;
			constantsOutput_ 	= new Output(constantsFile);
			compileConstants(packageName, translations);
		}
		
		if (constantsOutput_ != null)
			constantsOutput_.writeToFile(config_.encoding);
		for (Output langOutput : langOutputs_)
			langOutput.writeToFile(PROPERTIES_CHARSET);
		
		log("done");

		return true;
	}
	
	
	private void createLanguageOutputs(String packageName, File excelFile) throws IOException
	{
		log("read languages");
		
		if (!reader_.nextLine())
			throw new IOException(config_.excelFile + " is empty");
		reader_.nextString(); // header of keycol

		List<Output> list = new ArrayList<>();
		String filePrefix = IoUtil.cutExtension(config_.excelFile) + "_";
		String language;
		while((language = reader_.nextString()) != null)
		{
			log("- add language " + language);

			String langFileName = filePrefix + language + ".properties"; 
			File langFile = config_.outputType.getOutputFile(packageName, langFileName, excelFile).file; 
			list.add(new Output(langFile));
		}
		
		list.toArray(langOutputs_ = new Output[list.size()]);
	}


	private List<Translation> compileBundles()
	{
		List<Translation> translations = new ArrayList<>(); 
		
		for (Output output : langOutputs_)
		{
			CspWriter out = output.out;
			out.print("# Generated from ");
			out.print(config_.excelFile.getName());
			out.print(" at ");
			out.println(generationTime_);
			out.println("# Do not edit directly.");
			out.print("# Encoding is ");
			out.println(PROPERTIES_CHARSET);
		}
		
		while(reader_.nextLine())
		{
			String id = reader_.nextString();
			if (id == null)
				continue;
			id = id.trim();
			if (id.length() == 0)
				continue;
			
			Translation t = new Translation(id, langOutputs_.length);
			translations.add(t);
			
			for (int i=0; i<langOutputs_.length; i++)
			{
				String text = reader_.nextString();
				if (text == null)
					text = "?" + id;
				else
					text = text.trim();
				
				t.lang[i] = text;
				
				CspWriter out = langOutputs_[i].out;
				out.print(id);
				out.print("=");
				
				int len = text.length();
				for (int j=0; j<len; j++)
				{
					char c = text.charAt(j);
					if (c <= 255)
						out.print(c);
					else
					{
						out.print("\\u");
						out.print(StringUtil.fillLeft(Integer.toHexString(c), 4, '0'));
					}
				}
				out.println();
			}
		}
		
		return translations;
	}
	
	
	private void compileConstants(String packageName, List<Translation> translations)
	{
		translations.sort(null);
		ConstClassTemplate t = new ConstClassTemplate(config_, packageName, generationTime_, translations);
		t.print(constantsOutput_.out);
	}
	

	private void log(String message)
	{
		if (config_.verbose)
			System.out.println(message);
	}
	
	
	static class Config
	{
		public OutputLocation outputType = OutputLocation.OUTPUT_TO_INPUT_DIR;
		public String encoding = DEFAULT_JAVA_CHARSET;
		public File excelFile;
		public boolean inlineIdClass;
		public String idClass;
		public String constClass;
		public boolean verbose;
		public boolean javadoc;
	}
	
	
	private class Output
	{
		public Output(File file)
		{
			this.file = file;
			this.out  = new CspWriter(stringOut = new StringWriter());
		}

		
		public void writeToFile(String charSet) throws IOException
		{
			out.flush();
			
			log("writing " + file.getAbsolutePath()); 

			File parent = file.getParentFile();
			IoUtil.mkdirs(parent);
				
			try (Writer fileOut = new OutputStreamWriter(new FileOutputStream(file), charSet))
			{
				fileOut.write(stringOut.toString());
			}
		}
		
		
		public final File file;
		public CspWriter out;
		private StringWriter stringOut;
	}
	
	
	private static class ExcelReader
	{
		public ExcelReader(File file) throws IOException
		{
			try (InputStream in = new FileInputStream(file))
			{
				Workbook wb = WorkbookFactory.create(in);
				sheet_ 		= wb.getSheetAt(0);
				nextRow_	= sheet_.getFirstRowNum();
				inputOk		= true;
			}
			catch(IOException e)
			{
				if (e.getMessage().startsWith("Invalid header signature"))
					inputOk = false;
				else
					throw e;
			}
		}
		
		
		public boolean nextLine()
		{
			nextCell_ = 0;
			row_ = sheet_.getRow(nextRow_++);
			return row_ != null;
		}
		
		
		public String nextString()
		{
			return nextString(null);
		}
		
		
		public String nextString(String defaultValue)
		{
			Cell cell = row_.getCell(nextCell_++);
			try
			{
				if ((cell != null) && (cell.getCellType() == CellType.STRING))
					return cell.getStringCellValue();
				else
					return defaultValue;
			}
			catch(RuntimeException e)
			{
				System.err.println("exception in row " + nextRow_ + ", cell " + nextCell_);
				throw e;
			}
		}
		

		private Sheet sheet_;
		private Row row_;
		private int nextRow_;
		private int nextCell_;
		public boolean inputOk;
	}
	

	private Config config_;
	private Output constantsOutput_;
	private ExcelReader reader_;
	private Output[] langOutputs_;
	private LocalDateTime generationTime_;
}
