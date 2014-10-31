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
package org.civilian.resource;


import java.util.HashMap;
import java.util.Locale;
import org.civilian.application.AppConfig;
import org.civilian.content.ContentType;


/**
 * ExtensionMapping allows to map extension strings to content types
 * and locales. If ExtensionMappings are defined and a browser does
 * not send an Accept-Language or Accept header, then the extension
 * of the request path is examined, and preferred locale and content type
 * are derived from the extension. 
 * You can configure ExtensionMapping during application setup 
 * (see {@link AppConfig#getExtensionMapping()} 
 */
public class ExtensionMapping
{
	/**
	 * Maps an extension to a ContentType.
	 * @param extension an extension e.g. "html"
	 * @param contentType a content type e.g. ContentType.TEXT_HTML
	 */
	public void addContentType(String extension, ContentType contentType)
	{
		if (ext2contentType_ == null)
			ext2contentType_ = new HashMap<>();
		ext2contentType_.put(extension, contentType);
	}
	

	/**
	 * Returns the content type for an extension.
	 * @return the content type or null if not mapped.
	 */
	public ContentType getContentType(String extension)
	{
		return ext2contentType_ != null ? ext2contentType_.get(extension)  : null;
	}
	
	
	/**
	 * Returns the content type for an extension. In contrast to {@link #getContentType(String)},
	 * this methods examines if the extension is a sequence of extension parts (e.g. "html.fr").
	 * If yes then only the first part is used.
	 * For example if  and "html" is mapped to ContentType.TEXT_HTML
	 * then extractContentType("html.fr") would return this content type.
	 * @return the content type or null if not mapped.
	 */
	public ContentType extractContentType(String extension)
	{
		if (extension != null)
		{
			int p = extension.indexOf('.');
			if (p >= 0)
				extension = extension.substring(0, p);
			ContentType contentType = getContentType(extension);
			if (contentType != null)
				return contentType;
		}
		return null;
	}

	
	/**
	 * Maps an extension to a Locale.
	 * @param extension an extension e.g. "fr"
	 * @param locale a locale e.g. Locale.FRENCH
	 */
	public void addLocale(String extension, Locale locale)
	{
		if (ext2locale_ == null)
			ext2locale_ = new HashMap<>();
		ext2locale_.put(extension, locale);
	}
	

	/**
	 * Returns the locale for an extension.
	 * @return the locale or null if not mapped.
	 */
	public Locale getLocale(String extension)
	{
		return ext2locale_ != null ? ext2locale_.get(extension)  : null;
	}

	
	/**
	 * Returns the locale for an extension. In contrast to {@link #getLocale(String)},
	 * this methods examines if the extension is a sequence of extension parts (e.g. "html.fr").
	 * If yes then only the last part is used.
	 * For example if  and "fr" is mapped to Locale.FRENCH
	 * then extractLocale("html.fr") would return this Locale.
	 * @return the locale or null if not mapped.
	 */
	public Locale extractLocale(String extension)
	{
		Locale locale = null;
		if (extension != null)
		{
			int p = extension.lastIndexOf('.');
			if (p >= 0)
				extension = extension.substring(p+1);
			locale = getLocale(extension);
		}
		return locale;
	}
	
	
	private HashMap<String,ContentType> ext2contentType_;
	private HashMap<String,Locale> ext2locale_;
}
