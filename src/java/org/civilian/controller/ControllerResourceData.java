package org.civilian.controller;


import org.civilian.resource.ControllerTypeProvider;
import org.civilian.resource.Resource;
import org.civilian.util.Check;


/**
 * ControllerResourceData is placed into Resource objects
 * to associate resources with controller implementations.
 * @see Resource#setData(Object)
 * @see Resource#getData(Object)
 */
public class ControllerResourceData 
{
	public static ControllerResourceData of(Resource resource)
	{
		return resource.getData(ControllerResourceData.class);
	}

	
	public ControllerResourceData(ControllerSignature signature)
	{
		signature_ = Check.notNull(signature, "signature");
	}
	
	
	public ControllerSignature getSignature()
	{
		return signature_;
	}
	
	
	public ControllerTypeProvider getTypeProvider()
	{
		return typeProvider_;
	}
	

	public void setTypeProvider(ControllerTypeProvider provider)
	{
		if (typeProvider_ != null)
			throw new IllegalStateException("no type provider already set");
		typeProvider_ = provider;
	}

	
	private final ControllerSignature signature_; 
	private ControllerTypeProvider typeProvider_; 
}
