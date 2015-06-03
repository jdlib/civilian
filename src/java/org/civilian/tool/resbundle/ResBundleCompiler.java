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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.civilian.internal.source.OutputLocation;
import org.civilian.internal.source.PackageDetector;
import org.civilian.template.TemplateWriter;
import org.civilian.text.msg.MsgId;
import org.civilian.util.Arguments;
import org.civilian.util.DateTime;
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
	private static final String PROPERTIES_ENCODING = "ISO-8859-1";
	private static final String DEFAULT_JAVA_ENCODING = "UTF-8";
	
	
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
		System.out.println("-enc <encoding>      encoding of generated Java files            " + DEFAULT_JAVA_ENCODING);
		System.out.println("-idClass <class>     use the class instead of string message id");
		System.out.println("                     use #msgId to use org.civilian.text.msg.MsgId");
		System.out.println("                     use #inline to generate a inline id class");
		System.out.println("-v                   print progress");
		OutputLocation.printHelp(false);
	}	
	
	
	/**
	 * Runs the compiler.
	 */
	public boolean run(Config config) throws IOException 
	{
		config_ 			= config;
		generationTime_		= new DateTime();
		
		log("reading excel text definition " + config_.excelFile); 
		reader_ = new ExcelIterator(config_.excelFile);
		if (!reader_.inputOk)
		{
			log("error: the input file does not seem to be a excel file");
			return false;
		}
		
		String packageName 	= null;
		if (config.outputType.needsPackage() || (config.constClass != null))
			packageName	= PackageDetector.detectPackage(config.excelFile);
		
		createLanguageOutputs(packageName, config_.excelFile);
		
		log("compiling");
		ArrayList<String> msgIds = compileBundles();
		if (config.constClass != null)
		{
			File constantsFile	= config_.outputType.getOutputFile(packageName, config.constClass + ".java", config.excelFile).file;
			constantsOutput_ 	= new Output(constantsFile);
			compileConstants(packageName, msgIds);
		}
		
		if (constantsOutput_ != null)
			constantsOutput_.writeToFile(config_.encoding);
		for (Output langOutput : langOutputs_)
			langOutput.writeToFile(PROPERTIES_ENCODING);
		
		log("done");

		return true;
	}
	
	
	private void createLanguageOutputs(String packageName, File excelFile) throws IOException
	{
		log("read languages");
		
		if (!reader_.nextLine())
			throw new IOException(config_.excelFile + " is empty");
		reader_.nextString(); // header of keycol

		ArrayList<Output> list = new ArrayList<>();
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


	private ArrayList<String> compileBundles()
	{
		ArrayList<String> ids = new ArrayList<>(); 
		
		for (int i=0; i<langOutputs_.length; i++)
		{
			TemplateWriter out = langOutputs_[i].out;
			out.println("# Generated from " + config_.excelFile.getName() + " at " + generationTime_);
			out.println("# Do not edit directly.");
			out.println("# Encoding is " + PROPERTIES_ENCODING);
		}
		
		while(reader_.nextLine())
		{
			String id = reader_.nextString();
			if (id == null)
				continue;
			id = id.trim();
			if (id.length() == 0)
				continue;
			
			ids.add(id);
			
			for (int i=0; i<langOutputs_.length; i++)
			{
				String text = reader_.nextString();
				if (text == null)
					text = "?" + id;
				else
					text = text.trim();
				
				TemplateWriter out = langOutputs_[i].out;
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
						out.print(StringUtil.fillLeft(c, 4));
					}
				}
				out.println();
			}
		}
		
		return ids;
	}
	
	
	private void compileConstants(String packageName, ArrayList<String> ids)
	{
		Collections.sort(ids);
		ConstClassTemplate t = new ConstClassTemplate(config_, packageName, generationTime_, ids);
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
		public String encoding = DEFAULT_JAVA_ENCODING;
		public File excelFile;
		public boolean inlineIdClass;
		public String idClass;
		public String constClass;
		public boolean verbose;
	}
	
	
	private class Output
	{
		public Output(File file)
		{
			this.file = file;
			this.out  = new TemplateWriter(stringOut = new StringWriter());
		}

		
		public void writeToFile(String encoding) throws IOException
		{
			out.flush();
			
			log("writing " + file.getAbsolutePath()); 

			File parent = file.getParentFile();
			IoUtil.mkdirs(parent);
				
			Writer fileOut = new OutputStreamWriter(new FileOutputStream(file), encoding);
			try
			{
				fileOut.write(stringOut.toString());
			}
			finally
			{
				fileOut.close();
			}
		}
		
		
		
		public final File file;
		public TemplateWriter out;
		private StringWriter stringOut;
	}
	
	
	private static class ExcelIterator
	{
		public ExcelIterator(File file) throws IOException
		{
			try
			{
				Workbook wb = WorkbookFactory.create(file);
				sheet_ 		= wb.getSheetAt(0);
				nextRow_	= sheet_.getFirstRowNum();
				inputOk	= true;
			}
			catch(InvalidFormatException e)
			{
				inputOk = false;
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
				if ((cell != null) && (cell.getCellType() == Cell.CELL_TYPE_STRING))
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
	private ExcelIterator reader_;
	private Output[] langOutputs_;
	private DateTime generationTime_;
}
