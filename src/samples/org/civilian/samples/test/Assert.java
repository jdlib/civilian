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
package org.civilian.samples.test;


import java.util.Objects;


/**
 * Just for the sake of the demonstration.
 * Use JUnit Assert or something else more complete instead.
 */
public abstract class Assert
{
	public static void assertTrue(boolean condition)
	{
		if (!condition)
			throw new IllegalStateException("condition failed");
	}


	public static void assertEquals(Object expected, Object actual)
	{
		if (!Objects.equals(expected, actual))
			throw new IllegalArgumentException("expected " + expected + ", but was " + actual); 
	}


	public static void assertEquals(int expected, int actual)
	{
		if (expected != actual)
			throw new IllegalArgumentException("expected " + expected + ", but was " + actual); 
	}


	public static void assertNotNull(Object object)
	{
		if (object == null)
			throw new IllegalArgumentException("expected not null object"); 
	}
}
