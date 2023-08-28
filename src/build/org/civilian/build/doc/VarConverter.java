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
package org.civilian.internal.build;


import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.civilian.Version;
import org.civilian.util.ClassUtil;


public class VarConverter
{
	private static final String GITHUB_REPO = "https://github.com/jdlib/civilian/";
	private static final String GITHUB_REPO_RELEASES = GITHUB_REPO + "releases"; 
	private static final String GITHUB_REPO_RELEASES_DOWNLOAD_LATEST = GITHUB_REPO_RELEASES + "/download/" + Version.VALUE + '/'; 
	private static final String GITHUB_REPO_RELEASES_LATEST = GITHUB_REPO_RELEASES + "/tag/" + Version.VALUE; 
		
		
	public VarConverter(File javaDocDir, File parentFile, File inputFile)
	{
		javaDocDir_	= javaDocDir;
		parentFile_	= parentFile;
		inputFile_ 	= inputFile;
	}
	
	
	public String convert(String line, int lineIndex)
	{
		lineIndex_ = lineIndex;
		
		Matcher matcher = VAR_PATTERN.matcher(line);
		if (matcher.find())
		{
			buffer_.setLength(0);
			int start = 0;
			do
			{
				buffer_.append(line, start, matcher.start());
				String key = matcher.group(1);
				String val = matcher.group(2);

				if ("javadoc".equals(key))
					convertJavadoc(val);
				else if ("nav".equals(key))
					convertNavItem(val);
				else if ("link".equals(key))
					convertLink(val);
				else if ("download".equals(key))
					convertDownload(val);
				else if ("downloadfile".equals(key))
					convertDownloadFile(val);
				else if ("downloadarchive".equals(key))
					convertDownloadArchive(val);
				else if ("source".equals(key))
					convertSource(val);
				else if ("version".equals(key))
					convertVersion();
				else if ("repo".equals(key))
					convertRepo();
				else
					error("unknown variable '" + key + '\'');
				
				start = matcher.end();
			}
			while(matcher.find(start));
			buffer_.append(line, start, line.length());
			return buffer_.toString();
		}
		else
			return line;
	}
	
	
	private void convertLink(String param)
	{
		splitParam(param, false);
		if (paramPart2_ == null)
			paramPart2_ = paramPart1_;
		appendLink(paramPart1_, paramPart2_, true);
	}
	
	
	private void convertJavadoc(String param)
	{
		splitParam(param, false);
		if (paramPart2_ == null)
		{
			paramPart2_ = ClassUtil.cutPackageName(paramPart1_);
			paramPart2_ = paramPart2_.replace("#", ".").replace("*", ".");
		}
		
		String javadoc = paramPart1_.replace('.', '/');
		       javadoc = javadoc.replace('*', '.');
		       javadoc = javadoc.replace('$', ' ');
		       
		String path;
		int hash = javadoc.indexOf('#');
		if (hash >= 0)
		{
			path = javadoc.substring(0, hash) + ".html";
			javadoc = javadoc.replace("#", ".html#");
		}
		else
		{
			javadoc += ".html";
			path = javadoc;
		}

		File javaDocFile = new File(javaDocDir_, "org/civilian/" + path);
		if (!javaDocFile.exists())
			warning(javaDocFile.getAbsolutePath() + " not found");
		
		String href = "javadoc/org/civilian/" + javadoc;
		appendLink(href, paramPart2_, "javadoc", true);
	}
	
	
	private void convertDownload(String param)
	{
		splitParam(param, false);
		if (paramPart2_ == null)
			paramPart2_ = param;
		
		appendLink(GITHUB_REPO_RELEASES_LATEST, paramPart2_, true);
	}
	
	
	private void convertDownloadFile(String param)
	{
		splitParam(param, false);
		String file;
		file = paramPart1_.replace("version", Version.VALUE);
		file = file.replace("civilian", "civilian-" + Version.VALUE);
		
		String text = paramPart2_ != null ? paramPart2_ : file; 
		appendLink(GITHUB_REPO_RELEASES_DOWNLOAD_LATEST + file, text, true);
	}
	
	
	private void convertDownloadArchive(String param)
	{
		appendLink(GITHUB_REPO_RELEASES, param, true);
	}

	
	private void convertVersion()
	{
		buffer_.append(Version.VALUE);
	}
	
	
	private void convertRepo()
	{
		buffer_.append(GITHUB_REPO);
	}

	
	private void convertNavItem(String param)
	{
		splitParam(param, true);
		
		buffer_.append("<li");
		if (paramPart1_.equals(parentFile_.getName()))
			buffer_.append(" class=\"active\"");
		buffer_.append("><a href=\"");
		buffer_.append(paramPart1_);
		buffer_.append('"');
		buffer_.append('>');
		buffer_.append(paramPart2_);
		buffer_.append("</a></li>");
	}

	
	private void convertSource(String className)
	{
		boolean isSample = className.contains("samples"); 
		String href = GITHUB_REPO + "blob/master/src/" + (isSample ? "samples" : "java") + '/' + className.replace('.', '/') + ".java";
		appendLink(href, className, true);
	}
	
	
	private void appendLink(String href, String text, boolean external)
	{
		appendLink(href, text, external, external); 
	}
	
	
	private void appendLink(String href, String text, boolean external, boolean blankTarget)
	{
		appendLink(href, text, external ? "extlink" : null, blankTarget);
	}
	
	
	private void appendLink(String href, String text, String classAttr, boolean blankTarget)
	{
		buffer_.append("<a");
		appendAttr("href", href);
		if (blankTarget)
			appendAttr("target", "_blank");
		if (classAttr != null)
			appendAttr("class", classAttr);
		buffer_.append('>');
		buffer_.append(text);
		buffer_.append("</a>");
	}
	

	private void appendAttr(String name, String value)
	{
		buffer_.append(' ');
		buffer_.append(name);
		buffer_.append("=\"");
		buffer_.append(value);
		buffer_.append('"');
	}


	private void splitParam(String param, boolean needs2Parts)
	{
		int space = param.indexOf(' ');
		if (space != -1)
		{
			paramPart1_ = param.substring(0, space);
			paramPart2_ = param.substring(space + 1);
		}
		else if (needs2Parts)
		{
			error("param '" + param + "' needs two parts");
		}
		else
		{
			paramPart1_ = param;
			paramPart2_ = null;
		}
	}

	
	private void error(String message)
	{
		throw new IllegalArgumentException(message + " (" + inputFile_.getName() + ", line " + (lineIndex_ + 1) + ')');
	}
	
	
	private void warning(String message)
	{
		System.err.println(message + " (" + inputFile_.getName() + ", line " + (lineIndex_ + 1) + ')');
	}

	
	private String paramPart1_;
	private String paramPart2_;
	private File parentFile_; 
	private File inputFile_;
	private File javaDocDir_;
	private int lineIndex_;
	private StringBuilder buffer_ = new StringBuilder();
	private static final Pattern VAR_PATTERN = Pattern.compile("\\{([a-z]+)\\:([^}]*)}");
}
