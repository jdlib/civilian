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
import org.civilian.util.Check;


/**
 * CspTemplate is a template to which uses a TemplateWriter.
 */
public abstract class CspTemplate extends Template implements TemplateWriter.Printable
{
	/**
	 * @return the TemplateWriter or null, if the template is not printed.
	 */
	public TemplateWriter out()
	{
		return out;
	}

	
	/**
	 * Prints the template, using the given Writer.
	 * The method constructs a TemplateWriter from the writer and
	 * then calls {@link #print(TemplateWriter)}.
	 * @param out a Writer
	 * @param data optioanl context data
	 * @throws Exception any exception
	 */
	@Override public void print(PrintWriter out, Object... data) throws Exception
	{
		Check.notNull(out, "out");
		if (out instanceof TemplateWriter)
			print((TemplateWriter)out);
		else
		{
			TemplateWriter tw = new TemplateWriter(out, false);
			if (data.length > 0)
				tw.getData().addAll(data);
			print(tw);
		}
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
