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
package org.civilian.util;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;


/**
 * ResourceLoader provides a common interface for loading 
 * resources or ordinary files.
 */
public abstract class ResourceLoader
{
	/**
	 * Returns a resource loader which internally uses this
	 * ResourceLoader but throws an IllegalArgumentException 
	 * if the resource cannot be found.
	 * @return the required loader
	 */
	public ResourceLoader required()
	{
		return new RequiredResLoader(this);
	}
	
	
	/**
	 * Returns the URL of a resource with the specified name or null
	 * if the resource could not be found.
	 * @param name the resource name
	 * @return the URL
	 */
	public abstract URL getResourceUrl(String name);

	
	/**
	 * Returns an InputStream for a resource with the specified name or null
	 * if the resource could not be found.
	 * @param name the resource name
	 * @return the InputStream
	 */
	public abstract InputStream getResourceAsStream(String name); 

	
	/**
	 * Returns a Reader for a resource with the specified name or null
	 * if the resource could not be found.
	 * @param name the resource name
	 * @param charset the charset of the resource or null
	 * 		if the system charset should be used
	 * @return a Reader
	 */
	public Reader getResourceAsReader(String name, Charset charset)
	{
		InputStream in = getResourceAsStream(name);
		if (in == null)
			return null;
		else if (charset == null)
			return new InputStreamReader(in);
		else
			return new InputStreamReader(in, charset);
	}
}


class RequiredResLoader extends ResourceLoader
{
	public RequiredResLoader(ResourceLoader loader)
	{
		loader_ = loader;
	}
	

	@Override public URL getResourceUrl(String name)
	{
		return check(loader_.getResourceUrl(name), name);
	}

	
	@Override public InputStream getResourceAsStream(String name)
	{
		return check(loader_.getResourceAsStream(name), name);
	}


	@Override public Reader getResourceAsReader(String name, Charset charset)
	{
		return check(loader_.getResourceAsReader(name, charset), name);
	}
	

	private <T> T check(T object, String name)
	{
		if (object == null)
			throw new IllegalArgumentException("resource '" + name + "' not found");
		return object;
	}
	
	
	private final ResourceLoader loader_;
}
