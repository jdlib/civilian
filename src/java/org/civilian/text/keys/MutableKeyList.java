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


import java.util.ArrayList;

import org.civilian.util.Check;


/**
 * MutableKeyList is a mutable KeyList for a list of values.
 * It has an own serialization scheme for key values. Every new key
 * added to the list receives an own id, which is used as serialized value.
 */
public class MutableKeyList<VALUE> extends KeyList<VALUE>
{
	/**
	 * Creates a new MutableKeyList
	 */
	public MutableKeyList()
	{
		super(new Serializer());
	}
	
	
	@Override public int size()
	{
		return items_.size();
	}
	
	
	public void clear()
	{
		items_.clear();
	}
	

	@Override public VALUE getValue(int index)
	{
		return items_.get(index).value;
	}
	

	@Override public String getText(int index)
	{
		return items_.get(index).text;
	}

	
	public void setText(VALUE value, String text)
	{
		setText(indexOf(value), text);
	}
	
	
	public void setText(int index, String text)
	{
		Check.notNull(text, "text");
		items_.get(index).text = text;
	}

	
	public void add(VALUE value, String text)
	{
		Check.notNull(text, "text");
		items_.add(new Item<>(value, text, nextId_++));
	}
	
	
	public void remove(VALUE value)
	{
		remove(indexOf(value));
	}

	
	public VALUE remove(int index)
	{
		return items_.remove(index).value;
	}
	
	
	private static class Item<T>
	{
		public Item(T value, String text, int id)
		{
			this.value = value;
			this.text  = text;
			this.id    = id;
		}
		
		
		public final T value;
		public final int id;
		public String text;
	}
	
	
	private static class Serializer extends KeySerializer
	{
		private <VALUE> ArrayList<Item<VALUE>> getItems(KeyList<VALUE> keyList)
		{
			return ((MutableKeyList<VALUE>)keyList).items_; 
		}
		
		
		@Override public <VALUE> VALUE parseValue(KeyList<VALUE> keyList, String s) throws Exception
		{
			ArrayList<Item<VALUE>> items = getItems(keyList); 

			int id = Integer.parseInt(s);
			for (int i=items.size()-1; i>=0; i--)
			{
				if (items.get(i).id == id)
					return items.get(i).value;
			}
			throw rejectValue(s);
		}

		
		@Override public <VALUE> String formatValue(KeyList<VALUE> keyList, VALUE value, int index)
		{
			return String.valueOf(getItems(keyList).get(index).id);
		}

		
		@Override public <VALUE> String formatValue(KeyList<VALUE> keyList, VALUE value)
		{
			return formatValue(keyList, value, keyList.indexOf(value)); 
		}
	}
	

	private ArrayList<Item<VALUE>> items_ = new ArrayList<>();
	private int nextId_;
}
