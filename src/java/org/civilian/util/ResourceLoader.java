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


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import javax.servlet.ServletContext;


/**
 * ResourceLoader provides a common interface for loading 
 * resources or ordinary files.
 */
public abstract class ResourceLoader
{
	/**
	 * Holds a Builder object to construct resource loaders.
	 */
	public static final Builder builder = new Builder();
	
	
	/**
	 * Allows to construct resource loaders.
	 */
	public static class Builder
	{
		/**
		 * Creates a resource loader who uses
		 * ClassLoader.getResource to load resources.
		 */
		public ResourceLoader forClassLoader(ClassLoader classLoader)
		{
			return new ClassLoaderResLoader(classLoader);
		}
		
	
		/**
		 * Creates a resource loader who uses
		 * the system ClassLoader to load resources.
		 */
		public ResourceLoader forSystemClassLoader()
		{
			return forClassLoader(ClassLoader.getSystemClassLoader());
		}

		
		/**
		 * Creates a resource loader who uses
		 * the ClassLoader of this class to load resources.
		 */
		public ResourceLoader forClassLoader()
		{
			return forClassLoader(getClass().getClassLoader());
		}

		
		/**
		 * Creates a resource loader who uses
		 * Class.getResource to load resources.
		 */
		public ResourceLoader forClass(Class<?> c)
		{
			return new ClassResLoader(c);
		}
	
		
		/**
		 * Creates a resource loader who uses
		 * ServletContext.getResource to load resources.
		 */
		public ResourceLoader forSerlvetContext(ServletContext context)
		{
			return new ServletContextResLoader(context);
		}
	
		
		/**
		 * Creates a resource loader who
		 * loads resources from the current directory.
		 */
		public ResourceLoader forCurrentDirectory()
		{
			return forDirectory(new File("."));
		}
		
		
		/**
		 * Creates a resource loader who
		 * loads resources from the given directory.
		 */
		public ResourceLoader forDirectory(File file)
		{
			File directory = file.isDirectory() ? file : file.getParentFile();
			return new DirectoryResLoader(directory);
		}


		/**
		 * Creates a resource loader who
		 * loads resources from the given directory.
		 */
		public ResourceLoader forFile(File file)
		{
			return new FileResLoader(file);
		}

		
		/**
		 * Creates a resource loader who
		 * returns the string content if the resource name
		 * matches the specified name.
		 */
		public ResourceLoader forString(String name, String content)
		{
			return new StringResLoader(name, content);
		}

	
		/**
		 * Creates a chain of resource loaders. If a loader
		 * cannot load a resource, the next loader in the chain
		 * is asked.
		 */
		public ResourceLoader chain(ResourceLoader... loaders)
		{
			return new ChainedLoader(loaders);
		}

	
		/**
		 * Returns a ResourceLoader which constantly returns null.
		 */
		public ResourceLoader empty()
		{
			return new EmptyResLoader();
		}
	}

	
	/**
	 * Returns a resource loader which internally uses this
	 * ResourceLoader but throws an IllegalArgumentException 
	 * if the resource cannot be found.
	 */
	public ResourceLoader required()
	{
		return new RequiredResLoader(this);
	}
	
	
	/**
	 * Returns the URL of a resource with the specified name or null
	 * if the resource could not be found.
	 * @param name the resource name
	 */
	public abstract URL getResourceUrl(String name);
	
	
	/**
	 * Returns URL of all resources with the specified name
	 */
	public abstract Enumeration<URL> getResourceUrls(String name) throws IOException;

	
	/**
	 * Returns an InputStream for a resource with the specified name or null
	 * if the resource could not be found.
	 * @param name the resource name
	 */
	public abstract InputStream getResourceAsStream(String name); 

	
	/**
	 * Returns a Reader for a resource with the specified name or null
	 * if the resource could not be found.
	 * @param name the resource name
	 * @param charset the encoding of the resource or null
	 * 		if the system encoding should be used
	 */
	public Reader getResourceAsReader(String name, String charset)
		throws UnsupportedEncodingException
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


/**
 * A ResourceLoader based on a ClassLoader. 
 */
class ClassLoaderResLoader extends ResourceLoader
{
	public ClassLoaderResLoader(ClassLoader classLoader)
	{
		classLoader_ = Check.notNull(classLoader, "classLoader");
	}
	
	
	@Override public URL getResourceUrl(String name)
	{
		return classLoader_.getResource(name);
	}


