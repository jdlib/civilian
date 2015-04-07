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


/**
 * MsgId is a CharSequence which can be used
 * as {@link MsgBundle#msg(Object) message id parameter}.
 * The intention of this class is explained in the documentation
 * of ResBundleCompiler.
 */
public class MsgId implements CharSequence
{
	/**
	 * Creates a new Message id.
	 * @param value the string value of the message id.
	 */
	public MsgId(String value)
	{
		value_ = value;
	}
	
	
	/**
	 * CharSequence implementation.
	 */
	@Override public int length()
	{
		return value_.length();
	}


	/**
	 * CharSequence implementation.
	 */
	@Override public char charAt(int index)
	{
		return value_.charAt(index);
	}


	/**
	 * CharSequence implementation.
	 */
	@Override public CharSequence subSequence(int start, int end)
	{
		return value_.subSequence(start, end);
	}

	
	/**
	 * CharSequence implementation.
	 */
	@Override public String toString()
	{
		return value_;
	}

	
	private String value_;
}
