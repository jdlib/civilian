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
package org.civilian.response;


import org.civilian.util.Check;


/**
 * AsyncEvent represents an event of asynchronous request processing.
 * An AsyncEvent is issued by a {@link AsyncContext} and delivered
 * to {@link AsyncEventListener}s.
 */
public class AsyncEvent
{
	/**
	 * The type of the AsyncEvent.
	 */
	public enum Type
	{
		/**
		 *  An asynchronous operation has been completed.
		 *  @see AsyncContext#complete()
		 */
		COMPLETE,

		/**
		 *  An asynchronous operation failed to complete.
		 */
		ERROR,

		/**
		 *  An asynchronous operation was started.
		 *  @see Response#startAsync()
		 */
		START,

		/**
		 *  An asynchronous operation expired.
		 *  @see AsyncContext#setTimeout(long)
		 */
		TIMEOUT
	}
	 
	
	/**
	 * Creates a new AsyncEvent.
	 * @param type the event type
	 * @param context the context
	 */
	public AsyncEvent(Type type, AsyncContext context)
	{
		this(type, context, null);
	}
	
	
	/**
	 * Creates a new AsyncEvent.
	 * @param type the event type
	 * @param context the context
	 * @param throwable an error or null
	 */
	public AsyncEvent(Type type, AsyncContext context, Throwable throwable)
	{
		type_ 		= Check.notNull(type, "type");
		context_	= Check.notNull(context, "context");
		throwable_	= throwable;
	}

	
	/**
	 * @return the event type.
	 */
	public Type getType()
	{
		return type_;
	}
	

	/**
	 * @return the context.
	 */
	public AsyncContext getContext()
	{
		return context_;
	}
	
	
	/**
	 * @return the event error.
	 */
	public Throwable getError()
	{
		return throwable_;
	}
	
	
	private final Type type_;
	private final Throwable throwable_;
	private final AsyncContext context_;
}
