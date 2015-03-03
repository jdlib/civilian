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


class EmptyMsgBundle extends MsgBundle
{
	public EmptyMsgBundle(Locale locale)
	{
		locale_ = locale;
	}
	
	
	@Override public Locale getLocale()
	{
		return locale_;
	}

	
	@Override public boolean contains(Object id)
	{
		return false;
	}
		
	
	@Override public String get(Object id)
	{
		return null;
	}

	
	@Override public String msg(Object id)
	{
		return getUnknown(id);
	}

	
	@Override public <T> T unwrap(Class<T> implClass)
	{
		return null;
	}

	
	private Locale locale_;
}