	@Override public Enumeration<URL> getResourceUrls(String name) throws IOException
	{
		return classLoader_.getResources(name);
	}
	
	
	@Override public InputStream getResourceAsStream(String name)
	{
		return classLoader_.getResourceAsStream(name);
	}
	
	
	private ClassLoader classLoader_;
}	


/**
 * A ResourceLoader based on a Class. 
 */
class ClassResLoader extends ResourceLoader
{
	public ClassResLoader(Class<?> c)
	{
		class_ = Check.notNull(c, "class");
	}
	
	
	@Override public URL getResourceUrl(String name)
	{
		return class_.getResource(name);
	}
	
	
	@Override public Enumeration<URL> getResourceUrls(String name) throws IOException
	{
		return Iterators.asEnumeration(Iterators.forValue(getResourceUrl(name)));
	}
	
	
	@Override public InputStream getResourceAsStream(String name)
	{
		return class_.getResourceAsStream(name);
	}
	
	
	private Class<?> class_;
}	


/**
 * A ResourceLoader based on a ServletContext. 
 */
class ServletContextResLoader extends ResourceLoader
{
	public ServletContextResLoader(ServletContext servletContext)
	{
		servletContext_ = Check.notNull(servletContext, "servletContext");
	}
	
	
	@Override public URL getResourceUrl(String name)
	{
		try
		{
			return servletContext_.getResource(name);
		} 
		catch (MalformedURLException e)
		{
			throw new IllegalStateException("cannot create resource URL for '" + name + "'", e);
		}
	}
	
	
	@Override public Enumeration<URL> getResourceUrls(String name) throws IOException
	{
		final Iterator<String> paths = servletContext_.getResourcePaths(name).iterator();
		return new Enumeration<URL>()
		{
			@Override public boolean hasMoreElements()
			{
				return paths.hasNext();
			}

			@Override public URL nextElement()
			{
				return getResourceUrl(paths.next());
			}
			
		};
	}
	
	
	@Override public InputStream getResourceAsStream(String name)
	{
		return servletContext_.getResourceAsStream(name);
	}
	
	
	private ServletContext servletContext_;
}	


/**
 * A ResourceLoader based on a directory. 
 */
class DirectoryResLoader extends ResourceLoader
{
	public DirectoryResLoader(File directory)
	{
		directory_ = Check.notNull(directory, "directory");
	}
	
	
	@Override public URL getResourceUrl(String name)
	{
		try
		{
			File file = getResourceFile(name);
			return file == null ? null : file.toURI().toURL();
		} 
		catch (MalformedURLException e)
		{
			throw new IllegalStateException("cannot create resource URL for '" + name + "'", e);
		}
	}
	
	
	@Override public Enumeration<URL> getResourceUrls(String name) throws IOException
	{
		URL url = getResourceUrl(name);
		return Iterators.asEnumeration(Iterators.forValue(url));
	}
	
	
	@Override public InputStream getResourceAsStream(String name)
	{
		try
		{
			File file = getResourceFile(name);
			if (file != null)
				return new FileInputStream(file);
		}
		catch (FileNotFoundException e)
		{
			// ignore silently
		}
		return null;
	}
	
	
	private File getResourceFile(String name)
	{
		File file = new File(directory_, name);
		return file.exists() ? file : null;
	}
	
	
	private File directory_;
}	


/**
 * A ResourceLoader based on a single file. 
 */
class FileResLoader extends ResourceLoader
{
	public FileResLoader(File file)
	{
		file_ = Check.notNull(file, "file");
	}
	
	
	@Override public URL getResourceUrl(String name)
	{
		try
		{
			File file = getResourceFile(name);
			return file == null ? null : file.toURI().toURL();
		} 
		catch (MalformedURLException e)
		{
			throw new IllegalStateException("cannot create resource URL for '" + name + "'", e);
		}
	}
	
	
	@Override public Enumeration<URL> getResourceUrls(String name) throws IOException
	{
		URL url = getResourceUrl(name);
		return Iterators.asEnumeration(Iterators.forValue(url));
	}
	
	
	@Override public InputStream getResourceAsStream(String name)
	{
		try
		{
			File file = getResourceFile(name);
			if (file != null)
				return new FileInputStream(file);
		}
		catch (FileNotFoundException e)
		{
			// ignore silently
		}
		return null;
	}
	
	
	private File getResourceFile(String name)
	{
		return file_.exists() && file_.getName().equals(name) ? file_ : null;
	}
	
	
	private File file_;
}	


class ChainedLoader extends ResourceLoader
{
	public ChainedLoader(ResourceLoader[] loaders)
	{
		if (loaders == null)
			throw new IllegalArgumentException("loaders null");
		loaders_ = loaders;
	}
	
	
	@Override public URL getResourceUrl(String name)
	{
		for (int i=0; i<loaders_.length; i++)
		{
			URL url = loaders_[i].getResourceUrl(name);
			if (url != null)
				return url;
		}
		return null;
	}
	

