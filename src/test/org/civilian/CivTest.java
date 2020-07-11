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
package org.civilian;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import org.civilian.type.TypeLib;
import org.civilian.util.IoUtil;
import org.junit.Assert;


/**
 * Base class for Civilian tests.
 */
public class CivTest extends Assert
{
	public static <T> void assertEquals(boolean expected, boolean actual)
	{
		if (expected != actual)
			fail("expected " + expected + " but was " + actual);
	}
	
	 
	public static <T> void assertArrayEquals2(T[] actual, @SuppressWarnings("unchecked") T... expected)
	{
		if (expected == null)
		{
			if ((actual != null) && (actual.length > 0))
				fail();
			return;
		}
		assertArrayEquals(expected, actual);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> void assertColEquals(Object actual, T... expected)
	{
		assertTrue(actual instanceof Collection);
		assertIterator(((Collection)actual).iterator(), expected);
	}

	
	@SafeVarargs
	public static <T> void assertIterator(Iterator<T> it, T... expectedValues)
	{
		for (int i=0; i<expectedValues.length; i++)
		{
			assertTrue(it.hasNext());
			assertEquals(expectedValues[i], it.next());
		}
		assertFalse(it.hasNext());
	}

	
	protected Method findMethod(Class<?> c, String name)
	{
		for (Method method : c.getDeclaredMethods())
		{
			if (method.getName().equals(name))
				return method;
		}
		fail("cannot find method '" + name + "'");
		return null;
	}


	protected File createTempDir() throws IOException
	{
	    File temp = File.createTempFile("temp", "dir");
	    if (!temp.delete())
	        throw new IOException("Could not delete temp file " + temp.getAbsolutePath());

	    if (!temp.mkdir())
	        throw new IOException("Could not create temp directory " + temp.getAbsolutePath());

	    return temp;	
	}
	
	
	protected File findTestFile(String fileName) throws URISyntaxException
	{
		URL url = getClass().getResource(fileName);
		if (url == null)
			fail(fileName + " not found");
		return new File(url.toURI());
	}


	protected String readTestFile(String fileName) throws Exception
	{
		return read(findTestFile(fileName));
	}
	
	
	protected String read(File file) throws Exception
	{
		return read(file, "UTF-8");
	}
	
	
	protected String read(File file, String encoding) throws Exception
	{
		try(InputStreamReader in = new InputStreamReader(new FileInputStream(file), encoding))
		{
			return IoUtil.readString(in);
		}
	}
	

	protected File createTempFile(String extension, String encoding, String content) throws IOException
	{
		File file = File.createTempFile("test", extension);
		write(file, encoding, content);
		return file;
	}


	protected void write(File file, String encoding, String content) throws IOException
	{
		try(OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), encoding))
		{
			out.write(content);
		}
	}
	
	
	protected String normLineBreak(String s)
	{
		return s.replace("\r", "");
	}
	

	public Object[] getDummyMethodParams(Method method)
	{
		Class<?>[] pTypes = method.getParameterTypes();
		if ((pTypes == null) || (pTypes.length == 0))
			return null;
		else
		{
			Object[] params = new Object[pTypes.length];
			for (int i=0; i<params.length; i++)
				params[i] = TypeLib.getPrimitiveDefaultValue(pTypes[i]);
			return params;
		}
	}
	
	
	public void compareFiles(String expected, String actual)
	{
		expected = normLinebreak(expected);
		actual   = normLinebreak(actual);
		assertEquals(expected, actual);
	}

	
	public String normLinebreak(String s)
	{
		return s != null ? s.replace("\r", "") : null;
	}
	
	public static class SysOut extends PrintStream
	{
		public SysOut()
		{
			super(new ByteArrayOutputStream());
			old = System.out;
			System.setOut(this);
		}
		
		
		public ByteArrayOutputStream out()
		{
			flush();
			return (ByteArrayOutputStream)out;
		}
		
		
		@Override public void close()
		{
			System.setOut(old);
			super.close();
		}
		
		
		public void reset()
		{
			out().reset();
		}
		

		@Override public String toString()
		{
			return out().toString();
		}

		
		private PrintStream old;
	}
	
	
	public class WrapperProxy<T> implements InvocationHandler
	{
		@SuppressWarnings("unchecked")
		public WrapperProxy(Class<T> c)
		{
			class_ = c;
			proxy_ = (T)Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { c }, this); 
		}


		public void testForwardInvocation(T wrapper) throws Exception
		{
			for (Method method : class_.getDeclaredMethods())
			{
				if (Modifier.isAbstract(method.getModifiers()))
				{
					invokedMethod_ = null;
					method.invoke(wrapper, getDummyMethodParams(method));
					assertEquals(method.getName(), method, invokedMethod_);
				}	
			}
		}
		
		
		@Override public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable
		{
			invokedMethod_ = method;
			return TypeLib.getPrimitiveDefaultValue(method.getReturnType()); 
		}

		
		public T getProxy()
		{
			return proxy_;
		}
		
		
		private Method invokedMethod_;
		private T proxy_;
		private Class<T> class_;
	}
	

	protected static TypeLib TYPELIB = new TypeLib();
}
