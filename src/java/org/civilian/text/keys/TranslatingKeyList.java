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
 * TranslatingKeyList is a KeyList which wraps another KeyList.
 * Its values are the values of the original KeyList.
 * It treats the texts of the original keylist as ids of an MsgBundle
 * and returns the translated text when asked for the text of a key. 
 */
public class TranslatingKeyList<T> extends KeyList<T>
{
	/**
	 * Creates a new TranslatingKeyList.
	 * @param keys the wrapped KeyList
	 * @param msgBundle a MsgBundle
	 */
	public TranslatingKeyList(KeyList<T> keys, MsgBundle msgBundle)
	{
		super(keys.getType());
		keys_ 		= keys;
		msgBundle_ 	= msgBundle;
	}
	
	
	@Override public int size()
	{
		return keys_.size();
	}
	

	@Override public T getValue(int index)
	{
		return keys_.getValue(index);
	}
	

	@Override public String getText(int index)
	{
		return msgBundle_.msg(keys_.getText(index));
	}


	private final KeyList<T> keys_;
	private final MsgBundle msgBundle_;
}
