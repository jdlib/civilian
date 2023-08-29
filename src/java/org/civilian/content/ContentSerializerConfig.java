package org.civilian.content;


import java.util.Collections;
import java.util.Map;


public class ContentSerializerConfig 
{
	public static final ContentSerializerConfig EMPTY = new ContentSerializerConfig(null);
	
	
	public ContentSerializerConfig(Map<String,ContentSerializer> map)
	{
		map_ = map != null ? map : Map.of();
	}
	

	/**
	 * Returns a ContentSerializer for the content type.
	 * @return the ContentSerializer or null if no suitable serializer is available
	 * By default the application possesses ContentSerializers for text/plain and
	 * application/json (based on GSON).
	 */
	public ContentSerializer get(ContentType contentType)
	{
		return map_.get(contentType != null ? contentType.getValue() : null);
	}
	
	
	/**
	 * Returns a ContentSerializer for the content type.
	 * @return the ContentSerializer or null if no suitable serializer is available
	 */
	public ContentSerializer get(String contentType)
	{
		return map_.get(contentType);
	}

	
	/**
	 * Returns the serializers as Java Map.
	 */
	public Map<String,ContentSerializer> toMap()
	{
		return Collections.unmodifiableMap(map_);
	}

	
	private final Map<String,ContentSerializer> map_;
}
