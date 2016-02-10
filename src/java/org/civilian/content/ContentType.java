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


import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import org.civilian.annotation.Consumes;
import org.civilian.annotation.Produces;
import org.civilian.util.Check;


/**
 * ContentType represents the 
 * <a href="https://en.wikipedia.org/wiki/Internet_media_type">https://en.wikipedia.org/wiki/Internet_media_type</a> 
 * of request or response content. 
 */
public class ContentType
{
	private static final HashMap<String,ContentType> BUILT_INS = new HashMap<>(); 

	
	/**
	 * The Types interface defines constants for the main part of important content types.
	 */
	public static interface Types
	{
		public static final String APPLICATION 	= "application";
		public static final String AUDIO		= "audio";
		public static final String IMAGE		= "image";
		public static final String TEXT			= "text";
		public static final String VIDEO		= "video";
	}

	
	/**
	 * The Strings interface defines constants for often used ContentType strings.
	 * You can use these in {@link Produces} or {@link Consumes} annotations.
	 */
	public static interface Strings
	{
		public static final String ANY 						= "*/*";
		public static final String APPLICATION_EXCEL		= Types.APPLICATION + '/' + "vnd.ms-excel";
		public static final String APPLICATION_OCTET_STREAM = Types.APPLICATION + '/' + "octet-stream";
		public static final String APPLICATION_JAVASCRIPT	= Types.APPLICATION + '/' + "javascript";
		public static final String APPLICATION_JSON			= Types.APPLICATION + '/' + "json";
		public static final String APPLICATION_PDF			= Types.APPLICATION + '/' + "pdf";
		public static final String APPLICATION_XML			= Types.APPLICATION + '/' + "xml";
		public static final String APPLICATION_X_MPEG		= Types.APPLICATION + '/' + "x-mpeg";
		public static final String APPLICATION_X_FLASH		= Types.APPLICATION + '/' + "x-flash";
		public static final String APPLICATION_X_WWW_FORM_URLENCODED 
															= Types.APPLICATION + '/' + "x-www-form-urlencoded";
		public static final String TEXT_CSS 				= Types.TEXT 		+ '/' + "css";
		public static final String TEXT_HTML 				= Types.TEXT 		+ '/' + "html";
		public static final String TEXT_PLAIN 				= Types.TEXT 		+ '/' + "plain";
		public static final String TEXT_XML					= Types.TEXT 		+ '/' + "xml";
		public static final String TEXT_X_TEMPLATE			= Types.TEXT 		+ '/' + "x-template";
		public static final String TEXT_X_VCARD				= Types.TEXT 		+ '/' + "x-vcard";
		public static final String IMAGE_GIF 				= Types.IMAGE 		+ '/' + "gif";
		public static final String IMAGE_PNG 				= Types.IMAGE 		+ '/' + "png";
		public static final String IMAGE_JPEG 				= Types.IMAGE 		+ '/' + "jpeg";
		public static final String VIDEO_X_FLV 				= Types.VIDEO 		+ '/' + "x-flv";
	}
	
	
	/**
	 * ContentType("&#42;/&#42;") 
	 */
	public static final ContentType ANY = builtin(Strings.ANY);

	/**
	 * ContentType("application/vnd.ms-excel") 
	 */
	public static final ContentType APPLICATION_EXCEL = builtin(Strings.APPLICATION_EXCEL);

	/**
	 * ContentType("application/javascript") 
	 */
	public static final ContentType APPLICATION_JAVASCRIPT = builtin(Strings.APPLICATION_JAVASCRIPT);

	/**
	 * ContentType("application/json") 
	 */
	public static final ContentType APPLICATION_JSON = builtin(Strings.APPLICATION_JSON);

	/**
	 * ContentType("application/octet-stream") 
	 */
	public static final ContentType APPLICATION_OCTET_STREAM = builtin(Strings.APPLICATION_OCTET_STREAM);

	/**
	 * ContentType("application/pdf") 
	 */
	public static final ContentType APPLICATION_PDF = builtin(Strings.APPLICATION_PDF);

	/**
	 * ContentType("application/xml") 
	 */
	public static final ContentType APPLICATION_XML = builtin(Strings.APPLICATION_XML);

	/**
	 * ContentType("application/x-mpeg") 
	 */
	public static final ContentType APPLICATION_X_MPEG = builtin(Strings.APPLICATION_X_MPEG);

	/**
	 * ContentType("application/x-flash") 
	 */
	public static final ContentType APPLICATION_X_FLASH = builtin(Strings.APPLICATION_X_FLASH);

	/**
	 * ContentType("application/x-www-form-urlencoded") 
	 */
	public static final ContentType APPLICATION_X_WWW_FORM_URLENCODED = builtin(Strings.APPLICATION_X_WWW_FORM_URLENCODED);

	/**
	 * ContentType("text/css") 
	 */
	public static final ContentType TEXT_CSS = builtin(Strings.TEXT_CSS);

	/**
	 * ContentType("text/html") 
	 */
	public static final ContentType TEXT_HTML = builtin(Strings.TEXT_HTML);

