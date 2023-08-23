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
package org.civilian.controller;


import org.civilian.util.Check;


/**
 * ControllerTypeProvider can return a ControllerType for a resource. 
 */
public interface ControllerTypeProvider
{
	public static final ControllerTypeProvider EMPTY 		= new EmptyTypeProvider();
	public static final ControllerTypeProvider UNINITIALIZED = new UninitializedTypeProvider();
	
	
	public static ControllerTypeProvider create(ControllerService service, ControllerSignature signature)
	{
		if (service.isReloading())
			return new ForwardingTypeProvider(service, signature);
		else
			return new CachingTypeProvider(service, signature);
	}
	
	
	public abstract ControllerType getControllerType();
}


/**
 * A ControllerTypeProvider for resources which are not associated with a controller.
 */
class EmptyTypeProvider implements ControllerTypeProvider
{
	@Override public ControllerType getControllerType()
	{
		return null;
	}
}


/**
 * A ControllerTypeProvider for resources of a tree whose ControllerService is not set.
 */
class UninitializedTypeProvider implements ControllerTypeProvider
{
	@Override public ControllerType getControllerType()
	{
		throw new IllegalStateException("ControllerTypeProvider not yet initialized");
	}
}


/**
 * A ControllerTypeProvider which obtains a ControllerType for the resource
 * from the ControllerService.
 * This is done again each time a request, good for development which
 * controller class reloading. 
 */
class ForwardingTypeProvider implements ControllerTypeProvider
{
	public ForwardingTypeProvider(ControllerService service, ControllerSignature signature)
	{
		service_ 	= Check.notNull(service, "service");
		signature_	= Check.notNull(signature, "signature");
	}
	
	
	@Override public ControllerType getControllerType()
	{
		return service_.getControllerType(signature_);
	}
	
	
	private final ControllerService service_;
	private final ControllerSignature signature_;
}


/**
 * A CachingTypeProvider which obtains a initial ControllerType for the resource
 * from the ControllerService. The ControllerType is cached and subsequent
 * invocations return the cached ControllerType.
 * Good for production mode. 
 */
class CachingTypeProvider extends ForwardingTypeProvider 
{
	public CachingTypeProvider(ControllerService service, ControllerSignature signature)
	{
		super(service, signature);
	}
	
	
	@Override public ControllerType getControllerType()
	{
		if (type_ == null)
			type_ = super.getControllerType();
		return type_;
	}
	

	private ControllerType type_;
}
