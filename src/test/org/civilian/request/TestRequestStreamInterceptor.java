package org.civilian.request;


import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;


public class TestRequestStreamInterceptor implements RequestInterceptor<InputStream>
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


	private final byte[] header_;
}
