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
package org.civilian.server.servlet;


import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.civilian.application.Application;
import org.civilian.content.ContentType;
import org.civilian.request.AbstractRequest;
import org.civilian.request.CookieList;
import org.civilian.request.LocalInfo;
import org.civilian.request.RemoteInfo;
import org.civilian.request.RequestHeaders;
import org.civilian.request.RequestSecurity;
import org.civilian.request.ServerInfo;
import org.civilian.request.Session;
import org.civilian.util.ArrayUtil;
import org.civilian.util.ClassUtil;
import org.civilian.util.Iterators;
import org.civilian.util.http.HeaderNames;
import org.civilian.util.http.HeaderMap;


/**
 * A Request implementation which presents a HttpServletRequest as civilian Request.
 */
abstract class ServletRequestAdapter extends AbstractRequest implements RequestSecurity
{
	protected static final String[] EMPTY_PARAMS = new String[0];
	
	
	/**
	 * Creates the RequestAdapter.
	 */
	public ServletRequestAdapter(Application app, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
	{
		super(app, servletRequest.getPathInfo());
		servletRequest_		= servletRequest;
		servletResponse_	= servletResponse;
		initEncoding();
	}
	
	
	//----------------------------
	// PathInfo
	//----------------------------

	
	/**
	 * Forwards to the HttpServletRequest.
	 */
	@Override public String getMethod()
	{
		return servletRequest_.getMethod();
	}
	
	
	/**
	 * Forwards to the HttpServletRequest.
	 */
	@Override public String getOriginalPath()
	{
		return servletRequest_.getRequestURI();
	}

	
	//----------------------------
	// attributes
	//----------------------------
	
	
	/**
	 * Forwards to the HttpServletRequest.
	 */
	@Override public Object getAttribute(String name)
	{
		return servletRequest_.getAttribute(name);
	}

	
	/**
	 * Forwards to the HttpServletRequest.
	 */
	@Override public Iterator<String> getAttributeNames()
	{
		return Iterators.asIterator(servletRequest_.getAttributeNames());
	}

	
	/**
	 * Forwards to the HttpServletRequest.
	 */
	@Override public void setAttribute(String name, Object value)
	{
		servletRequest_.setAttribute(name, value);
	}
	
	
	//----------------------------
	// preferences
	//----------------------------

	
	private boolean hasAcceptLanguageHeader()
	{
		return getHeaders().get(HeaderNames.ACCEPT_LANGUAGE) != null;
	}
	

	@Override public Locale getAcceptedLocale()
	{
		return hasAcceptLanguageHeader() ?
			servletRequest_.getLocale() :
			getOwner().getLocaleServices().getDefaultLocale();
	}
	
	
	@Override public Iterator<Locale> getAcceptedLocales()
	{
		return !hasAcceptLanguageHeader() ? 
			Iterators.forValue(getAcceptedLocale()) :
			Iterators.asIterator(servletRequest_.getLocales());
	}

	
	//----------------------------
	// cookies
	//----------------------------
	
	
	/**
	 * Forwards to the HttpServletRequest.
	 */
	@Override public CookieList getCookies()
	{
		return new CookieList(servletRequest_.getCookies());
	}

	
	@Override public ServerInfo getServerInfo()
	{
		return new ServerImpl();
	}
	
	
	@Override public RemoteInfo getRemoteInfo()
	{
		return new RemoteImpl();
	}

	
	@Override public LocalInfo getLocalInfo()
	{
		return new LocalImpl();
	}

	
	//-----------------------------------
	// content
	//-----------------------------------


	@Override public String getCharEncoding()
	{
		return servletRequest_.getCharacterEncoding();
	}

	
	@Override public void setCharEncoding(String encoding) throws UnsupportedEncodingException
	{
		servletRequest_.setCharacterEncoding(encoding);
	}
	
	
	@Override public long getContentLength()
	{
		return servletRequest_.getContentLengthLong();
	}
	
	
	@Override protected ContentType getContentTypeImpl()
	{
		String s = servletRequest_.getContentType();
		if (s != null)
		{
			int p = s.indexOf(';');
			if (p >= 0)
				s = s.substring(0, p);
			return ContentType.getContentType(s);
		}
		else
			return null;
	}


	@Override protected ServletInputStream getContentStreamImpl() throws IOException
	{
		return servletRequest_.getInputStream();
	}


	@Override protected Reader getContentReaderImpl() throws IOException
	{
		return servletRequest_.getReader();
	}
	
	
	//-----------------------------------
	// RequestSecurity
	//-----------------------------------
	

	@Override public RequestSecurity getSecurity()
	{
		return this;
	}

	
	@Override public boolean isSecure()
	{
		return servletRequest_.isSecure();
	}


	@Override public boolean authenticate() throws IOException, ServletException
	{
		return servletRequest_.authenticate(servletResponse_);
	}


	@Override public String getAuthType()
	{
		return servletRequest_.getAuthType();
	}


	@Override public Principal getUserPrincipal()
	{
		return servletRequest_.getUserPrincipal();
	}


	@Override public boolean isUserInRole(String role)
	{
		return servletRequest_.isUserInRole(role);
	}


	@Override public void login(String username, String password) throws ServletException
	{
		servletRequest_.login(username, password);
	}


	@Override public void logout() throws ServletException
	{
		servletRequest_.logout();
	}


	@Override public String getRequestedSessionId()
	{
		return servletRequest_.getRequestedSessionId();
	}
	
	
	@Override public SessionIdSource getRequestedSessionIdSource()
	{
		if (servletRequest_.isRequestedSessionIdFromCookie())
			return SessionIdSource.FROM_COOKIE;
		else if (servletRequest_.isRequestedSessionIdFromURL())
			return SessionIdSource.FROM_URL;
		else
			return SessionIdSource.NONE;
	}
	
	
	@Override public boolean isRequestedSessionIdValid()
	{
		return servletRequest_.isRequestedSessionIdValid();
	}
	

	//-----------------------------------
	// headers
	//-----------------------------------
	
	
	@Override public RequestHeaders getHeaders()
	{
		if (headers_ == null)
			headers_ = new Headers();
		return headers_;
	}

	
	private class Headers extends HeaderMap implements RequestHeaders
	{
		/**
		 * Forwards to the ServletRequest.
		 */
		@Override protected String getNext(String name)
		{
			return servletRequest_.getHeader(name);
		}

		
		/**
		 * Forwards to the ServletRequest.
		 */
		@Override protected long getDateNext(String name)
		{
			return servletRequest_.getDateHeader(name);
		}
		
		
		/**
		 * Forwards to the ServletRequest.
		 */
		@Override protected String[] getNextAll(String name)
		{
			return ArrayUtil.toArray(servletRequest_.getHeaders(name), String.class);
		}


		/**
		 * Forwards to the ServletRequest.
		 */
		@Override protected Iterator<String> getNextNames()
		{
			return Iterators.asIterator(servletRequest_.getHeaderNames());
		}
	}


	//-----------------------------------
	// Session
	//-----------------------------------
	
	
	@Override public Session getSession(boolean create)
	{
		HttpSession httpSession = servletRequest_.getSession(create);
		return httpSession != null ? new SessionAdapter(httpSession) : null;
	}


	//-----------------------------------
	// RemoteInfo, LocalInfo, ServerInfo
	//-----------------------------------

	
	private class RemoteImpl extends RemoteInfo
	{
		@Override public String getIp()
		{
			return servletRequest_.getRemoteAddr();
		}

		@Override public String getHost()
		{
			return servletRequest_.getRemoteHost();
		}

		@Override public String getUser()
		{
			return servletRequest_.getRemoteUser();
		}

		@Override public int getPort()
		{
			return servletRequest_.getRemotePort();
		}
	}
	
	
	private class LocalImpl extends LocalInfo
	{
		@Override public String getIp()
		{
			return servletRequest_.getLocalAddr();
		}

		@Override public String getHost()
		{
			return servletRequest_.getLocalName();
		}

		@Override public int getPort()
		{
			return servletRequest_.getLocalPort();
		}
	}
	
	
	private class ServerImpl extends ServerInfo
	{
		@Override public String getProtocol()
		{
			return servletRequest_.getProtocol();
		}

		@Override public String getScheme()
		{
			return servletRequest_.getScheme();
		}
		
		@Override public String getHost()
		{
			return servletRequest_.getServerName();
		}
		
		@Override public int getPort()
		{
			return servletRequest_.getServerPort();
		}
	}
	

	//-----------------------------------
	// misc
	//-----------------------------------

	
	public HttpServletRequest getServletRequest()
	{
		return servletRequest_;
	}
	
	
	public HttpServletResponse getServletResponse()
	{
		return servletResponse_;
	}
	
	
	/**
	 * Returns the HttpServletRequest if you pass in HttpServletRequest.class.
	 */
	@Override public <T> T unwrap(Class<T> implClass)
	{
		return ClassUtil.unwrap(servletRequest_, implClass);
	}


	protected final HttpServletRequest servletRequest_;
	protected final HttpServletResponse servletResponse_;
	private Headers headers_;
}
