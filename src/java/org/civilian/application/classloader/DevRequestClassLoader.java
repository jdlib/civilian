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
package org.civilian.application.classloader;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.function.Predicate;
import org.civilian.Logs;
import org.civilian.util.Check;


/**
 * A ClassLoader which is used during development if the class reload is activated.
 * In this scenario for each request a new DevRequestClassLoader is created.
 * DevRequestClassLoader uses an eager, non-delegating class loader strategy.
 * Controller and dependent classes like templates will therefore be reloaded for every request -
 * perfect for rapid development where you change a class and hit refresh in the browser
 * to immediately see your changes without the need to restart the webserver.  
 */
public class DevRequestClassLoader extends ClassLoader
{
	private static final Method FINDELOADEDCLASS_METHOD;
	static
	{
		Method m;
		try
		{
			m = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
			m.setAccessible(true);
		}
		catch (Exception e)
		{
			m = null;
			Logs.CLASSLOADER.warn(e.getMessage(), e);
		}
		FINDELOADEDCLASS_METHOD = m;
	}
	
	
	public static boolean isSupported()
	{
		return FINDELOADEDCLASS_METHOD != null;
	}
	
	
	public static DevRequestClassLoader of(ClassLoader parent, Predicate<String> filter)
	{
		if (!isSupported())
			throw new UnsupportedOperationException();
		return new DevRequestClassLoader(parent, filter);
	}
	
	
	private DevRequestClassLoader(ClassLoader parent, Predicate<String> filter)
	{
		super(parent);
		filter_ = Check.notNull(filter, "filter");
	}


	@Override protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException 
	{
		Class<?> c;
		
        // check if the class has already been loaded
        c = findLoadedClass(name);
        if (c != null) {
            return c;
        }		
		
		// check whether the class is present in the bootstrap classpath
        try 
        {
            return BOOTSTRAP_CLASSLOADER.loadClass(name);
        } 
        catch (ClassNotFoundException e) 
        {
        	// ignored
        }		
		
		// check if loaded by a parent or by ourself
		c = findParentLoadedClass(name);
	    if (c != null)
	    	return c;
	    
	    boolean logTraceEnabled = Logs.CLASSLOADER.isTraceEnabled();
	    
	    // we see a new class
		if (logTraceEnabled)
			Logs.CLASSLOADER.trace("requested: " + name);
	    
		if (canLoad(name))
		{
			c = defineFileClass(name);
			if (c != null)
			{
				if (logTraceEnabled)
					Logs.CLASSLOADER.trace("loaded:    " + name);
				if (resolve)
					resolveClass(c);
				return c;
			}
		}
		
		if (logTraceEnabled)
			Logs.CLASSLOADER.trace("delegated: " + name);
	    return super.loadClass(name, resolve);
	}
	
	
	protected Class<?> findParentLoadedClass(String name)
	{
		try
		{
			return (Class<?>)FINDELOADEDCLASS_METHOD.invoke(getParent(), name);
		}
		catch (Exception e)
		{
			Logs.CLASSLOADER.error("error when calling 'findLoadedClass'", e);
			return null;
		}
	}
	
	
	/**
	 * We are only interested in loading classes which are files
	 * on the disk.
	 * @param name the class name
	 * @return the class
	 */
	protected Class<?> defineFileClass(String name)
	{
		String resource = name.replace('.', '/') + ".class";
		URL url = getParent().getResource(resource);
		if ((url != null) && "file".equals(url.getProtocol()))
		{
			try(InputStream in = url.openStream())
			{
				byte[] data = in.readAllBytes();
                return defineClass(name, data, 0, data.length);
			}
			catch(IOException e)
			{
				Logs.CLASSLOADER.error("error when loading class from '" + url + '\'', e);
			}
		}
		return null;
	}


	private boolean canLoad(String name)
	{
		// if the class is an inner class, we may not load it if
		// the outer class was loaded by the parent class loader
		// (even if the inner class was not yet loaded)
		int p = name.indexOf('$');
		if (p >= 0)
		{
			String outerClass = name.substring(0, p);
			Class<?> c = findLoadedClass(outerClass);
			if ((c != null) && (c.getClassLoader() != this))
				return false;
		}
		
		return isIncluded(name);
	}
	
	
	public boolean isIncluded(String name)
	{
		return filter_.test(name);
	}
	
	
	private final Predicate<String> filter_;
	private static final ClassLoader BOOTSTRAP_CLASSLOADER = findBootstrapClassLoader();
	private static ClassLoader findBootstrapClassLoader()
	{
		ClassLoader cl = DevRequestClassLoader.class.getClassLoader();
		while (cl != null) 
		{
			ClassLoader parent = cl.getParent();  
			if (parent == null)
				return cl;
			cl = parent;
		}
		throw new IllegalStateException("bootstrap classloader not found");
	}
}
