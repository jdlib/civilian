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
package org.civilian.internal.resource;


import org.civilian.controller.ControllerService;
import org.civilian.controller.ControllerType;
import org.civilian.resource.Resource;


/**
 * ControllerTypeProvider can return a ControllerType for a resource. 
 */
public abstract class ControllerTypeProvider
{
	public static final ControllerTypeProvider EMPTY 		= new EmptyTypeProvider();
	private static final ControllerTypeProvider UNAVAILABLE = new UnavailableTypeProvider();
	private static final ControllerTypeProvider FORWARDING  = new ForwardingTypeProvider();
	
	
	public static ControllerTypeProvider create(Resource resource)
	{
		if (resource.getControllerSignature() == null)
			return EMPTY;
		
		ControllerService service = resource.getTree().getControllerService();
		if (service == null)
			return UNAVAILABLE;
		else if (service.isReloading())
			return FORWARDING;
		else
			return new CachingTypeProvider();
	}
	
	public abstract ControllerType getControllerType(Resource resource) throws IllegalStateException;
}


/**
 * A ControllerTypeProvider for resources which are not associated with a controller.
 */
class EmptyTypeProvider extends ControllerTypeProvider
{
	@Override public ControllerType getControllerType(Resource resource)
	{
		return null;
	}
}


/**
 * A ControllerTypeProvider for resources of a tree whose ControllerService is not set.
 */
class UnavailableTypeProvider extends ControllerTypeProvider
{
	@Override public ControllerType getControllerType(Resource resource)
	{
		throw new IllegalStateException("ControllerType unavailable: resource not connected with ControllerService");
	}
}


/**
 * A ControllerTypeProvider which obtains a ControllerType for the resource
 * from the ControllerService.
 * This is done again each time a request, good for development which
 * controller class reloading. 
 */
class ForwardingTypeProvider extends ControllerTypeProvider
{
	@Override public ControllerType getControllerType(Resource resource)
	{
		return resource.getTree().getControllerService().getControllerType(resource.getControllerSignature());
	}
}


/**
 * A CachingTypeProvider which obtains a initial ControllerType for the resource
 * from the ControllerService. The ControllerType is cached and subsequent
 * invocations return the cached ControllerType.
 * Good for production mode. 
 */
class CachingTypeProvider extends ControllerTypeProvider
{
	@Override public ControllerType getControllerType(Resource resource)
	{
		if (type_ == null)
			type_ = resource.getTree().getControllerService().getControllerType(resource.getControllerSignature());
		return type_;
	}
	
	
	private ControllerType type_;
}