	/**
	 * ContentType("text/plain") 
	 */
	public static final ContentType TEXT_PLAIN = builtin(Strings.TEXT_PLAIN);

	/**
	 * ContentType("text/xml") 
	 */
	public static final ContentType TEXT_XML = builtin(Strings.TEXT_XML);

	/**
	 * ContentType("text/x-template") 
	 */
	public static final ContentType TEXT_X_TEMPLATE = builtin(Strings.TEXT_X_TEMPLATE);

	/**
	 * ContentType("text/x-vcard") 
	 */
	public static final ContentType TEXT_X_VCARD = builtin(Strings.TEXT_X_VCARD);

	/**
	 * ContentType("image/gif") 
	 */
	public static final ContentType IMAGE_GIF = builtin(Strings.IMAGE_GIF);

	/**
	 * ContentType("image/png") 
	 */
	public static final ContentType IMAGE_PNG = builtin(Strings.IMAGE_PNG);

	/**
	 * ContentType("image/jpeg") 
	 */
	public static final ContentType IMAGE_JPEG = builtin(Strings.IMAGE_JPEG);

	/**
	 * ContentType("video/x-flv") 
	 */
	public static final ContentType VIDEO_X_FLV = builtin(Strings.VIDEO_X_FLV);
	
	
	/**
	 * Compare defines ContentType comparators.
	 */
	public static interface Compare
	{
		/**
		 * A comparator to sort ContentTypes by their quality, highest coming first.
		 */
		public static final Comparator<ContentType> BY_QUALITY = new QualityComparator();
		
		
		/**
		 * A comparator to sort ContentTypes by how specific they are
		 * "main/sub" &lt; "main/&#42;" &lt; "&#42;/sub" &lt;  "&#42;/&#42;"
		 */
		public static final Comparator<ContentType> BY_SPECIFICITY = new SpecificityComparator();
	}
	
	
	/**
	 * Registers a builtin content type. 
	 */
	private static final ContentType builtin(String s)
	{
		ContentType type = new ContentType(s, true);
		BUILT_INS.put(s, type);
		return type;
	}
	
	
	/**
	 * Returns a ContentType for the given string.
	 * If the string is null, then null is returned.
	 * If it is one of the predefined content types, the corresponding constant
	 * is returned, else a new content-type object is created.
	 */
	public static ContentType getContentType(String s)
	{
		ContentType contentType = null;
		if (s != null)
		{
			contentType = BUILT_INS.get(s);
			if (contentType == null)
				contentType = new ContentType(s);
		}
		return contentType;
	}
	

	/**
	 * Creates a ContentType for the given string representation.
	 * @param value a string containing a slash, separating the type and subtype. 
	 * 		The wildcard '*' is allowed for both type and subtype.
	 */
	public ContentType(String value)
	{
		this(value, false);
	}
	
	
	private ContentType(String value, boolean reuseValue)
	{
		Check.notNull(value, "value");
		
		int p = value.indexOf('/');
		if (p == -1)
			throw new IllegalArgumentException("not a content type: " + value);
		init(value.substring(0, p), value.substring(p + 1), reuseValue ? value : null);
	}
	
	
	/**
	 * Creates a ContentType for the given main part and sub part.
	 * '*' or null values are allowed and interpreted as wildcard.
	 */
	public ContentType(String mainPart, String subPart)
	{
		init(mainPart, subPart, null);
	}
	
	
	/**
	 * Creates a ContentType for the given main part, sub part and quality.
	 * @param quality a value &gt;= 0.0
	 */
	public ContentType(String mainPart, String subPart, double quality)
	{
		this(mainPart, subPart);
		quality_ = checkQuality(quality);
	}

	
	/**
	 * Creates a copy of a ContentType with the given quality.
	 */
	public ContentType(ContentType contentType, double quality)
	{
		mainPart_ 	= contentType.mainPart_;
		subPart_ 	= contentType.subPart_;
		value_		= contentType.value_; 
		quality_ 	= checkQuality(quality);
	}

	
	private void init(String mainPart, String subPart, String value)
	{
		mainPart_ 	= normPart(mainPart);
		subPart_ 	= normPart(subPart);
		if (value != null)
			value_	= value;
		else
			value_	= (mainPart_ == null ? "*" : mainPart) + '/' + (subPart_ == null ? "*" : subPart_); 
	}
	
	
	private static String normPart(String part)
	{
		if (part != null)
		{
			part = part.trim();
			if ("*".equals(part))
				part = null;
		}
		return part;
	}


