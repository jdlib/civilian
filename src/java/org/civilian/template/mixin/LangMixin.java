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
package org.civilian.template.mixin;


import org.civilian.template.TemplateWriter;
import org.civilian.text.NumberStyle;
import org.civilian.text.Style;
import org.civilian.text.msg.MsgBundle;
import org.civilian.text.msg.MsgBundleProvider;
import org.civilian.text.service.LocaleService;
import org.civilian.text.service.LocaleServiceProvider;
import org.civilian.text.type.LocaleSerializer;
import org.civilian.text.type.TypeSerializer;
import org.civilian.type.Type;
import org.civilian.util.Check;


/**
 * LangMixin is a template mixin which defines utility methods for localization.
 * It contains a TypeSerializer to format values and a MsgBundle to translate message ids.
 * <p>
 * The LangMixin tries to initialize the TypeSerializer and MsgBundle from a 
 * {@link LocaleServiceProvider} in the TemplateWriter {@link TemplateWriter#getAttribute(Class) context}.
 * If the TemplateWriter was created by a Response, the Response acts as the LocaleServiceProvider,
 * and therefore the mixin uses the TypeSerializer and MsgBundle of the response.  
 * Else the mixin defaults to {@link LocaleSerializer#SYSTEM_LOCALE_SERIALIZER} and an empty message bundle.
 * <p>
 * Independent of the initialization you may explicitly set TypeSerializer and MsgBundle. 
 */
public class LangMixin implements MsgBundleProvider, LocaleServiceProvider
{
	/**
	 * Creates a new LangMixin object.
	 */
	public LangMixin(TemplateWriter out)
	{
		Check.notNull(out, "out");
		
		LocaleServiceProvider lsp = out.getAttribute(LocaleServiceProvider.class);
		init(lsp != null ? lsp.getLocaleService() : LocaleService.SYSTEM_LOCALE_LOCALESERVICE); 
	}
	
	
	/**
	 * Initializes the mixin to use the LocaleSerivce.
	 */
	public void init(LocaleService service)
	{
		ls_ = Check.notNull(service, "service");
	}

	
	//-----------------------------
	// accessors
	//-----------------------------

	
	/**
	 * Returns the LocaleService used by the mixin.
	 */
	@Override public LocaleService getLocaleService()
	{
		return ls_;
	}

	
	/**
	 * Returns the TypeSerializer used by the mixin.
	 */
	public TypeSerializer getSerializer()
	{
		return ls_.getSerializer();
	}
	
	
	/**
	 * Returns the MsgBundle used by the mixin.
	 */
	@Override public MsgBundle getMsgBundle()
	{
		return ls_.getMsgBundle();
	}
		
	
	//------------------------
	// format methods
	//------------------------

	
	/**
	 * Formats the value.
	 * @param value a value
	 * @return the formatted value or "" if the value is null
	 */
	public String format(Object value)
	{
		return format(value, null, null);
	}
	
	
	/**
	 * Formats the value.
	 * @param value a value
	 * @param style a style passed to the formatter or null
	 * @return the formatted value or "" if the value is null
	 */
	public String format(Object value, Style style)
	{
		return format(value, style, null);
	}

	
	/**
	 * Formats the value.
	 * @param value a value
	 * @param defaultFormat returned if the value is null
	 * @return the formatted value or defaultFormat if the value is null
	 */
	public <T> String format(T value, String defaultFormat)
	{
		return format(value, null, defaultFormat);
	}

	
	/**
	 * Formats the value.
	 * @param value a value
	 * @param style a style passed to the formatter or null
	 * @param defaultFormat returned if the value is null
	 * @return the formatted value or defaultFormat if the value is null
	 */
	public <T> String format(T value, Style style, String defaultFormat)
	{
		if (value == null)
			return defaultFormat;
		@SuppressWarnings("unchecked")
		Type<? super T> type = ls_.getTypeLib().get((Class<T>)value.getClass());
		return type == null ?
			value.toString() :
			getSerializer().format(type, value, style);
	}

	
	/**
	 * Formats a int value.
	 * @param value a int value
	 * @return a locale dependent string representation of the integer.
	 */
	public String format(int value)
	{
		return format(value, null);
	}
	

	/**
	 * Formats a int value.
	 * @param value a int value
	 * @param style an optional style object. Use a {@link NumberStyle} variant
	 * 		if you want to tweak locale dependent formatting
	 * @return a locale dependent string representation of the integer.
	 */
	public String format(int value, Style style)
	{
		return format(Integer.valueOf(value), style);
	}

	
	/**
	 * Formats a long value.
	 * @param value a long value
	 * @return a locale dependent string representation of the long value.
	 */
	public String format(long value)
	{
		return format(value, null);
	}

	
	/**
	 * Formats a long value.
	 * @param value a long value
	 * @param style an optional style object. Use a {@link NumberStyle} variant
	 * 		if you want to tweak locale dependent formatting
	 * @return a locale dependent string representation of the long value.
	 */
	public String format(long value, Style style)
	{
		return format(Long.valueOf(value), style);
	}
	
	
	/**
	 * Formats a double value.
	 * @param value a double value
	 * @return a locale dependent string representation of the double value.
	 */
	public String format(double value)
	{
		return format(value, null);
	}

	
	/**
	 * Formats a double value.
	 * @param value a double value
	 * @param style an optional style object. Use a {@link NumberStyle} variant
	 * 		if you want to tweak locale dependent formatting
	 * @return a locale dependent string representation of the double value.
	 */
	public String format(double value, Style style)
	{
		return format(Double.valueOf(value), style);
	}
	

	private LocaleService ls_;
}
