package org.civilian.internal;


import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import org.civilian.request.Request;
import org.civilian.request.RequestReaderInterceptor;


public class TestRequestReaderInterceptor implements RequestReaderInterceptor
{
	public TestRequestReaderInterceptor(String header)
	{
		header_ = header.toCharArray();
	}
	
	
	@Override public Reader intercept(Request request, Reader reader) throws IOException
	{
		PushbackReader pushBack = new PushbackReader(reader, header_.length);
		pushBack.unread(header_);
		return pushBack;
	}


	private char[] header_;
}
