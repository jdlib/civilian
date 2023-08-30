package org.civilian.response;


import org.civilian.content.ContentSerializerConfig;
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
	 * @return the default encoding for textual content of responses.
	 */
	public String getDefaultCharEncoding();
	
	
	/**
	 * @return a ContentSerializerMap.
	 */
	public ContentSerializerConfig getContentSerializers();


	/**
	 * @return a LocaleServiceList.
	 */
	public LocaleServiceList getLocaleServices();
	
	
	/**
	 * Returns an ResponseHandler which is used by the response
	 * to send an error to the client. Applications can 
	 * return a different implementation to tweak the error response.
	 * @param statusCode the status code
	 * @param message a message
	 * @param error an error 
	 * @see Response#sendError(int)  
	 * @see Response#sendError(int, String, Throwable)
	 * @return the handler  
	 */
	public ResponseHandler createErrorHandler(int statusCode, String message, Throwable error);
	
	
	/**
	 * @return the root resource of the owner (application).
	 */
	public Resource getRootResource();
	
	
	/**
	 * Returns the resource which is handled by the given ResourceHandler class.
	 * @param handlerClass the handler class
	 * @return the resource
	 */
	public Resource getResource(Class<? extends ResourceHandler> handlerClass);
}
