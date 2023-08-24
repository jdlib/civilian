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
package org.civilian.controller;


import org.civilian.Logs;
import org.civilian.annotation.Consumes;
import org.civilian.annotation.Get;
import org.civilian.annotation.Post;
import org.civilian.annotation.Produces;
import org.civilian.annotation.RequestMethod;
import org.civilian.application.Application;
import org.civilian.controller.method.ControllerMethod;
import org.civilian.request.Request;
import org.civilian.request.RequestProvider;
import org.civilian.resource.Resource;
import org.civilian.resource.ResourceHandler;
import org.civilian.response.Response;
import org.civilian.response.ResponseProvider;
import org.civilian.text.msg.MsgBundle;
import org.civilian.text.msg.MsgBundleProvider;
import org.civilian.text.service.LocaleService;
import org.civilian.util.Check;
import org.civilian.util.http.HeaderNames;


/**
 * Controller handles requests for a {@link Resource}. 
 * All controller classes of an application must be derived from Controller. 
 * A Controller object is instantiated to process a single request and is then
 * discarded. Therefore controllers don't need to be threadsafe 
 * and may declare own properties and use them to
 * process the request. The Controller class itself has several properties, notably 
 * for the {@link #getRequest() request} and {@link #getResponse() response}.
 * <p>
 * Controllers define action methods which represent different ways to handle a request,
 * depending on request properties. 
 * An action method is a 
 * <ul>
 * <li>non-static method
 * <li>with return type void 
 * <li>annotated with a {@link RequestMethod} annotation or one or more of the abbreviated 
 * 	   method annotations like {@link Get}, {@link Post} etc.
 * </ul>
 * <p>
 * Additionally an action method can be annotated with {@link Consumes} and {@link Produces}
 * annotations, to signal which request content can be processed and/or which response
 * content can be produced.
 * <p>
 * Given an incoming request, the best matching action method is selected by an algorithm
 * called content negotiation. If no action method matches the request, an error 
 * is sent (e.g. {@link Response.Status#SC405_METHOD_NOT_ALLOWED},
 * {@link Response.Status#SC406_NOT_ACCEPTABLE},
 * {@link Response.Status#SC415_UNSUPPORTED_MEDIA_TYPE} as response. 
 * Else the matching method is invoked.<br>
 * An action method can have annotated parameters into which context values, like 
 * request parameters, matrix parameters,
 * headers, etc. are injected when the method is invoked.
 * <p> 
 * Before and after the invocation of the negotiated action method several 
 * predefined controller methods are called, 
 * to allow the controller to properly initialize and shutdown before and after request processing.  
 * The exact invocation order is as follows:
 * <pre><code>
 * {@link #checkAccess() checkAccess}
 * try
 *     if request is not committed
 *         [actionMethod,error] = find matching action method
 *         if actionMethod is null
 *              {@link #reject(int) reject(error)}
 *         else
 *              {@link #init()}
 *              {@link #setCaching()}
 *              if request is not committed
 *                   call action method
 * catch exception
 *     {@link #onError(Exception) onError(exception)}
 * finally
 *     {@link #exit()}
 * </code></pre> 			
 * All of the above methods except exit() allow to throw any exception. 
 * If you decide not to handle an exception in your controller
 * implementation, then the exception is passed to {@link Application#onError(Request, Throwable)}
 * for application-wide error handling.
 */
public class Controller implements MsgBundleProvider, RequestProvider, ResponseProvider, ResourceHandler
{
	//------------------------------------
	// accessors
	//------------------------------------

	
	/**
	 * Returns the develop flag of the application.
	 * @see Application#develop
	 */
	public boolean develop()
	{
		return getApplication().develop();
	}


	/**
	 * Returns the application to which the controller belongs.
	 */
	public Application getApplication()
	{
		return Check.isA(getRequest().getOwner(), Application.class);
	}

	
	/**
	 * Returns the request.
	 */
	@Override public Request getRequest()
	{
		checkProcessing();
		return request_;
	}
	

	/**
	 * Returns the response.
	 * @return the Response
	 */
	@Override public Response getResponse()
	{
		checkProcessing();
		return response_;
	}
	

