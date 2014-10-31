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
import org.civilian.text.msg.MsgBundle;
import org.civilian.text.msg.MsgBundleFactories;
import org.civilian.text.msg.MsgBundleFactory;


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
		baseName_ = baseName;
	}
	 
	
	/**
	 * Returns the ResMsgBundle for the locale.
	 */
	@Override public MsgBundle getMsgBundle(Locale locale)
	{
		return new ResMsgBundle(baseName_, locale);
	}

	
	/**
	 * Returns an info string.
	 */
	@Override public String toString()
	{
		return MsgBundleFactories.PREFIX_RESBUNDLE + baseName_;
	}
	
	
	private String baseName_;
}
