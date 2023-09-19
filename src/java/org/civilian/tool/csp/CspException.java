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


import org.civilian.util.Scanner;


/**
 * CspException is used by TemplateCompiler
 * to indicate CSP syntax errors when compiling a template file.
 */
public class CspException extends RuntimeException
{
	private static final long serialVersionUID = 1L; 

	
	/**
	 * Creates a new CspException.
	 * @param msg an error message.
	 * @param cause the original error cause, can be null
	 * @param scanner the used scanner, providing context information about the compiled input
	 */
	public CspException(String msg, Throwable cause, Scanner scanner)
	{
		super(Scanner.buildErrorMessage(msg, cause, scanner), cause);
		if (scanner != null)
		{
			templateFile_	= scanner.input.getSource();
			lineIndex_		= scanner.input.getLineIndex();
			line_			= scanner.getLine();
		}
	}

	
	/**
	 * @return the line index of the error causing line, or -1 if not known.
	 */
	public int getLineIndex()
	{
		return lineIndex_;
	}
	

	/**
	 * @return the line that caused the error, or null if not known.
	 */
	public String getLine()
	{
		return line_;
	}
	

	/**
	 * @return the template  file, or null if not known. 
	 */
	public String getTemplateFile()
	{
		return templateFile_;
	}

	
	private String templateFile_;
	private String line_;
	private int lineIndex_ = -1;
}
