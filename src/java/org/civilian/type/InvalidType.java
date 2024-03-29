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
 * A Type implementation which throws an UnsupportedOperationException
 * on all operations.
 */
public class InvalidType<T> extends Type<T>
{
	public static final InvalidType<Object> INSTANCE = new InvalidType<>();
	
	
	public InvalidType()
	{
		super(Category.SIMPLE);
	}
	
	/**
	 * @return a InvalidType object for a type.
	 * @param <S> the type 
	 */
	@SuppressWarnings("unchecked")
	public static <S> InvalidType<S> instance()
	{
		return (InvalidType<S>)INSTANCE;
	}
	
	
	@Override public boolean isSimpleType()
	{
		throw new UnsupportedOperationException();
	}
	

	@Override public Class<T> getJavaType()
	{
		throw new UnsupportedOperationException();
	}


	@Override public Class<T> getJavaPrimitiveType()
	{
		throw new UnsupportedOperationException();
	}
}
