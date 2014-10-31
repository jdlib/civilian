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


import org.civilian.processor.AssetDispatch;
import org.civilian.processor.ProcessorChain;
import org.civilian.processor.ResourceDispatch;


/**
 * A Processor represents a service within an application which processes a request.
 * An application can have several processors which are arranged in a pipeline
 * and are executed one after another until a processor signals that
 * it has handled the request.
 * Processor implementations exist to handle requests for dynamic
 * resources (see {@link ResourceDispatch}), static resources (aka assets)
 * (see {@link AssetDispatch}).
 * @see Application#initProcessors(ProcessorConfig)
 */
public abstract class Processor
{
	/**
	 * Processes the request.
	 * @param request the request
	 * @param chain the processor chain. If the processor did not completely processed
	 * 		the request, then {@link ProcessorChain#next(Request)} should be called.
	 * @return true if the request was processed and no further processing should
	 * 		be applied.
	 */
	public abstract boolean process(Request request, ProcessorChain chain) throws Exception;

	
	/**
	 * Returns information about the processor.
	 */
	public String getInfo()
	{
		return info_;
	}
	
	
	/**
	 * Called when the application closes. The processor should free any resources.
	 * The default implementation does nothing.
	 */
	public void close()
	{
	}
	
	
	/**
	 * The info string returned by the default implementation of {@link #getInfo()}.
	 * Processor implementation should initialize the info string in their constructor.
	 */
	protected String info_ = ""; 
}
