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
package org.civilian.template;


import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * A Template can be used to produce the textual content of a response.
 * Templates store all their dynamic data in instance variables which are initialized
 * in the template constructor:
 * Therefore a template object is self-contained and can just simply be printed 
 * to an output. This especially allows to pass templates to other templates 
 * as parameters and to build complex content by composing it from simpler parts.<br> 
 * Since template objects contain response specific data they - like Controllers - 
 * are instantiated for a single response and not reused.<br>
 * Civilian provides an own templating system and syntax (CSP), but other templates engines like
 * velocity or freemarker can easily be presented as Template objects. 
 */
public abstract class Template
{
	/**
	 * Prints the template and returns it as string.
	 * @return the string
	 * @throws Exception any exception allowed
	 */
	public String printString() throws Exception
	{
		StringWriter out = new StringWriter();
		print(new PrintWriter(out));
		return out.toString();
	}
	
	
	/**
	 * Prints the template, using the given Writer.
	 * The method constructs a TemplateWriter from the writer and
	 * then calls {@link #print(TemplateWriter)}.
	 * @param out a Writer
	 * @param data optional context data
	 * @throws Exception any exception
	 */
	public abstract void print(PrintWriter out, Object... data) throws Exception;
}