	private void setRequestResponse(Request request, Response response)
	{
		if (request_ != null)
			throw new IllegalStateException("already processing");
		request_ = Check.notNull(request, "request");
		response_= Check.notNull(response, "response");
	}

	
	/**
	 * Returns the type of the controller.
	 * @return the ControllerType
	 */
	public ControllerType getControllerType()
	{
		return type_;
	}
	
	
	/**
	 * Sets the ControllerType of a Controller. This is automatically done
	 * when a Controller is created during resource dispatch.
	 * @param controller the Controller
	 * @param type the ControllerType
	 */
	void setControllerType(ControllerType type)
	{
		Check.notNull(type, "type");
		if (type.getControllerClass() != getClass())
			throw new IllegalArgumentException();
		type_ = type;
	}
	
	
	/**
	 * Returns the LocaleService of the response. 
	 * Shortcut for getResponse().getLocaleService().
	 * @return the LocaleService
	 */
	public LocaleService getLocaleService()
	{
		return getResponse().getLocaleService();
	}

	
	/**
	 * Returns the MsgBundle object of the LocaleService in the response.
	 * Shortcut for getResponse().getLocaleService().getMsgBundle()
	 * @return the MsgBundle of the locale service
	 */
	@Override public MsgBundle getMsgBundle()
	{
		return getLocaleService().getMsgBundle();
	}
	
	
	/**
	 * Returns any uncatched exception thrown during request processing. 
	 * This is the same exception passed to {@link #onError(Exception)}.
	 * @return the Exception
	 */
	public Exception getException()
	{
		return exception_;
	}
	
	

	//------------------------------------
	// processing
	//------------------------------------

	
	/**
	 * Processes a request. This method is called by the resource dispatch
	 * on the controller defined for the resource which matches the request.
	 * The ControllerType must have been {@link #setControllerType(ControllerType) initialized}
	 * before this method can be called.
	 * This method 
	 * <ul>
	 * <li>calls the various init-methods
	 * <li>selects the action method based on request properties
	 * <li>invokes the action method
	 * <li>calls the various exit-methods
	 * </ul>
	 * @param request the request
	 * @param request the response
	 * @throws Exception if an error during processing occurs
	 */
	public void process(Request request, Response response) throws Exception
	{
		setRequestResponse(request, response);
		
		try
		{
			boolean debug = Logs.CONTROLLER.isDebugEnabled();
			if (debug)
				Logs.CONTROLLER.debug(getClass().getName());
			
			checkAccess();
			if (!response.isCommitted())
			{
				NegotiatedMethod negMethod = negotiate(request);
				if (negMethod.success())
				{
					response.setContentType(negMethod.getContentType());
					init();
					setCaching();
					if (!response.isCommitted())
					{
						ControllerMethod method = negMethod.getMethod();
						if (debug)
							Logs.CONTROLLER.debug("#{}", method);
						method.invoke(this, request, response);
					}
				}
				else
					reject(negMethod.getError());
			}
		}
		catch(Exception e)
		{
			exception_ = e;
			onError(e);
		}
		finally
		{
			exit();
			request_  = null;
			response_ = null;
		}
	}


	private NegotiatedMethod negotiate(Request request)
	{
		if (type_ == null)
			throw new IllegalArgumentException("ControllerType not initialized");
		return type_.getMethod(request);
	}
	

	/**
	 * Returns if the controller is currently processing a request.
	 */
	public boolean isProcessing()
	{
		return request_ != null;
	}
	
	
	private void checkProcessing()
	{
		if (request_ == null)
			throw new IllegalStateException("may not be called outside of process(Request)");
	}

	
	/**
	 * Called at the beginning of request processing.
	 * The controller should check if access to the resource is allowed.<br>
	 * Example: Controllers for resources who lie behind a login-wall 
	 * could allow access depending on whether there exists a valid session.<br>
	 * If the resource does not allow access, it should either set
	 * an appropriate response status code, or redirect the response.<br>
	 * The controller can use the request or response object, but should not 
	 * rely on any other initializations. 
	 */
	protected void checkAccess() throws Exception
	{
	}
	

	/**
	 * Called when no action method matches the request.
	 * The default implementation sends an response error.
	 * @param error the suggested response error code (405, 406, 415)
	 */
	protected void reject(int error) throws Exception
	{
		getResponse().sendError(error);
	}

	
	/**
	 * Initialize the controller. Called after {@link #checkAccess()} was called. 
	 * Derived classes can put common initialization code here.
	 * The default implementation is empty.
	 */
	protected void init() throws Exception
	{
	}
	
	
	/**
	 * Configures the caching behavior. 
	 * Called after {@link #init()} was called.
	 * The default implementation sets the "Cache-Control" header to "no-cache".  
	 */
	protected void setCaching()
	{
		getResponse().getHeaders().set(HeaderNames.CACHE_CONTROL, "no-cache");
	}

	
	/**
	 * Called when an exception is thrown during request processing.
	 * The default implementation just rethrows the exception which will then be delivered
	 * to {@link Application#onError(Request, Response, Throwable)}.
	 * Derived controller implementations which want to handle errors should override this method.
	 */
	protected void onError(Exception e) throws Exception
	{
		throw e;
	}
	
	
	/**
	 * Called at the end of request processing.
	 */
	protected void exit()
	{
	}
	
	
	private Request request_;
	private Response response_;
	private ControllerType type_;
	private Exception exception_;
}