	/**
	 * Returns the string representation of the ContentType.
	 */
	public String getValue()
	{
		return value_;
	}

	
	/**
	 * Returns if the string representation of the ContentType
	 * equals the given string.
	 */
	public boolean hasValue(String contentType)
	{
		return value_.equals(contentType);
	}

	
	/**
	 * Returns the main part of the ContentType, i.e. the part before the '/'.
	 * @return the main type or null, if not specified or specified as wildcard '*'.
	 */
	public String getMainPart()
	{
		return mainPart_;
	}
	
	
	/**
	 * Returns the sub part of the ContentType, i.e. the part after the '/'.
	 * @return the subtype or null, if not specified or specified as wildcard '*'.
	 */
	public String getSubPart()
	{
		return subPart_;
	}
	
	
	/**
	 * Returns if the main part of this ContentType matches the given string, i.e.
	 * if either this type or the given type is the wildcard or they are equal.
	 */
	public boolean matchesMainPart(String part)
	{
		return matchesPart(mainPart_, part); 
	}

	
	/**
	 * Returns if the subtype of this ContentType matches the given subtype, i.e.
	 * if either this subtype or the given subtype is the wildcard or if they are equal.
	 */
	public boolean matchesSubPart(String part)
	{
		return matchesPart(subPart_, part); 
	}

	
	/**
	 * Returns if the type and subtype of this ContentType and the other ContentType match.
	 */
	public boolean matches(ContentType contentType)
	{
		return matchesMainPart(contentType.getMainPart()) && matchesSubPart(contentType.getSubPart());  
	}
	
	
	private static boolean matchesPart(String thisPart, String otherPart)
	{
		return (thisPart == otherPart) || (thisPart == null) || (otherPart == null) || thisPart.equals(otherPart) || normPart(otherPart) == null; 
	}
	
	
	/**
	 * Returns if neither the main part nor sub part represent the wildcard. 
	 */
	public boolean isConcrete()
	{
		return (mainPart_ != null) && (subPart_ != null);
	}
	
	
	/**
	 * Returns the quality (degradation) factor.
	 * For content-types used in a HTTP Accept header the quality is a number between 0 and 1. It 
	 * codes the willigness of the client to accept a response with such content-type.
	 * For content-types used on the server side (when specified in a {@link Produces @Produces} annotation,
	 * it is a number &gt;= 0. It can be used to boost specific content-types which have the same
	 * client quality.  
	 */
	public double getQuality()
	{
		return quality_;
	}
	
	
	/**
	 * Returns a new ContentType with the given quality.
	 * @param quality a number &gt;= 0.
	 */
	public ContentType withQuality(double quality)
	{
		return quality_ == quality ? this : new ContentType(this, quality);   
	}
	
	
	protected double checkQuality(double quality)
	{
		if (quality < 0.0)
			throw new IllegalArgumentException("quality must >= 0.0, but is " + quality);
		return quality;
	}
	
	
	/**
	 * Returns the specificity of the content-type.
	 * @see #getSpecificity(String, String) 
	 */
	public int getSpecificity()
	{
		return getSpecificity(mainPart_, subPart_);
	}
	

	/**
	 * Returns the specificity of a content-type with the given parts.
	 * It returns 0 for content-types &#42;/&#42;<br>
	 * It returns 1 for content-types x/&#42;<br>
	 * It returns 2 for content-types &#42;/y<br>
	 * It returns 3 for content-types x/y<br>
	 * @param mainPart the main part of a ContentType or null, if wildcard "*"
	 * @param subPart the sub part of a ContentType or null, if wildcard "*"
	 */
	public static int getSpecificity(String mainPart, String subPart)
	{
		if (mainPart == null)
			return subPart == null ? 0 : 2;
		else
			return subPart == null ? 1 : 3;
	}

	
	/**
	 * Returns a hash code.
	 */
	@Override public int hashCode()
	{
		return value_.hashCode();
	}
	
	
	/**
	 * Tests if the given object represents the same content type string,
	 * but ignoring differences in quality or parameters.
	 */
	@Override public boolean equals(Object other)
	{
		return (other instanceof ContentType) && equals((ContentType)other, false);
	}
	
	
	/**
	 * Tests if the given ContentType represents the same content type string,
	 * @param compareQuality if true then the quality value of the ContentType is also
	 * 		included, to test if they are equal
	 */
	public boolean equals(ContentType other, boolean compareQuality)
	{
		return 
			(other != null) &&
			value_.equals(other.value_) && 
			(!compareQuality || (quality_ == other.quality_)); 
	}


	/**
	 * Returns the string representation of the ContentType. If the quality is not 1.0,
	 * the string representation contains a quality parameter.
	 */
	@Override public String toString()
	{
		String s = value_;
		if (quality_ != 1.0)
			s += "; q=" + quality_;
		return s;
	}

	
	@SuppressWarnings("serial")
	private static class QualityComparator implements Comparator<ContentType>, Serializable
	{
		@Override public int compare(ContentType t1, ContentType t2)
		{
			return (int)Math.signum(t2.getQuality() - t1.getQuality());
		}
	}
	
	
	@SuppressWarnings("serial")
	private static class SpecificityComparator implements Comparator<ContentType>, Serializable
	{
		@Override public int compare(ContentType t1, ContentType t2)
		{
			if ((t1.getMainPart() == null) != (t2.getMainPart() == null))
				return t1.getMainPart() == null ? 1 : -1;
			
			if ((t1.getSubPart() == null) != (t2.getSubPart() == null))
				return t1.getSubPart() == null ? 1 : -1;
			
			return 0;
		}
	}


	private String mainPart_;
	private String subPart_; 
	private String value_;
	private double quality_ = 1.0;
}
