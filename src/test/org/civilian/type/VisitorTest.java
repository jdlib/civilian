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
package org.civilian.type;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.type.lib.ArrayType;


public class VisitorTest extends CivTest implements InvocationHandler
{
	@SuppressWarnings("unchecked")
	@Test public void test() throws Exception
	{
		visitor_ = (TypeVisitor<Type<?>, Type<?>,Exception>)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { TypeVisitor.class }, this);
		
		assertVisit(TypeLib.BIGDECIMAL);
		assertVisit(TypeLib.BIGINTEGER);
		assertVisit(TypeLib.BOOLEAN);
		assertVisit(TypeLib.BYTE);
		assertVisit(TypeLib.CHARACTER);
		assertVisit(TypeLib.DOUBLE);
		assertVisit(TypeLib.FLOAT);
		assertVisit(TypeLib.INTEGER);
		assertVisit(TypeLib.LONG);
		assertVisit(TypeLib.SHORT);
		assertVisit(TypeLib.STRING);
		assertVisit(TypeLib.DATE_CALENDAR, "Date");
		assertVisit(TypeLib.DATE_CIVILIAN, "Date");
		assertVisit(TypeLib.DATE_JAVA_SQL, "Date");
		assertVisit(TypeLib.DATE_JAVA_UTIL, "Date");
		assertVisit(new ArrayType<>(TypeLib.STRING), "Array");
	}
	

	private void assertVisit(Type<?> type) throws Exception
	{
		assertVisit(type, type.getJavaType().getSimpleName());
	}

	
	private void assertVisit(Type<?> type, String methodSuffix) throws Exception
	{
		current_ = type;
		methodSuffix_ = methodSuffix;
		assertSame(type, type.accept(visitor_, type));
	}
	

	
	@Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		assertSame(current_, args[0]);
		assertEquals("visit" + methodSuffix_, method.getName());
		return args[0];
	}
	
	
	private Type<?> current_;
	private String methodSuffix_;
	private TypeVisitor<Type<?>, Type<?>,Exception> visitor_;
}
