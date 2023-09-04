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


/**
 * A {@link KeyList} implementation for enums.
 * The values of the KeyList are the enum entries, the text
 * is the enum name.
 * @param <E> a enum type 
 */
public class EnumKeyList<E extends Enum<E>> extends KeyList<E>
{
	/**
	 * Creates a new EnumKeyList.
	 * @param enumClass the enum class
	 */
	public EnumKeyList(Class<E> enumClass)
	{
		super(KeySerializers.forEnum(enumClass));
		values_ = enumClass.getEnumConstants();
	}
	
	
	/**
	 * @return the number of enum entries.
	 */
	@Override public int size()
	{
		return values_.length;
	}
	

	/**
	 * @param index an index
	 * @return the i-th enum.
	 */
	@Override public E getValue(int index)
	{
		return values_[index];
	}
	

	/**
	 * @param index an index
	 * @return the name of the i-th enum.
	 */
	@Override public String getText(int index)
	{
		return values_[index].name();
	}

	
	private final E[] values_;
}
