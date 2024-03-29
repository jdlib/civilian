package org.civilian.tool.csp;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.civilian.template.CspTemplate;
import org.civilian.template.CspWriter;
import org.civilian.tool.source.JavaPackageDetector;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;
import org.civilian.util.Scanner;
import org.civilian.util.StringUtil;


class CspClassParser 
{
	public CspClassParser(Scanner scanner, ClassData classData, Map<String,MixinField> registeredMixins)
	{
		scanner_ 			= Check.notNull(scanner, "scanner");
		classData_ 			= Check.notNull(classData, "classData");
		registeredMixins_	= Check.notNull(registeredMixins, "registeredMixins");
	}
	
	
	public void parsePackageCmd(File templFile, String assumedPackage)
	{
		if (scanner_.nextKeyword("package"))
		{
			classData_.packageName = StringUtil.cutRight(scanner_.expect("package").nextToken(), ";");
			if ((assumedPackage != null) && !assumedPackage.equals(classData_.packageName))
				scanner_.exception("package was set by compiler parameters to '" + assumedPackage + ", but the template specified '" + classData_.packageName + "'");
		}
		else if (assumedPackage != null)
			classData_.packageName = assumedPackage;
		else
		{
			String p = JavaPackageDetector.DEFAULT.detect(templFile);
			if (p == null)
				scanner_.exception("cannot template package, please provide an explicit package in the template");
			classData_.packageName = p;
		}
	}

	
	public void parseImportCmds()
	{
		while(scanner_.nextKeyword("import"))
		{
			String s = scanner_.expect("import").nextToken(";");
			s = resolveRelativeImport(s);
			classData_.imports.add(s);
			scanner_.next(';');
		}
	}


	public void parsePrologCmds()
	{
		while(scanner_.nextKeyword("prolog"))
			classData_.prolog.add(scanner_.nextRest());
	}


	public void parseTemplateCmd() throws IOException
	{
		//-------------------------------------
		// "template"
		if (!scanner_.nextKeyword("template"))
			scanner_.exception("expected the template command, but reached end of file");
		parseTemplateArgs();

		//-------------------------------------
		// "package-access"
		if (scanner_.nextKeyword("package-access"))
			classData_.isPublic = false;

		//-------------------------------------
		// "abstract"
		if (scanner_.nextKeyword("abstract"))
			classData_.isAbstract = true;

		//-------------------------------------
		// "extends"
		if (scanner_.nextKeyword("extends"))
		{
			if (scanner_.next('-'))
			{
				classData_.standalone   = true;
				classData_.extendsClass = null;

				String writerClass = CspWriter.class.getSimpleName();
				if (scanner_.nextKeyword("using"))
					writerClass = scanner_.expect("using").nextToken();

				if ("CspWriter".equals(writerClass))
					writerClass = CspWriter.class.getName();

				classData_.writerClass = writerClass;
				classData_.writerClassSimple = ClassUtil.cutPackageName(writerClass);
				classData_.imports.add(writerClass);
			}
			else
			{
				classData_.extendsClass = scanner_.expect("extends").nextToken("(");
				parseTemplateSuperArgs();
			}

		}
		if ((classData_.extendsClass == null) && !classData_.standalone)
		{
			classData_.imports.add(CspTemplate.class);
			classData_.extendsClass = CspTemplate.class.getSimpleName();
		}

		//-------------------------------------
		// "implements"
		if (scanner_.nextKeyword("implements"))
			classData_.implementsList = parseClassList();

		//-------------------------------------
		// "mixin"
		if (scanner_.nextKeyword("mixin"))
			parseMixins();

		//-------------------------------------
		// "throws"
		if (scanner_.nextKeyword("throws"))
		{
			classData_.exception = parseClassList();
			if ("-".equals(classData_.exception))
				classData_.exception = null;
		}

		if ((scanner_.getPos() > 0) && scanner_.hasMoreChars())
			scanner_.exception("invalid input: '" + scanner_.getRest() + "'");
		
		if (scanner_.getLine().trim().equals(CspSymbols.START_TEMPLATE_SECTION))
			classData_.hasMainTemplate = true;
	}

	
	private void parseTemplateArgs() throws IOException
	{
		if (!scanner_.next('('))
			return;
		if (scanner_.next(')'))
			return;

		StringBuilder argsString = new StringBuilder();

		while(true)
		{
			Argument argument = new Argument(scanner_);
			classData_.arguments.add(argument);

			if (argsString.length() > 0)
				argsString.append(", ");
			argument.ctorArg(argsString);

			if (scanner_.next(')'))
				break;
			if (!scanner_.next(','))
				scanner_.exception("expected closing bracket ')' of template argument list");
		}

		classData_.args = argsString.toString();
	}


	private void parseTemplateSuperArgs() throws IOException
	{
		if (!scanner_.next('('))
			return; // no super args

		StringBuilder superArgs	= new StringBuilder();

		int bracketLevel = 1;
		while(bracketLevel > 0)
		{
			String part = scanner_.nextUpto("\"'()", false, false, false, false);
			superArgs.append(part);
			if (!scanner_.hasMoreChars())
				scanner_.exception("template super must be finished on the beginning line");
			switch (scanner_.current())
			{
				case '"':
				case '\'':
					superArgs.append(scanner_.nextQuotedString(true));
					break;
				case '(':
					bracketLevel++;
					superArgs.append('(');
					break;

				case ')':
					bracketLevel--;
					if (bracketLevel > 0)
						superArgs.append(')');
					else
						scanner_.skip();
					break;
			}
		}

		classData_.superArgs = superArgs.toString();
	}


	private String parseClassList() throws CspException
	{
		StringBuffer list = new StringBuffer();
		while(true)
		{
			String type = scanner_.expect("implements").nextToken(",");
			if (list.length() > 0)
				list.append(", ");
			list.append(type);
			if (!scanner_.next(','))
				break;
		}
		return list.toString();
	}


	private void parseMixins() throws CspException
	{
		do
		{
			String def = scanner_.expect("mixin def").nextToken(",");
			String[] parts = def.split(":", 2);
			if (parts.length > 0)
			{
				String className = parts[0];
				String fieldName = parts.length > 1 ? parts[1] : null;
				parseMixin(className, fieldName);
			}
		}
		while(scanner_.next(','));
	}


	private void parseMixin(String className, String fieldName)
	{
		MixinField registeredMixin = registeredMixins_.get(className);
		if (registeredMixin != null)
		{
			if (fieldName == null)
				fieldName = registeredMixin.fieldName;
			className = registeredMixin.className;
		}

		classData_.mixins.add(new MixinField(className, fieldName));
		classData_.imports.add(className);
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
					scanner_.exception("relative import '" + s + "' can't be applied to package '" + classData_.packageName + "'");
				prefix = prefix.substring(0, p);
				t = t.substring(3);
			}
			while(t.startsWith("../"));
			return prefix + '.' + t;
		}
		else
			return s;
	}
	
	
	private final Scanner scanner_;
	private final ClassData classData_;
	private final Map<String,MixinField> registeredMixins_;
}
