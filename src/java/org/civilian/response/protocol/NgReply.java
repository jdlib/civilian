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


import java.util.ArrayList;
import java.util.HashMap;

import org.civilian.response.Response;
import org.civilian.response.ResponseContent;
import org.civilian.util.Check;


/**
 * NgReply is a experimental ResponseContent implementation for Civilian clients
 * using AngularJS.
 * It can contain these data objects:
 * <ul>
 * <li>Variables, i.e. arbitrary data objects, to be interpreted and used by the client.
 * <li>ScopeVariables, i.e. arbitrary data objects, which are published into the clients
 * 	   active AngularJS scope (see below) under a specific name.
 * <li>Messages, consisting of a title, text and type (e.g. error, info, warning etc.)
 * 	   intended to be displayed by the client, e.g. as a message box
 * <li>Toasts, consisting of a title, text and type (e.g. error, info, warning etc.)
 * 	   intended to be displayed by the client, e.g. as a toast message
 * <li>Alerts, consisting of an text, intended to be display by the client as JavaScript alert().
 * <li>Log texts, which are written to the Clients console log. 
 * </ul>     
 * With exception of pure variables, all other data can be handled in a generic way.
 * Therefore NgReply establishes a "mini"-protocol allowing the server to execute
 * standard actions on the client like the display of a message box.<p>
 * Civilian offers a script civilian.js for use with AngularJS, which contains
 * an interpreter of NgReply.   
 */
public class NgReply extends ResponseContent
{
	/**
	 * Writes the reply as JSON.
	 * @param response a response
	 */
	@Override public void writeTo(Response response) throws Exception
	{
		response.writeJson(this);
	}

	
	/**
	 * Adds a Message to the action result.
	 * The message is intended to be displayed to the user.
	 * @return the message object. Use its methods to set message type, text and title.
	 */
	public Message addMessage()
	{
		return addMessage(new Message());
	}
	
	
	/**
	 * Adds a Message to the action result.
	 * The message is intended to be displayed to the user.
	 * @param text the message text
	 * @return the message object. Use its methods to set message type, title.
	 */
	public Message addMessage(String text)
	{
		return addMessage().setText(text);
	}
	
	
	/**
	 * Adds a Message to the action result.
	 * The message is intended to be displayed to the user.
	 * @param text the message text
	 * @param title the message title
	 * @return the message object. Use its methods to set message type, title.
	 */
	public Message addMessage(String title, String text)
	{
		return addMessage().setText(text).setTitle(title);
	}
	
	
	/**
	 * Adds a Message to the action result.
	 * The message is intended to be displayed to the user.
	 * @param message the message object.
	 * @return the message object. Use its methods to set message type, text and title.
	 */
	public Message addMessage(Message message)
	{
		Check.notNull(message, "message");
		if (messages == null)
			messages = new ArrayList<>(1);
		messages.add(message);
		return message;
	}

	
	/**
	 * @return the number of messages.
	 */
	public int getMessageCount()
	{
		return size(messages);
	}
	
	
	/**
	 * Clears all messages.
	 */
	public void clearMessages()
	{
		messages = null;
	}
	

	/**
	 * Adds a toast message to the action result.
	 * The message is intended to be displayed to the user.
	 * @return the message object. Use its methods to set message type, text and title.
	 */
	public Message addToast()
	{
		return addToast(new Message());
	}
	
	
	/**
	 * Adds a Message to the action result.
	 * The message is intended to be displayed to the user.
	 * @param text the message text
	 * @return the message object. Use its methods to set message type, title.
	 */
	public Message addToast(String text)
	{
		return addToast().setText(text);
	}
	
	
	/**
	 * Adds a toast message to the action result.
	 * The message is intended to be displayed to the user.
	 * @param text the message text
	 * @param title the message title
	 * @return the message object. Use its methods to set message type, title.
	 */
	public Message addToast(String title, String text)
	{
		return addToast().setText(text).setTitle(title);
	}
	
	
	/**
	 * Adds a toast message to the action result.
	 * @param message the message object.
	 * @return the message
	 */
	public Message addToast(Message message)
	{
		Check.notNull(message, "message");
		if (toasts == null)
			toasts = new ArrayList<>(1);
		toasts.add(message);
		return message;
	}
	
	
	/**
	 * @return the number of toasts.
	 */
	public int getToastCount()
	{
		return size(toasts);
	}
	
	
	/**
	 * Clears all toasts.
	 */
	public void clearToasts()
	{
		toasts = null;
	}

	
	/**
	 * Adds a log entry to the NgReply.
	 * The log is intended to be inserted into the Javascript console log.
	 * @param text the log text
	 */
	public void addLog(String text)
	{
		Check.notNull(text, "text");
		if (logs == null)
			logs = new ArrayList<>(1);
		logs.add(text);
	}
	
	
	/**
	 * @return the number of alerts.
	 */
	public int getLogCount()
	{
		return size(logs);
	}
	
	
	/**
	 * Clears all logs.
	 */
	public void clearLogs()
	{
		logs = null;
	}

	
	/**
	 * Adds an alert text to the NgReply.
	 * The text is intended to be displayed as a Javascript alert().
	 * @param text the log text
	 */
	public void addAlert(String text)
	{
		Check.notNull(text, "text");
		if (alerts == null)
			alerts = new ArrayList<>(1);
		alerts.add(text);
	}
	
	
	/**
	 * @return the number of alerts.
	 */
	public int getAlertCount()
	{
		return size(alerts);
	}
	
	
	/**
	 * Clears all alerts.
	 */
	public void clearAlerts()
	{
		alerts = null;
	}

	
	/**
	 * Adds a variable to the NgReply.
	 * @param name the name of the variable. 
	 * @param value the variable value.
	 */
	public void addVariable(String name, Object value)
	{
		vars = addVariable(name, value, vars);
	}
	
	
	/**
	 * @return the number of variables.
	 */
	public int getVariableCount()
	{
		return size(vars);
	}
	
	
	/**
	 * Clears all variables.
	 */
	public void clearVariables()
	{
		vars = null;
	}

	
	/**
	 * Adds a scope variable to the NgReply. In case of AngularJs client,
	 * the variable is intended to be put into the active angular scope.
	 * @param name the name of the variable. 
	 * @param value the variable value.
	 * 
	 */
	public void addScopeVariable(String name, Object value)
	{
		scopeVars = addVariable(name, value, scopeVars);
	}

	
	/**
	 * @return the number of scope variables.
	 */
	public int getScopeVariableCount()
	{
		return size(scopeVars);
	}
	
	
	/**
	 * Clears all variables.
	 */
	public void clearScopeVariables()
	{
		scopeVars = null;
	}

	
	private HashMap<String,Object> addVariable(String name, Object value, HashMap<String,Object> vars)
	{
		Check.notNull(name, "name");
		if (vars == null)
			vars = new HashMap<>();
		vars.put(name, value);
		return vars;
	}

	
	/**
	 * Clears the NgReply.
	 */
	public void clear()
	{
		clearAlerts();
		clearLogs();
		clearMessages();
		clearScopeVariables();
		clearVariables();
	}
	
	
	private int size(ArrayList<?> list)
	{
		return list != null ? list.size() : 0;
	}
			
	
	private int size(HashMap<?,?> map)
	{
		return map != null ? map.size() : 0;
	}

	
	// no trailing _ for properties since we serialize to json 
	private ArrayList<Message> messages; 
	private ArrayList<Message> toasts; 
	private ArrayList<String> alerts; 
	private HashMap<String,Object> scopeVars; 
	private ArrayList<String> logs; 
	private HashMap<String,Object> vars; 
}
