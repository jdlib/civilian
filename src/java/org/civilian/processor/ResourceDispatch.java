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


import org.civilian.controller.ControllerType;
import org.civilian.controller.ControllerTypeProvider;
import org.civilian.request.Request;
import org.civilian.resource.Resource;
import org.civilian.response.Response;
import org.civilian.util.Check;


/**
 * A Processor which finds the resource which matches a 
 * request and invokes the controller of the resource
 * to handle the request.
 */
public class ResourceDispatch extends Processor
{
	public ResourceDispatch(Resource root, String info)
	{
		root_ 	 = Check.notNull(root, "root");
		info_ 	 = Check.notNull(info, "info");
	}

	
	public ResourceDispatch(Resource root)
	{
		this(root, ResourceDispatch.class.getName());
	}

	
	@Override public String getInfo() 
	{
		return info_;
	}

	
	/**
	 * Finds the resource which matches a request and invokes the controller of the resource
	 * to handle the request. If such a resource or controller does not exist,
	 * invoke the next processor in the chain.
	 */
	@Override public boolean process(Request request, Response response, ProcessorChain chain) throws Exception
	{
		// find the matching resource: we only handle complete matches
		Resource.Match match = root_.match(request.getRelativePath().toString());
		if (match.completeMatch)
		{
			ControllerType controllerType = ControllerTypeProvider.getType(match.resource);
			if (controllerType != null)
			{
				request.setResource(match.resource);
				request.setPathParams(match.pathParams);
				
				// resource is associated with a controller
				controllerType.createController().process(request, response);
				return true; // we handled the request
			}
		}
		
		// else invoke the next processor 
		return chain.next(request, response);
	}


	private final Resource root_;
	private final String info_;
}
