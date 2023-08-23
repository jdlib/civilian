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
package org.civilian.text.keys.serialize;


import java.lang.reflect.Type;
import org.civilian.text.keys.KeyList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


/**
 * A JsonSerializer for KeyLists. The serialized KeyList is an array of JavaScript
 * objects, each having properties "text" and "value". 
 */
public class KeyListSerializer implements JsonSerializer<KeyList<?>>
{
	/**
	 * Serializes the KeyList.
	 */
	@Override public JsonElement serialize(KeyList<?> keyList, Type typeOfSrc, JsonSerializationContext context) 
	{
	    JsonArray array = new JsonArray();
	    int n = keyList.size();
	    for (int i=0; i<n; i++)
	    {
	    	String text  		= keyList.getText(i);
	    	Object value 		= keyList.getValue(i);
	    	JsonObject element	= new JsonObject();
	    	element.addProperty("text", text);
	    	element.add("value", context.serialize(value));
	    	array.add(element);
	    }
	    return array;
	}
}
