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
 * Type represents a data type. In combination with interfaces
 * like TypeSerializer or TypeVisitor it allows to implement
 * various tasks (like parsing or formatting) using a double dispatch pattern.
 */
public interface Type<T>
{
	/**
	 * Returns true if the Type is a simple type (i.e. not an array or list type).
	 */
	public boolean isSimpleType();


	/**
	 * Returns a string representation of a value.
	 * @param serializer a TypeSerializer to format the value
	 * @param value the value
	 */
	public String format(TypeSerializer serializer, T value);
	

	/**
	 * Returns a string representation of a value.
	 * @param serializer a TypeSerializer to format the value
	 * @param value the value
	 * @param style an optional style hint to the serializer
	 */
	public String format(TypeSerializer serializer, T value, Object style);

	
	/**
	 * Returns a value parsed from its string representation.
	 * @param serializer a TypeSerializer to parse the string
	 * @param s the string
	 * @exception Exception if parsing fails
	 */
	public T parse(TypeSerializer serializer, String s) throws Exception;
 
	
	/**
	 * Accepts the visitor.
 	 */
	public <R,P,E extends Exception> R accept(TypeVisitor<R,P,E> visitor, P param) throws E;

	
	/**
	 * Returns the associated Java type.
	 */
	public Class<T> getJavaType();
	

	/**
	 * Returns the associated primitive Java type, or null if does not have one.
	 */
	public Class<T> getJavaPrimitiveType();
}
 