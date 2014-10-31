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
package org.civilian.response;


import java.io.Writer;
import java.util.Locale;
import org.civilian.Request;
import org.civilian.Response;
import org.civilian.Template;
import org.civilian.content.ContentSerializer;
import org.civilian.provider.LocaleDataProvider;
import org.civilian.text.LocaleData;
import org.civilian.text.msg.MsgBundle;
import org.civilian.type.TypeSerializer;
import org.civilian.type.lib.LocaleSerializer;
import org.civilian.util.ArrayUtil;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;
import org.civilian.util.TabWriter;


/**
 * ResponseWriter is used to write a character text response.
 * You could use it directly to write the response, but in most cases
 * it will be passed to a {@link Template} or {@link ContentSerializer}
 * which will write their content to it.
 * It can be obtained by calling {@link Response#getContentWriter()}.
 * The ResponseWriter provides the following functions:
 * <ul>
 * <li>it is a PrintWriter
 * <li>it is a TabWriter i.e. allows convenient indenting of content
 * <li>it provides a {@link TypeSerializer} which can be used to format dates, numbers, etc. 
 * <li>it provides a {@link MsgBundle} which can be used to translate message ids into message texts.
 * <li>it can be associated with multiple {@link #addContext(Object) context} objects. The Response will add itself
 * 		as context object.  
 * </ul>    
 */
public class ResponseWriter extends TabWriter
{
	/**
	 * Sets the default line separator used by a new ResponseWriter.
	 * Since a ResponseWriter is mostly used in a web environment, it will by default
	 * use a single '\n' character as line separator (whereas the TabWriter by default
	 * uses the system dependent line separator).
	 */ 
	public static void setDefaultLineSeparator(String separator)
	{
		defaultLineSeparator_ = Check.notNull(separator, "separator");
	}

	
	/**
	 * Returns the default line separator used by a new ResponseWriter.
	 */ 
	public static String getDefaultLineSeparator()
	{
		return defaultLineSeparator_;
	}

	
	/**
	 * Creates a new ResponseWriter. 
	 * @param writer the underlying writer 
	 */
	public ResponseWriter(Writer writer)
	{
		this(writer, false);
	}
	
	
	/**
	 * Creates a new ResponseWriter. 
	 * @param writer the underlying writer 
	 * @param autoFlush - a boolean; if true, the println() methods will flush
	 *      the output buffer
	 */
	public ResponseWriter(Writer writer, boolean autoFlush)
	{
		super(writer, autoFlush);
		setLineSeparator(defaultLineSeparator_);
	}

	
	//------------------------
	// locale related
	//------------------------
	
	
	/**
	 * Sets the TypeSerializer of this ResponseWriter.
	 */
	public void setTypeSerializer(TypeSerializer serializer)
	{
		typeSerializer_ = Check.notNull(serializer, "serializer");
	}
	
	
	/**
	 * Returns the TypeSerializer of this ResponseWriter.
	 * If the ResponseWriter was created from a {@link Request} the 
	 * TypeSerializer is initialized with the one from the requests
	 * {@link Request#getLocaleData() LocaleData}.
	 * Else it is the {@link LocaleSerializer#SYSTEM_LOCALE_SERIALIZER LocaleSerializer} 
	 * for the default system locale. 
	 */
	public TypeSerializer getTypeSerializer()
	{
		if (typeSerializer_ == null)
			initTypeSerializer();
		return typeSerializer_;
	}

	
	private void initTypeSerializer()
	{
		initDefaults();
		if (typeSerializer_ == null)
			typeSerializer_ = LocaleSerializer.SYSTEM_LOCALE_SERIALIZER;
	}
	
	
	/**
	 * Sets the MsgBundle of this ResponseWriter.
	 */
	public void setMsgBundle(MsgBundle msgBundle)
	{
		msgBundle_ = Check.notNull(msgBundle, "msgBundle");
	}
	
	
	/**
	 * Returns the MsgBundle of this ResponseWriter.
	 */
	public MsgBundle getMsgBundle()
	{
		if (msgBundle_ == null)
			initMsgBundle();
		return msgBundle_;
	}
		
	
	private void initMsgBundle()
	{
		initDefaults();
		if (msgBundle_ == null)
			msgBundle_ = MsgBundle.empty(Locale.getDefault());
	}
	
	
	private void initDefaults()
	{
		LocaleDataProvider lp = getContext(LocaleDataProvider.class);
		if (lp != null)
		{
			LocaleData ld = lp.getLocaleData();
			if (typeSerializer_ == null)
				typeSerializer_ = ld.getTypeSerializer();
			if (msgBundle_ == null)
				msgBundle_ = ld.getMsgBundle();
		}
	}
	
	
	/**
	 * Sets the MsgBundle and TypeSerialiter of the ResponseWriter
	 * to the instances provided by the LocaleData.
	 */
	public void setLocaleData(LocaleData localeData)
	{
		Check.notNull(localeData, "localeData");
		setMsgBundle(localeData.getMsgBundle());
		setTypeSerializer(localeData.getTypeSerializer());
	}

	
	//------------------------
	// context
	//------------------------

	
	/**
	 * Associates the ResponseWriter with an arbitrary context object.
     * When the ResponseWriter is constructed within a Civilian request
	 * the {@link Response} is added as context object. 
	 */
	public void addContext(Object context)
	{
		context_ = (context_ == null) ?
			context :
			ArrayUtil.addLast((Object[])context_, context);
	}

	
	/**
	 * Returns the first context object of the ResponseWriter that has
	 * the given class.
	 */
	public <T> T getContext(Class<? extends T> cls)
	{
		return ClassUtil.unwrap(context_, cls);
	}
	
	
	/**
	 * Returns the ResponseWriters context object if it has
	 * the given class. Else it throws an IllegalStateException
	 */
	public <T> T getSafeContext(Class<? extends T> cls)
	{
		T context = getContext(cls);
		if (context != null)
			return context;
		throw new IllegalStateException("context is " + (context_ != null ? context_.getClass().getName() : "null") + " but not a " + cls.getName());
	}

	
	//------------------------
	// print methods
	//------------------------

	
	/**
	 * Checks if the object is a printable and in that case calls
	 * print(Printable), else it just calls the default implementation.
	 */
	@Override public void print(Object object)
	{
		if (object instanceof Printable)
			print((Printable)object);
		else
			super.print(object);
	}
	
	
	/**
	 * If a not-null Printable is passed to the ResponseWriter,
	 * then the printable is asked to print itself. 
	 */
	public void print(Printable printable)
	{
		if (printable != null)
		{
			try
			{
				printable.print(this);
			}
			catch (RuntimeException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				throw new IllegalStateException("error when printing '" + printable.getClass().getName() + "'", e);
			}
		}
	}
	
	
	/**
	 * Printable is a interface for print-aware classes who 
	 * implement a custom print strategy. Templates and form controls
	 * are examples of Printables.
	 * If you pass a Printable to {@link ResponseWriter#print(Object)} or
	 * ResponseWriter#print(Printable), then the {@link #print(ResponseWriter)}
	 * method of the Printable is called by the ResponseWriter, allowing
	 * to Printable to print itself.
	 */
	public static interface Printable 
	{
		public void print(ResponseWriter out) throws Exception;
	}

	
	private TypeSerializer typeSerializer_;
	private MsgBundle msgBundle_;
	private Object context_;
	private static String defaultLineSeparator_ = "\n";
}
