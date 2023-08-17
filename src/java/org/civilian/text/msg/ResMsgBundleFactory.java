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
import java.util.ResourceBundle;
import org.civilian.util.Check;


/**
 * A MsgBundleFactory implementation which creates
 * ResMsgBundle objects.
 */
public class ResMsgBundleFactory extends MsgBundleFactory
{
	/**
	 * Creates the ResMsgBundleFactory. 
	 * @param baseName the base name of the message bundles
	 */
	public ResMsgBundleFactory(String baseName)
	{
		baseName_ 		= baseName;
		classLoader_	= getClass().getClassLoader();
		control_		= DEFAULT_CONTROL; 
	}
	 
	
	/**
	 * Creates a ResMsgBundleFactory. 
	 * @param msgClass the class name is used as basename (with '.' replaced with '/').
	 */
	public ResMsgBundleFactory(Class<?> msgClass)
	{
		this(msgClass.getName().replace('.', '/'));
	}

	
	/**
	 * Returns the ResMsgBundle for the locale.
	 */
	@Override public MsgBundle getMsgBundle(Locale locale)
	{
		return new ResMsgBundle(getResBundle(locale));
	}

	
	/**
	 * Returns a ResourceBundle for the locale.
	 */
	public ResourceBundle getResBundle(Locale locale)
	{
		return ResourceBundle.getBundle(baseName_, locale, classLoader_, control_);
	}


	/**
	 * Sets the ClassLoader used by the factory.
	 */
	public void setClassLoader(ClassLoader classLoader)
	{
		classLoader_ = Check.notNull(classLoader, "classLoader");
	}
	
	
	/**
	 * Sets the ResourceBundle.Control used by the factory.
	 */
	public void setControl(ResourceBundle.Control control)
	{
		control_ = Check.notNull(control, "control");
	}

	
	/**
	 * Disables caching of ResourceBundles produced by this factory.
	 */
	public void disableCache()
	{
		control_ = new ResourceBundle.Control() 
		{
			@Override public long getTimeToLive(String baseName, Locale locale) 
			{
				return TTL_DONT_CACHE;
			}		
		};
	}

	
	/**
	 * Clears the ResourceBundle cache for our ClassLoader.
	 */
	@Override public void clearCache()
	{
		ResourceBundle.clearCache(classLoader_);
	}

	
	/**
	 * Returns an info string.
	 */
	@Override public String toString()
	{
		return MsgBundleFactories.PREFIX_RESBUNDLE + baseName_;
	}
	
	
	protected final String baseName_;
	protected ClassLoader classLoader_;
	protected ResourceBundle.Control control_;
	protected static final ResourceBundle.Control DEFAULT_CONTROL = new ResourceBundle.Control() {};
}
