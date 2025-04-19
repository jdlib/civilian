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


import org.junit.Test;
import org.civilian.CivTest;


public class DevRequestClassLoaderTest extends CivTest
{
	public static class Alpha
	{
	}
	
	
	public static class Beta
	{
	}
	
	
	public static class Gamma
	{
	}


	public static class Delta
	{
	}
	
	private static final String ALPHA_NAME = getInnerClassName("Alpha");
	private static final String BETA_NAME = getInnerClassName("Beta");
	private static final String GAMMA_NAME = getInnerClassName("Gamma");
	private static final String DELTA_NAME = getInnerClassName("Delta");
	
	
	private static String getInnerClassName(String inner)
	{
		return DevRequestClassLoaderTest.class.getName() + "$" + inner;
	}
	
	
	@Test public void testFilter() throws Exception
	{
		if (DevRequestClassLoader.isSupported())
		{
			ReloadConfig config = new ReloadConfig();
			config.includes().addPackage(getClass());
			config.excludes().add(BETA_NAME);
			
			DevRequestClassLoader cl = DevRequestClassLoader.of(getClass().getClassLoader(), config);
			
			// Alpha is included
			Class<?> alphaClass = cl.loadClass(ALPHA_NAME);
			assertSame(cl, alphaClass.getClassLoader()); 
	
			// Beta is excluded
			Class<?> betaClass = cl.loadClass(BETA_NAME);
			assertSame(getClass().getClassLoader(), betaClass.getClassLoader()); 
		}
	}

	
	@Test public void testDelegation() throws Exception
	{
		if (DevRequestClassLoader.isSupported())
		{
			ClassLoader parent = getClass().getClassLoader();
			DevRequestClassLoader cl = DevRequestClassLoader.of(parent, s -> true);
			// load alpha class by parent classloader
			// cl will detect this and not load by itself
			Class<?> gammaClass = parent.loadClass(GAMMA_NAME);
			assertSame(parent, gammaClass.getClassLoader()); 
			assertSame(gammaClass, cl.loadClass(GAMMA_NAME));
	
			Class<?> betaClass = cl.loadClass(DELTA_NAME);
			assertSame(cl, betaClass.getClassLoader()); 
		}
	}
}
