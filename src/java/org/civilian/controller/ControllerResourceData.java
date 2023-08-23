package org.civilian.controller;


import java.util.HashMap;
import java.util.Map;
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

	
	public static ControllerSignature getSignature(Resource resource)
	{
		ControllerResourceData data = of(resource);
		return data != null ? data.getSignature() : null;
	}

	
	public static ControllerTypeProvider getTypeProvider(Resource resource)
	{
		ControllerResourceData data = of(resource);
		return data != null ? data.getTypeProvider() : ControllerTypeProvider.EMPTY;
	}

	
	public static ControllerType getType(Resource resource)
	{
		return getTypeProvider(resource).getControllerType();
	}

	
	public static Map<ControllerSignature,Resource> initTypeProviders(Resource resource, ControllerService service)
	{
		Map<ControllerSignature,Resource> sig2resource = new HashMap<>();
		initTypeProviders(resource, service, sig2resource);
		return sig2resource;
	}
		
	private static void initTypeProviders(Resource resource, ControllerService service, Map<ControllerSignature,Resource> sig2resource)
	{
		ControllerResourceData data = ControllerResourceData.of(resource);
		if (data != null)
		{
			ControllerSignature sig = data.getSignature();
			data.setTypeProvider(ControllerTypeProvider.create(service, sig));
			sig2resource.put(sig, resource);
		}		
		
		for (int i=0; i<resource.getChildCount(); i++)
			initTypeProviders(resource.getChild(i), service, sig2resource);
	}

	
	/**
	 * Recursively go trough the resource tree and instantiate the controller classes.
	 * This allows for a test during application startup (in production mode) 
	 * if the resource tree has valid controller classes.   
	 */
	public static void touchControllerClasses(Resource resource) throws ClassNotFoundException
	{
		ControllerSignature signature = getSignature(resource);
		if (signature != null)
			Class.forName(signature.getClassName());
		for (int i=0; i<resource.getChildCount(); i++)
			touchControllerClasses(resource.getChild(i));
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
		ControllerTypeProvider tp = typeProvider_;
		return tp != null ? tp : ControllerTypeProvider.UNINITIALIZED;
	}
	

	public void setTypeProvider(ControllerTypeProvider provider)
	{
		if (typeProvider_ != null)
			throw new IllegalStateException("type provider already set");
		typeProvider_ = provider;
	}
	
	
	/**
	 * Returns the signature string.
	 */
	@Override public String toString()
	{
		return signature_.toString();
	}

	
	private final ControllerSignature signature_; 
	private ControllerTypeProvider typeProvider_; 
}
