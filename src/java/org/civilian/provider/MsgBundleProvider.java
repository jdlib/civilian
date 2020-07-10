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
package org.civilian.provider;


import org.civilian.text.msg.MsgBundle;


/** 
 * An interface for a service which can provide
 * a MsgBundle and translate a message id into a message text.
 */
public interface MsgBundleProvider
{
	/**
	 * Returns the MsgBundle.
	 * @return the bundle
	 */
	public MsgBundle getMsgBundle();

	
	/**
	 * Returns the message for the id
	 */
	public default String msg(Object id)
	{
		return getMsgBundle().msg(id);
	}

	
	/**
	 * Returns the message for the id and inserts the parameters
	 * into the message at placeholder locations.
	 */
	public default String msg(Object id, Object... params)
	{
		return getMsgBundle().msg(id, params);
	}


	/**
	 * Returns the text for the id.
	 * If the id is a String, the id is returned as text, else {@link #msg(Object)} is returned.
	 */
	public default String msgOrText(Object id)
	{
		if (id instanceof String)
			return (String)id;
		else
			return msg(id);
	}


	/**
	 * Returns the message for the id using {@link #msgOrText(Object)} and inserts the parameters
	 * into the message at placeholder locations.
	 */
	public default String msgOrText(Object id, Object... params)
	{
		return getMsgBundle().replaceVars(msgOrText(id), params);
	}
}
