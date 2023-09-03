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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.StringTokenizer;
import org.civilian.util.Check;
import org.civilian.util.http.HeaderParser;


/**
 * CompressionScheme represents a scheme to compress or decompress binary content.
 */
public abstract class CompressionScheme
{
	/**
	 * Defines constants for known compression schemes.
	 */
	public interface Names
	{
		/**
		 * The name of the "identity" compression scheme.
		 */
		public static final String IDENTITY = "identity";
	
		
		/**
		 * The name of the "gzip" compression scheme.
		 */
		public static final String GZIP = "gzip";
	
		
		/**
		 * An alternate name of the "gzip" compression scheme.
		 */
		public static final String X_GZIP = "x-gzip";
	
		
		/**
		 * The name of the "deflate" compression scheme.
		 */
		public static final String DEFLATE = "deflate";
	
		
		/**
		 * The name of the "compress" compression scheme.
		 */
		public static final String COMPRESS = "compress";
	
		
		/**
		 * An alternate name of the "compress" compression scheme.
		 */
		public static final String X_COMPRESS = "x-compress";
	
		
		/**
		 * The name of the "br" (brotli) compression scheme.
		 */
		public static final String BROTLI = "br";
	}

	
	/**
	 * The default implementation of the "compress" compression scheme.
	 */
	public static final CompressionScheme COMPRESS = new Compress();


	/**
	 * The default implementation of the "deflate" compression scheme.
	 */
	public static final CompressionScheme DEFLATE = new Deflate();

	
	/**
	 * The default implementation of the "gzip" compression scheme.
	 */
	public static final CompressionScheme GZIP = new GZip();


	/**
	 * The default implementation of the "identity" compression scheme.
	 */
	public static final CompressionScheme IDENTITY = new Identity();

		
	private static CompressionScheme preferred_ = GZIP;
	private static CompressionScheme identity_ 	= IDENTITY;
	private static final HashMap<String,CompressionScheme> REGISTRY = new HashMap<>();
	static
	{
		set(Names.IDENTITY, 	IDENTITY);
		set(Names.GZIP, 		GZIP);
		set(Names.X_GZIP, 		GZIP);
		set(Names.COMPRESS, 	COMPRESS);
		set(Names.X_COMPRESS,	COMPRESS);
		set(Names.DEFLATE, 		DEFLATE);
	}


	/**
	 * Sets or removes a compression scheme
	 * @param name the name
	 * @param scheme the scheme or null if the scheme under the name should
	 * 		be unregistered
	 */
	public static void set(String name, CompressionScheme scheme)
	{
		Check.notNull(name, "name");
		if (scheme != null)
		{
			REGISTRY.put(name, scheme);
			if (scheme.isIdentity())
				identity_ = scheme;
		}
		else
			REGISTRY.remove(name);
	}
	
	
	/**
	 * Returns a CompressionScheme for a name.
	 * @param name a name
	 * @return the scheme or null if not registered.
	 */
	public static CompressionScheme get(String name)
	{
		return REGISTRY.get(name);
	}
	
	
	/**
	 * Returns a CompressionScheme for a name.
	 * @param name a name
	 * @param defaultScheme a default scheme
	 * @return the scheme or the defaultScheme if not registered.
	 */
	public static CompressionScheme get(String name, CompressionScheme defaultScheme)
	{
		CompressionScheme scheme = get(name);
		return scheme != null ? scheme : defaultScheme;
	}

	
	/**
	 * @return the preferred CompressionScheme. By default this is the "gzip" scheme.
	 */
	public static CompressionScheme getPreferred()
	{
		return preferred_;
	}

	
	/**
	 * Sets the preferred compression scheme.
	 * @param preferred the preferred scheme
	 */
	public static void setPreferred(CompressionScheme preferred)
	{
		preferred_ = Check.notNull(preferred, "preferred");
	}

	
	/**
	 * @return the preferred CompressionScheme. By default this is the "gzip" scheme.
	 */
	public static CompressionScheme getIdentity()
	{
		return identity_;
	}

	
	/**
	 * Sets the identity compression scheme.
	 * @param identity the scheme
	 */
	public static void setIdentity(CompressionScheme identity)
	{
		identity_ = Check.notNull(identity, "identity");
	}
	
	
	/**
	 * Returns the best matching Compression-Scheme for the Accept-Encoding header of a request. 
	 * @param accept a String defining the accepted compression schemes, as given by
	 * 		the Accept-Encoding header of a request. It must not be null.
	 * 		<a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html">http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html</a>.
	 * @return the best matching scheme. The returned scheme may be the identity scheme.
	 * 		Null is returned if no scheme matched and identity was explicitly excluded
	 */
	public static CompressionScheme match(String accept)
	{
		Check.notNull(accept, "accept");
		
		if (accept.length() == 0)
			return identity_; 
		
		if ("*".equals(accept))
			return preferred_;
		
		// next contains weights
		if (accept.contains(";"))
			return matchWeighted(accept);
		
		// now: accept either a single name or a list of names
		
		// check it it contains the preferred
		if (accept.contains(preferred_.getName()))
			return preferred_;
		
		// check if it is a single name
		if (!accept.contains(","))
			return get(accept, identity_);
		
		// parse the list
		StringTokenizer st = new StringTokenizer(accept, ",");
		while(st.hasMoreTokens())
		{
			String next = st.nextToken().trim();
			CompressionScheme scheme = get(next);
			if (scheme != null)
				return scheme;
		}
		
		// give up
		return identity_;
	}


