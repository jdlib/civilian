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
package org.civilian.text.keys;


import org.civilian.text.msg.MsgBundle;


/**
 * Provides factory methods to create key lists. 
 */
public class KeyLists
{
	private static final KeyList<String> EMPTY = forTexts();


	/**
	 * @param texts a list of text strings
	 * @return Creates a KeyList whose key values equal the key texts. 
	 */
	public static KeyList<String> forTexts(String... texts)
	{
		return new SimpleKeyList<>(KeySerializers.FOR_STRINGS, texts, texts);
	}
	

	/**
	 * @param values a list of values
	 * @param texts a list of text strings
	 * @param <T> the keys value type
	 * @return Creates a KeyList with the given values and texts. 
	 */
	public static <T> KeyList<T> forContent(T values[], String texts[])
	{
		return new SimpleKeyList<>(values, texts);
	}
	

	/**
	 * @param values a list of values
	 * @param texts a list of text strings
	 * @param serializer a serializer
	 * @param <T> the keys value type
	 * @return Creates a KeyList with the given values and texts,
	 * using a specific serializer.
	 */
	public static <T> KeyList<T> forContent(T values[], String texts[], KeySerializer serializer)
	{
		return new SimpleKeyList<>(serializer, values, texts);
	}


	/**
	 * @param values a list of values
	 * @param <T> the keys value type
	 * @return Creates a KeyList with the given values. The text associated with
	 * the value is value.toString()
	 */
	@SafeVarargs public static <T> KeyList<T> forValues(T... values)
	{
		return forValues(null, values);
	}
	
	
	/**
	 * @param serializer a serializer
	 * @param values a list of values
	 * @param <T> the keys value type
	 * @return Creates a KeyList with the given values and a specific KeySerializer. The text associated with
	 * the value is value.toString()
	 */
	@SafeVarargs public static <T> KeyList<T> forValues(KeySerializer serializer, T... values)
	{
		return new ValueKeyList<>(serializer, values);
	}
	

	/**
	 * @param <T> the keys value type
	 * @return an empty KeyList.
	 */
	@SuppressWarnings("unchecked")
	public static <T> KeyList<T> empty()
	{
		return (KeyList<T>)EMPTY;
	}

	
	/**
	 * @param enumClass a enum class
	 * @param <E> a enum type
	 * @return a KeyList whose values are the values of a Enum, and the texts
	 * are the enum names.
	 */
	public static <E extends Enum<E>> KeyList<E> forEnum(Class<E> enumClass)
	{
		return new EnumKeyList<>(enumClass);
	}
	
	
	/**
	 * @param keys a KeyList
	 * @param msgBundle a MsgBundle
	 * @param <T> the keys value type
	 * @return a KeyList whose values are the values of the given KeyLists.
	 * It treats the texts of the original keylist as ids of an MsgBundle
	 * and returns the translated text when asked for the text of a key. 
	 */
	public static <T> KeyList<T> translating(KeyList<T> keys, MsgBundle msgBundle)
	{
		return new TranslatingKeyList<>(keys, msgBundle);
	}
}
