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


import org.civilian.request.Request;
import org.civilian.util.Check;


public class ProcessorChain
{
	public static final ProcessorChain EMPTY = new ProcessorChain();
	
	
	public ProcessorChain(Processor... processors)
	{
		processors_ = Check.notNull(processors, "processors");
	}
	
	
	public boolean next(Request request) throws Exception
	{
		if (next_ < processors_.length)
		{
			Processor nextProcessor = processors_[next_++];
			return nextProcessor.process(request, this);
		}
		else
			return false;
	}

	
	@Override public String toString()
	{
		return "ProcessorChain[" + next_ + "/" + processors_.length + "]";
	}
	
	
	private int next_;
	private Processor[] processors_;
}