	private static CompressionScheme matchWeighted(String accept)
	{
		CompressionScheme best 	= null;
		boolean identityAllowed = true;
		double bestQuality 		= 0.0;
		
		try
		{
			HeaderParser parser = new HeaderParser(accept);
			
			parser.next();
			while(parser.getToken() == HeaderParser.Token.ITEM)
			{
				boolean isWildcard 			= "*".equals(parser.item);
				CompressionScheme scheme 	= isWildcard ? preferred_ : get(parser.item);
				double quality 				= 1.0;
				
				while(parser.next() == HeaderParser.Token.PARAM)
				{
					if ("q".equals(parser.paramName))
						quality = Double.parseDouble(parser.paramValue);
				}
				
				if ((scheme != null) && (quality > bestQuality))
				{
					best = scheme;
					bestQuality = quality;
				}
				if ((quality == 0) && ((scheme == identity_) || isWildcard))
					identityAllowed = false;
			}
			
		}
		catch(RuntimeException e)
		{
			// ignore
		}
		
		if (best != null)
			return best;
		else
			return identityAllowed ? identity_ : null;
	}

	
	/**
	 * Creates a new CompressionScheme
	 * @param name the scheme name
	 */
	public CompressionScheme(String name)
	{
		name_ = name;
	}
	
	
	/**
	 * @return the name.
	 */
	public String getName()
	{
		return name_;
	}

	
	/**
	 * @return if this scheme is the identity scheme.
	 */
	public boolean isIdentity()
	{
		return Names.IDENTITY.equals(name_);
	}

	
	/**
	 * Wraps a InputStream for compressed binary content which decompresses the data.
	 * @param in a InputStream
	 * @return the wrapped stream
	 * @throws IOException if a IO error occurs
	 */
	public abstract InputStream wrap(InputStream in) throws IOException;

	
	/**
	 * Wraps a OutputStream for uncompressed binary content which compresses the data.
	 * @param out a OutputStream
	 * @return the wrapped stream
	 * @throws IOException if a IO error occurs
	 */
	public abstract OutputStream wrap(OutputStream out) throws IOException;

	
	/**
	 * The GZip CompressionScheme.
	 */
	public static class GZip extends CompressionScheme
	{
		public GZip()
		{
			super(Names.GZIP);
		}
		
		
		@Override public InputStream wrap(InputStream in) throws IOException
		{
			return new java.util.zip.GZIPInputStream(in);
		}


		@Override public OutputStream wrap(OutputStream out) throws IOException
		{
			return new GZipOutputStream(out);
		}
	}


	/**
	 * GZipOutputStream is a java.util.zip.GZIPOutputStream.
	 * which actively releases its deflater. 
	 */
	public static class GZipOutputStream extends java.util.zip.GZIPOutputStream
	{
		public GZipOutputStream(OutputStream out) throws IOException
		{
			super(out, true /*syncFlush*/);
		}
		
	
		/**
		 * Also releases the deflater.
		 */
		@Override public void finish() throws IOException
		{
			super.finish();
			def.end();
		}
	}
	

	/**
	 * The "Compress" (e.g. Zip) CompressionScheme.
	 */
	public static class Compress extends CompressionScheme
	{
		public Compress()
		{
			super(Names.COMPRESS);
		}
		
		
		@Override public InputStream wrap(InputStream in)  throws IOException
		{
			return new java.util.zip.ZipInputStream(in);
		}

	
		@Override public OutputStream wrap(OutputStream out) throws IOException
		{
			return new ZipOutputStream(out);
		}
	}


	/**
	 * ZipOutputStream is a java.util.zip.ZipOutputStream.
	 * which actively releases its deflater. 
	 */
	public static class ZipOutputStream extends java.util.zip.ZipOutputStream
	{
		public ZipOutputStream(OutputStream out) throws IOException
		{
			super(out);
		}
		
	
		/**
		 * Also releases the deflater.
		 */
		@Override public void finish() throws IOException
		{
			super.finish();
			def.end();
		}
	}
	

	/**
	 * The Deflate CompressionScheme.
	 */
	public static class Deflate extends CompressionScheme
	{
		public Deflate()
		{
			super(Names.DEFLATE);
		}
		
		
		@Override public InputStream wrap(InputStream in) throws IOException
		{
			return new java.util.zip.InflaterInputStream(in);
		}

	
		@Override public OutputStream wrap(OutputStream out) throws IOException
		{
			return new DeflaterOutputStream(out);
		}
	}
	
	
	/**
	 * DeflaterOutputStream is a java.util.zip.DeflaterOutputStream.
	 * which actively releases its deflater. 
	 */
	public static class DeflaterOutputStream extends java.util.zip.DeflaterOutputStream
	{
		public DeflaterOutputStream(OutputStream out) throws IOException
		{
			super(out);
		}
		
	
		/**
		 * Also releases the deflater.
		 */
		@Override public void finish() throws IOException
		{
			super.finish();
			def.end();
		}
	}
	

	/**
	 * The Deflate CompressionScheme.
	 */
	public static class Identity extends CompressionScheme
	{
		public Identity()
		{
			super(Names.IDENTITY);
		}
		
		
		@Override public InputStream wrap(InputStream in) throws IOException
		{
			return in;
		}

	
		@Override public OutputStream wrap(OutputStream out) throws IOException
		{
			return out;
		}
	}

	
	private final String name_;
}
