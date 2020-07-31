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
package org.civilian.internal;


import java.util.Iterator;
import org.civilian.Application;
import org.civilian.Server;
import org.civilian.resource.Path;
import org.civilian.util.Iterators;
import org.civilian.util.ResourceLoader;
import org.civilian.content.ContentTypeLookup;


/**
 * TempServer is used as Server instance by Application
 * until its actual server is set.
 * All methods do nothing, return empty defaults or throw an UnsupportedOperationException.  
 */
public class TempServer extends Server
{
	public static final TempServer INSTANCE = new TempServer();
	
	
	private UnsupportedOperationException error()
	{
		return new UnsupportedOperationException("Server method may not be called before Application#init was called");
	}
	
	
	@Override protected Object connect(Application app, boolean supportAsync)
	{
		throw error();
	}
	
	
	@Override protected void disconnect(Application app)
	{
		throw error();
	}
	
	
	@Override public String getServerVersion()
	{
		return "";
	}


	@Override public String getServerInfo()
	{
		return "";
	}


	@Override public Path getPath()
	{
		return Path.ROOT;
	}

	
	@Override public ResourceLoader getResourceLoader()
	{
		return ResourceLoader.builder.empty();
	}


	@Override public ContentTypeLookup getContentTypeLookup()
	{
		return ContentTypeLookup.DEFAULT;
	}

	
	@Override public Object getAttribute(String name)
	{
		return null;
	}


	@Override public Iterator<String> getAttributeNames()
	{
		return Iterators.<String>empty();
	}


	@Override public void setAttribute(String name, Object object)
	{
		throw error();
	}
	
	
	@Override public <T> T unwrap(Class<T> implClass)
	{
		return null;
	}


	@Override public void log(String msg)
	{
		throw error();
	}


	@Override public void log(String msg, Throwable throwable)
	{
		throw error();
	}


	@Override public String getRealPath(String path)
	{
		return null;
	}


	@Override public boolean isProhibitedPath(String path)
	{
		return true;
	}


	@Override public String getConfigPath(String name)
	{
		return null;
	}
}
