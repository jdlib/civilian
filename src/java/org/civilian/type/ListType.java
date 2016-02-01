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


/**
 * A type specialization for list types.
 * List types means arrays, java.util.Lists, etc.
 */
public abstract class ListType<T,E> extends Type<T>
{
	public ListType()
	{
		super(Category.LIST);
	}
	
	
	/**
	 * Returns the type of a list element.
	 */
	public abstract Type<E> getElementType();
	
	
	/**
	 * Parses the string array and returns a list of converted values.
	 */
	public abstract T parseList(TypeSerializer serializer, String... s) throws Exception;
}
