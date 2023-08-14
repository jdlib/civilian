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


import org.civilian.Request;
import org.civilian.controller.ControllerService;
import org.civilian.controller.ControllerType;
import org.civilian.resource.Resource;
import org.civilian.util.Check;


/**
 * A Processor which finds the resource which matches a 
 * request and invokes the controller of the resource
 * to handle the request.
 */
public class ResourceDispatch extends Processor
{
	public ResourceDispatch(Resource rootResource)
	{
		rootResource_ = Check.notNull(rootResource, "rootResource");
		
		ControllerService service = rootResource.getTree().getControllerService();
		if (service == null)
			throw new IllegalStateException("no controller service set on resource tree");
		info_ = "service: " + service.toString() + ": reloading " + service.isReloading();
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
	@Override public boolean process(Request request, ProcessorChain chain) throws Exception
	{
		// find the matching resource: we only handle complete matches
		Resource.Match match = rootResource_.match(request.getRelativePath().toString());
		if (match.completeMatch)
		{
			request.setResource(match.resource);
			request.setPathParams(match.pathParams);
			
			ControllerType ctrlType	= match.resource.getControllerType();
			if (ctrlType != null)
			{
				// resource is associated with a controller
				ctrlType.createController().process(request);
				return true; // we handled the request
			}
		}
		
		// else invoke the next processor 
		return chain.next(request);
	}


	private final Resource rootResource_;
	private final String info_;
}
