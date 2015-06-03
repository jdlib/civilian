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
package org.civilian.response;


import java.io.Writer;
import org.civilian.Response;
import org.civilian.Template;
import org.civilian.content.ContentSerializer;
import org.civilian.template.TemplateWriter;


/**
 * ResponseWriter is used to write a character text response.
 * You could use it directly to write the response, but in most cases
 * it will be passed to a {@link Template} or {@link ContentSerializer}
 * which will write their content to it.
 * It can be obtained by calling {@link Response#getContentWriter()}.
 * The ResponseWriter provides the following functions:
 * <ul>
 * <li>it is a PrintWriter
 * <li>it is a TemplateWriter i.e. allows convenient indenting of content
 * </ul>    
 */
public class ResponseWriter extends TemplateWriter
{
	/**
	 * Creates a new ResponseWriter. 
	 * @param writer the underlying writer 
	 */
	public ResponseWriter(Writer writer)
	{
		this(writer, false);
	}
	
	
	/**
	 * Creates a new ResponseWriter. 
	 * @param writer the underlying writer 
	 * @param autoFlush - a boolean; if true, the println() methods will flush
	 *      the output buffer
	 */
	public ResponseWriter(Writer writer, boolean autoFlush)
	{
		super(writer, autoFlush);
	}

	
	//------------------------
	// print methods
	//------------------------

	
	/**
	 * Checks if the object is a printable and in that case calls
	 * print(Printable), else it just calls the default implementation.
	 */
	@Override public void print(Object object)
	{
		if (object instanceof Printable)
			print((Printable)object);
		else
			super.print(object);
	}
	
	
	/**
	 * If a not-null Printable is passed to the ResponseWriter,
	 * then the printable is asked to print itself. 
	 */
	public void print(Printable printable)
	{
		if (printable != null)
		{
			try
			{
				printable.print(this);
			}
			catch (RuntimeException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				throw new IllegalStateException("error when printing '" + printable.getClass().getName() + "'", e);
			}
		}
	}
	
	
	/**
	 * Printable is a interface for print-aware classes who 
	 * implement a custom print strategy. Templates and form controls
	 * are examples of Printables.
	 * If you pass a Printable to {@link ResponseWriter#print(Object)} or
	 * ResponseWriter#print(Printable), then the {@link #print(ResponseWriter)}
	 * method of the Printable is called by the ResponseWriter, allowing
	 * to Printable to print itself.
	 */
	public static interface Printable 
	{
		public void print(ResponseWriter out) throws Exception;
	}
}