	@Override public Enumeration<URL> getResourceUrls(String name) throws IOException
	{
		ArrayList<URL> urls = new ArrayList<>();
		for (int i=0; i<loaders_.length; i++)
			Iterators.addAll(urls, loaders_[i].getResourceUrls(name));
		return Iterators.asEnumeration(urls.iterator());
	}
	
	
	@Override public InputStream getResourceAsStream(String name)
	{
		for (int i=0; i<loaders_.length; i++)
		{
			InputStream in = loaders_[i].getResourceAsStream(name);
			if (in != null)
				return in;
		}
		return null;
	}
	
	
	private ResourceLoader[] loaders_;
}


class StringResLoader extends ResourceLoader
{
	public StringResLoader(String name, String content)
	{
		name_ = name;
		content_ = content;
	}

	@Override public URL getResourceUrl(String name)
	{
		throw new UnsupportedOperationException();
	}
	

	@Override public Enumeration<URL> getResourceUrls(String name) throws IOException
	{
		throw new UnsupportedOperationException();
	}
	
	
	@Override public InputStream getResourceAsStream(String name)
	{
		return name_.equals(name) ? new ByteArrayInputStream(content_.getBytes()) : null;
	}
	

	@Override public Reader getResourceAsReader(String name, String charset)
	{
		return name_.equals(name) ? new StringReader(content_) : null;
	}
	
	
	private String name_;
	private String content_;
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

	
	@Override public Enumeration<URL> getResourceUrls(String name) throws IOException
	{
		return check(loader_.getResourceUrls(name), name);
	}

	
	@Override public InputStream getResourceAsStream(String name)
	{
		return check(loader_.getResourceAsStream(name), name);
	}


	@Override public Reader getResourceAsReader(String name, String charset)
		throws UnsupportedEncodingException
	{
		return check(loader_.getResourceAsReader(name, charset), name);
	}
	

	private <T> T check(T object, String name)
	{
		if (object == null)
			throw new IllegalArgumentException("resource '" + name + "' not found");
		return object;
	}
	
	
	private ResourceLoader loader_;
}


class EmptyResLoader extends ResourceLoader
{
	@Override public URL getResourceUrl(String name)
	{
		return null;
	}

	
	@Override public Enumeration<URL> getResourceUrls(String name) throws IOException
	{
		return Iterators.asEnumeration(Iterators.<URL>empty());
	}

	
	@Override public InputStream getResourceAsStream(String name)
	{
		return null;
	}
}