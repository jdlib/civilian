package org.civilian.request;


import org.civilian.content.ContentSerializer;
import org.civilian.content.ContentType;
import org.civilian.resource.PathProvider;
import org.civilian.text.service.LocaleServiceList;


/**
 * The owner of a Request, provides defaults.
 */
public interface RequestOwner extends PathProvider 
{
	/**
	 * Returns the default encoding for textual content of responses.
	 */
	public String getDefaultCharEncoding();
	
	
	/**
	 * Returns a ContentSerializer for the content type.
	 * @return the ContentSerializer or null if no suitable serializer is available
	 * By default the application possesses ContentSerializers for text/plain and
	 * application/json (based on GSON).
	 */
	public ContentSerializer getContentSerializer(ContentType contentType);


	/**
	 * Returns a LocaleServiceList.
	 */
	public LocaleServiceList getLocaleServices();
}
