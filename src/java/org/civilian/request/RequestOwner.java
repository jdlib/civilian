package org.civilian.request;


import org.civilian.content.ContentSerializerConfig;
import org.civilian.resource.PathProvider;
import org.civilian.text.service.LocaleServiceList;


/**
 * The owner of a Request, provides defaults.
 */
public interface RequestOwner extends PathProvider 
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
}
