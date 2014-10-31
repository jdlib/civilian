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
package org.civilian.response.protocol;


import org.civilian.util.Check;


/**
 * Message consists of a text, title and type. It is used by {@link NgReply}
 * to send message data to the client which is intended to be displayed as
 * a message box. The message type is typically used to style the message box (e.g. colors, icons, etc.)  
 */
public class Message
{
	/**
	 * Creates a Message with type "info" and empty text and title. 
	 */
	public Message()
	{
		this(null, null, "info");
	}
	
	
	/**
	 * Creates a Message with the specified text, title, type. 
	 */
	public Message(String text, String title, String type)
	{
		setType(type);
		setText(text);
		setTitle(title);
	}

	
	/**
	 * Sets the message type to "error". 
	 */
	public Message asError()
	{
		return setType("error");
	}

	
	/**
	 * Sets the message type to "info". 
	 */
	public Message asInfo()
	{
		return setType("info");
	}
	
	
	/**
	 * Sets the message type to "success". 
	 */
	public Message asSuccess()
	{
		return setType("success");
	}

	
	/**
	 * Sets the message type to "warning". 
	 */
	public Message asWarning()
	{
		return setType("warning");
	}

	
	/**
	 * Sets the message type. 
	 */
	public Message setType(String type)
	{
		this.type = Check.notNull(type, "type");
		return this;
	}

	
	/**
	 * Returns the message type.
	 */
	public String getType()
	{
		return type;
	}
	
	
	/**
	 * Returns the message text.
	 */
	public String getText()
	{
		return text;
	}
	
	
	/**
	 * Sets the message text.
	 */
	public Message setText(String text)
	{
		this.text = text;
		return this;
	}

	
	/**
	 * Returns the message title.
	 */
	public String getTitle()
	{
		return title;
	}
	
	
	/**
	 * Sets the message title
	 */
	public Message setTitle(String title)
	{
		this.title = title;
		return this;
	}
	
	
	/**
	 * Returns if text and title are null.
	 */
	public boolean isEmpty()
	{
		return (text == null) && (title == null);
	}

	
	// no trailing _ for properties since we serialize to json 
	private String type;
	private String text;
	private String title;
}
