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


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.civilian.application.Application;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;


/**
 * ProcessorConfig is used during application setup to configure
 * the processor pipeline. 
 * @see Application#initProcessors(ProcessorConfig)
 */
public class ProcessorConfig
{
	/**
	 * Adds a processor to the processor list at the first position.
	 */
	public void addFirst(Processor processor)
	{
		Check.notNull(processor, "processor");
		list_.add(0, processor);
	}
	
	
	/**
	 * Adds a processor to the processor list at the last position.
	 */
	public void addLast(Processor processor)
	{
		Check.notNull(processor, "processor");
		list_.add(processor);
	}


	/**
	 * Adds a processor to the processor list before the processor with the given class.
	 */
	public void addBefore(Class<? extends Processor> beforeClass, Processor processor)
	{
		Check.notNull(beforeClass, "beforeClass");
		Check.notNull(processor, "processor");
		
		int index = indexOf(beforeClass);
		if (index < 0)
			throw new NoSuchElementException(beforeClass.getName());
		
		list_.add(index, processor);
	}

	
	/**
	 * Adds a processor to the processor list after the processor with the given class.
	 */
	public void addAfter(Class<? extends Processor> afterClass, Processor processor)
	{
		Check.notNull(afterClass, "afterClass");
		Check.notNull(processor, "processor");
		
		int index = indexOf(afterClass);
		if (index < 0)
			throw new NoSuchElementException(afterClass.getName());
		
		list_.add(index + 1, processor);
	}

	
	/**
	 * Returns the processor list for direct manipulation.
	 */
	public List<Processor> getList()
	{
		return list_;
	}
	

	/**
	 * Returns the first position of a processor with the given
	 * class within the list. 
	 */
	public int indexOf(Class<? extends Processor> processorClass)
	{
		int size = list_.size();
		for (int i=0; i<size; i++)
		{
			if (ClassUtil.isA(list_.get(i), processorClass))
				return i;
		}
		return -1;
	}
	
	
	private final List<Processor> list_ = new ArrayList<>();
}
