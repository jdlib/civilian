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
package org.civilian.text.msg.resbundle;


import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.civilian.text.msg.MsgBundle;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;


/**
 * ResMsgBundle is a MsgBundle implementation backed
 * by a java.util.ResourceBundle.
 */
public class ResMsgBundle extends MsgBundle
{
	/**
	 * Creates a new ResMsgBundle for a ResourceBundle. 
	 */
	public ResMsgBundle(ResourceBundle bundle)
	{
		bundle_ = Check.notNull(bundle, "bundle");
	}

	
	/**
	 * Returns the locale of the resource bundle.
	 */
	@Override public Locale getLocale()
	{
		return bundle_.getLocale();
	}
	

	/**
	 * Returns if the resource bundle contains the message 
	 * with the given id.
	 */
	@Override public boolean contains(Object id)
	{
		return bundle_.containsKey(id.toString());
	}
	
	
	/**
	 * Returns the message with the id.
	 * If the resource bundle does not contain a message with that id,
	 * the resulting MissingResourceException is catched and  
	 * null is returned instead.
	 */
	@Override public String get(Object id)
	{
		try
		{
			return bundle_.getString(id.toString());
		}
		catch(MissingResourceException e)
		{
			return null;
		}
	}

	
	/**
	 * Returns the message with the id.
	 * If the resource bundle does not contain a message with that id,
	 * the resulting MissingResourceException is catched and  
	 * the key prefixed with a '?' is returned instead.
	 * @see #getUnknown(Object)
	 */
	@Override public String msg(Object id)
	{
		String text = get(id);
		return text != null ? text : getUnknown(id);
	}

	
	/**
	 * Returns the resource bundle if you pass ResourceBundle.class
	 * as parameter.
	 */
	@Override public <T> T unwrap(Class<T> implClass)
	{
		return ClassUtil.unwrap(bundle_, implClass);
	}
	
	
	private ResourceBundle bundle_;
}
