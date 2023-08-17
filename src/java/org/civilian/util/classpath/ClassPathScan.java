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
package org.civilian.util.classpath;


import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import org.civilian.util.Check;


public class ClassPathScan
{
	private static final String ECLIPSE_FILE_LOCATOR = "org.eclipse.core.runtime.FileLocator";

	
	public ClassPathScan(String... rootPackages)
	{
		this(Thread.currentThread().getContextClassLoader(), rootPackages);
	}

	
	public ClassPathScan(ClassLoader classLoader, String... rootPackages)
	{
		classLoader_ 	= Check.notNull(classLoader, "classLoader");
		rootPackages_	= Check.notEmpty(rootPackages, "rootPackages");
		detectEquinox();
	}

	
	public Set<String> collect() throws Exception
	{
		return collect((ClassFilter)null);
	}
	
	
	public Set<String> collect(ClassFilter filter) throws Exception
	{
		SimpleScanResult result = new SimpleScanResult(filter);
		run(result);
		return result.getClasses();
	}
	
	
	public <R extends ScanResult> R run(R result) throws Exception
	{
		for (String rootPackage : rootPackages_)
		{
			ScanContext context = new ScanContext(result, rootPackage);
			
			Enumeration<URL> rootUrls = classLoader_.getResources(context.rootPath);
			while (rootUrls.hasMoreElements())
				run(context, rootUrls.nextElement());
		}
		
		return result;
	}
	
	
	private void run(ScanContext context, URL rootUrl) throws Exception
	{
		if (rootUrl.getProtocol().startsWith("bundle"))
			rootUrl = resolveBundleUrl(rootUrl);
		
		for (Protocol protocol : PROTOCOLS)
		{
			if (protocol.accept(rootUrl))
			{
				protocol.scan(context, rootUrl);
				return;
			}
		}
		
		throw new IllegalArgumentException("no protocol implementation for URL " + rootUrl);
	}
	
	
	private URL resolveBundleUrl(URL url)
	{
		if (detectEquinox_)
		{
			detectEquinox_ = false;
			detectEquinox();
		}
		if (equinoxResolveMethod_ != null)
		{
			try 
			{
				url = (URL)equinoxResolveMethod_.invoke(null, url);
			}
			catch(RuntimeException e)
			{
				throw e;
			}
			catch (Exception e) 
			{
				throw new IllegalStateException(e);
			}
		}
		return url;
	}


	public static boolean equinoxDetected()
	{
		detectEquinox();
		return equinoxResolveMethod_ != null;
	}
	
	
	private static void detectEquinox()
	{
		if (equinoxResolveMethod_ == null)
		{
			try 
			{
				Class<?> fileLocatorClass = ClassPathScan.class.getClassLoader().loadClass(ECLIPSE_FILE_LOCATOR);
				equinoxResolveMethod_ = fileLocatorClass.getMethod("resolve", URL.class);
			}
			catch (Throwable e) 
			{
			}
		}
	}
	
	
	private ClassLoader classLoader_;
	private String[] rootPackages_;
	private boolean detectEquinox_ = true;
	private static Method equinoxResolveMethod_;
	private static Protocol[] PROTOCOLS = { ArchiveProtocol.INSTANCE, FileProtocol.INSTANCE, VfsProtocol.INSTANCE };  
}
