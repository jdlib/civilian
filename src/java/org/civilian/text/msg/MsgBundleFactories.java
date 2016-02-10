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

import org.civilian.text.msg.resbundle.ResMsgBundleFactory;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;


/**
 * A utility class to create MsgBundleFactories
 */
public abstract class MsgBundleFactories
{
	public static final String PREFIX_RESBUNDLE = "resbundle:";
	public static final String EMPTY = "empty";

	
	/**
	 * Creates a MessageBundelFactory from a definition string in the config file.
	 * @param definition right now these definitions strings are supported:
	 * 		<ul>
	 * 		<li>resbundle:&lt;base name for java resoure bundles&gt;
	 * 		</ul>
	 * 		Else the definition string is interpreted as class name of MsgBundleFactory.
	 */
	public static MsgBundleFactory createFactory(String definition) 
		throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		Check.notNull(definition, "definition");
		
		if (definition.startsWith(PREFIX_RESBUNDLE))
		{
			String baseName = definition.substring(PREFIX_RESBUNDLE.length()).trim(); 
			return createResBundleFactory(baseName);
		}
		else if (definition.equals(EMPTY))
			return createEmptyFactory();
		else
			return ClassUtil.createObject(definition, MsgBundleFactory.class, null);
	}
	
	
	/**
	 * Creates a MsgBundleFactory which returns empty MsgNundles. 
	 */
	public static MsgBundleFactory createEmptyFactory()
	{
		return new EmptyMsgBundleFactory();
	}
	

	/**
	 * Creates a MsgBundleFactory based on Java resource bundles. 
	 * @param baseName the base name of the message bundles
	 */
	public static MsgBundleFactory createResBundleFactory(String baseName)
	{
		return new ResMsgBundleFactory(baseName);
	}
}
