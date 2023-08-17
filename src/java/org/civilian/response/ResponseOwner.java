package org.civilian.response;


import org.civilian.content.ContentSerializer;
import org.civilian.resource.PathProvider;
import org.civilian.text.service.LocaleServiceList;

/**
 * The owner of the Response, provides defaults.
 */
public interface ResponseOwner extends PathProvider
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
	public ContentSerializer getContentSerializer(String contentType);


	/**
	 * Returns a LocaleServiceList.
	 */
	public LocaleServiceList getLocaleServices();
}
