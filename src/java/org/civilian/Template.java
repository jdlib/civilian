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
package org.civilian;


import java.io.Writer;
import org.civilian.response.ResponseWriter;
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
public abstract class Template implements ResponseWriter.Printable
{
	/**
	 * Returns the ResponseWriter or null, if the template is not printed.
	 */
	public ResponseWriter out()
	{
		return out;
	}

	
	/**
	 * Prints the template, using the given Writer.
	 * The method constructs a ResponseWriter from the writer and
	 * then calls {@link #print(ResponseWriter)}.
	 */
	public void print(Writer out) throws Exception
	{
		Check.notNull(out, "out");
		ResponseWriter rw = out instanceof ResponseWriter ? 
			(ResponseWriter)out : 
			new ResponseWriter(out, false);
		print(rw);
	}
	
	
	/**
	 * Prints the template, using the given ResponseWriter.
	 * The method stores the ResponseWriter in the field {@link #out}
	 * and the calls {@link #print()}.
	 */
	@Override public synchronized void print(ResponseWriter out) throws Exception
	{
		Check.notNull(out, "out");
		this.out = out;
		try
		{
			print();
		}
		finally
		{
			this.out = null;
		}
	}
	
	
	/**
	 * Needs to be implemented by derived classes.
	 */
	protected abstract void print() throws Exception;
	
	
	protected ResponseWriter out;
}
