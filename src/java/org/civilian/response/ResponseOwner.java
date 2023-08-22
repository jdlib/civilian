package org.civilian.response;


import org.civilian.content.ContentSerializer;
import org.civilian.resource.PathProvider;
import org.civilian.resource.Resource;
import org.civilian.resource.ResourceHandler;
import org.civilian.text.service.LocaleServiceList;


/**
 * The owner of a Response, provides defaults.
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
	
	
	/**
	 * Returns an ResponseHandler which is used by the response
	 * to send an error to the client. Applications can 
	 * return a different implementation to tweak the error response. 
	 * @see Response#sendError(int)  
	 * @see Response#sendError(int, String, Throwable)  
	 */
	public ResponseHandler createErrorHandler(int statusCode, String message, Throwable error);
	
	
	/**
	 * Returns the root resource of the owner (application).
	 */
	public Resource getRootResource();
	
	
	/**
	 * Returns the resource which is handled by the given ResourceHandler class.
	 * @param handlerClass the handler class
	 * @return the resource
	 */
	public Resource getResource(Class<? extends ResourceHandler> handlerClass);
}
