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
package org.civilian.text.msg;


import java.util.Locale;

import org.civilian.provider.MsgBundleProvider;
import org.civilian.text.LocaleService;


/**
 * MsgBundle is a bundle of text messages in
 * a specific language, similar to a Java ResourceBundle.
 * LocaleService contains a MsgBundle suitable for its locale.
 * @see LocaleService#getMsgBundle()
 */
public abstract class MsgBundle implements MsgBundleProvider
{
	/**
	 * Creates an empty MsgBundle.
	 */
	public static MsgBundle empty(Locale locale)
	{
		return new EmptyMsgBundle(locale);
	}

	
	/**
	 * Implements MsgBundleProvider and returns this.
	 */
	@Override public MsgBundle getMsgBundle()
	{
		return this;
	}

	
	/**
	 * Returns the locale of the MsgBundle.
	 */
	public abstract Locale getLocale();

	
	/**
	 * Tests if the bundle contains an entry for the given id.
	 */
	public abstract boolean contains(Object id);


	/**
	 * Returns the message entry for the given id.
	 * If the bundle does not contain the id null is returned.
	 */
	public abstract String get(Object id);
	
	
	/**
	 * Returns the message entry for the given id.
	 * If the bundle does not contain the id, the id prefixed
	 * by a "?" is returned.
	 */
	@Override public abstract String msg(Object id);
	
	
	/**
	 * Returns the message entry for the given id, with all the
	 * parameters inserted into the message.
	 * The message may contain placeholders "{0}", "{1}", etc.
	 * These placeholders are replaced with the parameter values
	 * and the resulting string is returned.
	 */
	@Override public String msg(Object id, Object... params)
	{
		String msg = msg(id);
		return msg == null ? null : replaceVars(msg, params);
	}


	/**
	 * Returns the id prefixed by a "?".
	 */
	protected String getUnknown(Object id)
	{
		return "?" + id;
	}
		
	
	/**
	 * Inserts the parameter into the message text. 
	 */
	public String replaceVars(String msg, Object... params)
	{
		if ((params != null) && (params.length > 0))
		{
			for (int i=0; i<params.length; i++)
				msg = replaceVar(msg, i, params[i]);
		}
		return msg;
	}
	
	
	/**
	 * Inserts a single parameter into the message text. 
	 */
	public String replaceVar(String msg, int index, Object param)
	{
		String v = index < VARS.length ? VARS[index] : "{" + index + "}";
		String p = param != null ? param.toString() : "null";
		return msg.replace(v, p);
	}


	/**
	 * Provides access to the MsgBundle implementation. 
	 */
	public abstract <T> T unwrap(Class<T> implClass);


	/**
	 * Returns a description.
	 */
	@Override public String toString()
	{
		return getClass().getSimpleName() + '[' + getLocale() + ']';
	}

	
	private static final String[] VARS = { "{0}", "{1}", "{2}", "{3}", "{4}", "{5}", "{6}", "{7}" };  
}
