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


import java.io.StringWriter;
import java.io.Writer;
import org.civilian.util.Check;


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
public abstract class Template implements TemplateWriter.Printable
{
	/**
	 * @return the TemplateWriter or null, if the template is not printed.
	 */
	public TemplateWriter out()
	{
		return out;
	}

	
	/**
	 * Prints the template and returns it as string.
	 * @return the string
	 * @throws Exception any exception allowed
	 */
	public String printString() throws Exception
	{
		StringWriter out = new StringWriter();
		print(out);
		return out.toString();
	}
	
	
	/**
	 * Prints the template, using the given Writer.
	 * The method constructs a TemplateWriter from the writer and
	 * then calls {@link #print(TemplateWriter)}.
	 * @param out a Writer
	 * @throws Exception any exception
	 */
	public void print(Writer out) throws Exception
	{
		Check.notNull(out, "out");
		TemplateWriter tw = out instanceof TemplateWriter ? 
			(TemplateWriter)out : 
			new TemplateWriter(out, false);
		print(tw);
	}
	
	
	/**
	 * Prints the template, using the given TemplateWriter.
	 * The method stores the TemplateWriter in the field {@link #out}
	 * and the calls {@link #print()}.
	 */
	@Override public synchronized void print(TemplateWriter out) throws Exception
	{
		Check.notNull(out, "out");
		if (this.out != null)
			throw new IllegalStateException("already printing");
		try
		{
			this.out = out;
			init();
			print();
		}
		finally
		{
			this.out = null;
			exit();
		}
	}
	
	
	/**
	 * Allows derived implementation to initialize before the template is printed .
	 * Called by {@link #print(TemplateWriter)} when {@link #out} was set, before {@link #print()} is called.
	 * The default implementation is empty.
	 */
	protected void init()
	{
	}
	
	
	/**
	 * Allows derived implementation to performe operations after the template is printed .
	 * Called by {@link #print(TemplateWriter)} after {@link #print()} was called.
	 * The default implementation is empty.
	 */
	protected void exit()
	{
	}

	
	/**
	 * Needs to be implemented by derived classes.
	 * @throws Exception any exception
	 */
	protected abstract void print() throws Exception;
	
	
	protected TemplateWriter out;
}
