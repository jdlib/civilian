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
package org.civilian.content;


import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.civilian.text.keys.KeyList;
import org.civilian.text.keys.KeyListSerializer;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;


/**
 * A ContentSerializer for JSON data based on GSON.
 */
public class GsonJsonSerializer extends ContentSerializer
{
	/**
	 * Creates a new JsonSerializer.
	 */
	public GsonJsonSerializer()
	{
		this(new GsonBuilder());
	}
	
	
	/**
	 * Creates a new JsonSerializer. The gson object of the serializer
	 * is obtained from the builder
	 */
	public GsonJsonSerializer(GsonBuilder builder)
	{
		Check.notNull(builder, "builder");
		builder.registerTypeHierarchyAdapter(KeyList.class, new KeyListSerializer());
		gson_ = builder.create();
	}

	
	/**
	 * Creates a new JsonSerializer based on the gson object.

	 */
	public GsonJsonSerializer(Gson gson)
	{
		gson_ = Check.notNull(gson, "gson");
	}
	
	
	/**
	 * Uses the JsonService of the application to parse the content.    
	 */
	@SuppressWarnings("unchecked")
	@Override public <T> T read(Class<T> type, Type genericType, Reader reader) throws Exception
	{
		Check.notNull(type, "type");
		Check.notNull(reader, "reader");

		if (type == JsonElement.class)
		{
			JsonParser parser = new JsonParser();
			return (T)parser.parse(reader);
		}
		else if (genericType != null)
			return gson_.fromJson(reader, genericType);
		else
			return gson_.fromJson(reader, type);
	}


	/**
	 * Writes the content of the response. 
	 */
	@Override public void write(Object object, Writer writer) throws Exception
	{
		if (object instanceof JsonElement)
			gson_.toJson((JsonElement)object, writer);
		else
			gson_.toJson(object, writer);
	}
	
	
	/**
	 * Returns the gson object if implClass equals Gson.class.
	 */
	@Override public <T> T unwrap(Class<T> implClass)
	{
		return ClassUtil.unwrap(gson_, implClass);
	}

	
	/**
	 * Returns the exception message if the exception is a JsonSyntaxException
	 * or a JsonParseException, else returns null.
	 */
	@Override public String describeReadError(Exception e)
	{
		return (e instanceof JsonSyntaxException) || (e instanceof JsonParseException) ?
			e.getMessage() : 
			null;
	}

	
	private final Gson gson_;
}
