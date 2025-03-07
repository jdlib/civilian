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
package org.civilian.processor;


import java.util.Iterator;
import java.util.List;
import org.civilian.request.Request;
import org.civilian.response.Response;
import org.civilian.util.Check;
import org.civilian.util.Iterators;


/**
 * ProcessorList contains a list of Processors
 * and executes them one after another until a processor
 * signals that it has responded to the request
 * and no further processing should be done.
 */
public class ProcessorList extends Processor implements Iterable<Processor>
{
	public static final ProcessorList EMPTY = new ProcessorList();
	
	
	public ProcessorList(List<Processor> processors)
	{
		this(processors.toArray(new Processor[processors.size()]));
	}
	
	
	public ProcessorList(Processor... processors)
	{
		processors_ = processors;
		for (int i=0; i<processors.length; i++)
			Check.notNull(processors[i], "processor");
	}
	
	
	@Override public String getInfo() 
	{
		return "size " + processors_.length;
	}

	
	public int size()
	{
		return processors_.length;
	}
	

	/**
	 * @param i a processor index
	 * @return the i-th processor in the list.
	 */
	public Processor get(int i)
	{
		return processors_[i];
	}
	
	
	/**
	 * @param procClass a processor class
	 * @return the first processor in the list with the given
	 * class or null if none found.
	 */
	public Processor get(Class<? extends Processor> procClass)
	{
		if (procClass != null)
		{
			for (int i=0; i<processors_.length; i++)
			{
				if (procClass.isAssignableFrom(processors_[i].getClass()))
					return processors_[i];
			}
		}
		return null;
	}
	
	
	@Override public Iterator<Processor> iterator()
	{
		return Iterators.forValues(processors_);
	}
	
	
	public boolean process(Request request, Response response) throws Exception
	{
		ProcessorChain chain = new ProcessorChain(processors_);
		return chain.next(request, response);
	}
	
	
	@Override public boolean process(Request request, Response response, ProcessorChain chain) throws Exception
	{
		return process(request, response) || chain.next(request, response);
	}
	
	
	/**
	 * Closes all contained processors.
	 */
	@Override public void close()
	{
		for (int i=0; i<processors_.length; i++)
			processors_[i].close();
	}


	private final Processor[] processors_;
}
