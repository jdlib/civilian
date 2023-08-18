package org.civilian.request;


import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;


public class TestRequestReaderInterceptor implements RequestInterceptor<Reader>
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
