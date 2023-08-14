package org.civilian.internal;


import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import org.civilian.request.Request;
import org.civilian.request.RequestStreamInterceptor;


public class TestRequestStreamInterceptor implements RequestStreamInterceptor
{
	public TestRequestStreamInterceptor(String header)
	{
		header_ = header.getBytes();
	}
	
	
	@Override public InputStream intercept(Request request, InputStream in) throws IOException
	{
		PushbackInputStream pushBack = new PushbackInputStream(in, header_.length);
		pushBack.unread(header_);
		return pushBack;
	}


	private byte[] header_;
}
