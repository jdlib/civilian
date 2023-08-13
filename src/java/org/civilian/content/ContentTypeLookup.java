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


import java.net.URLConnection;
import java.util.Map;
import javax.servlet.ServletContext;
import org.civilian.util.Check;
import org.civilian.util.IoUtil;


/**
 * ContentTypeLookup is a service to translate
 * a file name or an extension into a content type. 
 */
public abstract class ContentTypeLookup
{
	/**
	 * An empty ContentTypeLookup.
	 */
	public static final ContentTypeLookup EMPTY = new ConstantContentTypeLookup(null);


	/**
	 * An ContentTypeLookup based on URLConnection#guessContentTypeFromName(String).
	 */
	public static final ContentTypeLookup DEFAULT = new UrlConContentTypeLookup();

	
	/**
	 * Creates a constant ContentTypeLookup which always returns the same content type.
	 */
	public static final ContentTypeLookup constant(ContentType contentType)
	{
		return new ConstantContentTypeLookup(contentType);
	}
	
	
	/**
	 * Creates a ContentTypeLookup from the map which maps extensions (without leading
	 * dots) to content types..
	 */
	public static final ContentTypeLookup forMap(Map<String,ContentType> ext2type)
	{
		return new MapContentTypeLookup(ext2type);
	}

	
	/**
	 * Creates a ContentTypeLookup which uses the content-type configuration
	 * of a ServletContext.
	 */
	public static final ContentTypeLookup forServletContext(ServletContext context)
	{
		return new ServletContentTypeLookup(context);
	}

	
	/**
	 * Returns the content type of the given file, or null if the content type is not known.
	 */
	public abstract ContentType forFile(String fileName);

	
	/**
	 * Returns the content type of the given file, 
	 * or the default type if the content type is not known.
	 */
	public ContentType forFile(String fileName, ContentType defaultType)
	{
		ContentType type = forFile(fileName);
		return type != null ? type : defaultType;
	}

	
	/**
	 * Returns the content type of the given file extension, or null if the content type is not known.
	 */
	public abstract ContentType forExtension(String extension);


	/**
	 * Returns the content type of the given file extension, or 
	 * the default type if the content type is not known.
	 */
	public ContentType forExtension(String extension, ContentType defaultType)
	{
		ContentType type = forExtension(extension);
		return type != null ? type : defaultType;
	}
}


/**
 * Returns a constant content type.
 */
class ConstantContentTypeLookup extends ContentTypeLookup
{
	public ConstantContentTypeLookup(ContentType contentType)
	{
		contentType_ = contentType;
	}
	
	
	@Override public ContentType forFile(String fileName)
	{
		return contentType_;
	}


	@Override public ContentType forExtension(String extension)
	{
		return contentType_;
	}
	
	
	private final ContentType contentType_;
}


/**
 * A ContentTypeLookup based on a map.
 */
class MapContentTypeLookup extends ContentTypeLookup
{
	public MapContentTypeLookup(Map<String,ContentType> ext2type)
	{
		ext2type_ = ext2type;
	}
	
	
	@Override public ContentType forFile(String fileName)
	{
		return forExtension(IoUtil.getExtension(fileName));
	}


	@Override public ContentType forExtension(String extension)
	{
		return ext2type_.get(IoUtil.normExtension(extension));
	}
	
	
	private final Map<String,ContentType> ext2type_;
}



/**
 * A ContentTypeLookup implementation based on a ServletContext. 
 */
class ServletContentTypeLookup extends ContentTypeLookup
{
	public ServletContentTypeLookup(ServletContext servletContext)
	{
		servletContext_ = Check.notNull(servletContext, "servletContext");
	}
	
	
	@Override public ContentType forFile(String fileName)
	{
		return ContentType.getContentType(servletContext_.getMimeType(fileName)); 
	}
	

	@Override public ContentType forExtension(String extension)
	{
		if (extension == null)
			return null;
		else
		{
			String file = (extension.startsWith(".") ? "x" : "x.") + extension;
			return forFile(file);
		}
	}


	private final ServletContext servletContext_;
}


/**
 * A ContentTypeLookup implementation based on a URLConnection. 
 */
class UrlConContentTypeLookup extends ContentTypeLookup
{
	@Override public ContentType forFile(String fileName)
	{
		String type = fileName != null ? URLConnection.guessContentTypeFromName(fileName) : null;
		return ContentType.getContentType(type); 
	}
	

	@Override public ContentType forExtension(String extension)
	{
		if (extension == null)
			return null;
		else
		{
			String file = (extension.startsWith(".") ? "x" : "x.") + extension;
			return forFile(file);
		}
	}
}
