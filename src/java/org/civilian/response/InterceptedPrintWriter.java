package org.civilian.response;


import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;


class InterceptedPrintWriter extends PrintWriter implements InterceptedOutput
{
	private static Writer createWriter(OutputStream originalStream, 
		ResponseInterceptor<OutputStream> streamInterceptor,
		String contentEncoding) throws IOException
	{
		OutputStream out = ResponseInterceptorChain.intercept(originalStream, streamInterceptor);
		return new OutputStreamWriter(out, contentEncoding);
	}
	
	
	public InterceptedPrintWriter(Writer originalWriter, 
		ResponseInterceptor<Writer> writerInterceptor) 
		throws IOException
	{
		super(originalWriter);
		originalWriter_		= originalWriter;
		writerInterceptor_ 	= writerInterceptor;
		init(true);
	}

	
	public InterceptedPrintWriter(OutputStream originalStream, 
		ResponseInterceptor<OutputStream> streamInterceptor, 
		ResponseInterceptor<Writer> writerInterceptor,
		String contentEncoding) throws IOException
	{
		super(createWriter(originalStream, streamInterceptor, contentEncoding));
		writerInterceptor_ 	= writerInterceptor;
		originalStream_		= originalStream;
		streamInterceptor_	= streamInterceptor;
		contentEncoding_	= contentEncoding;
		init(true);
	}
	
	
	private void init(boolean fromCtor) throws IOException
	{
		if (originalWriter_ != null)
		{
			if (!fromCtor)
				this.lock = this.out = originalWriter_;
			this.out = ResponseInterceptorChain.intercept(originalWriter_, writerInterceptor_);
		}
		else
		{
			if (!fromCtor)
				this.lock = this.out = createWriter(originalStream_, streamInterceptor_, contentEncoding_);
			this.out = ResponseInterceptorChain.intercept(this.out, writerInterceptor_); 
		}
	}
	
	

	@Override public void reset()
	{
		try
		{
			init(false);
		}
		catch(IOException e)
		{
			throw new IllegalStateException("could not reset response output", e);
		}
	}
	
	
	private OutputStream originalStream_;
	private Writer originalWriter_;
	private ResponseInterceptor<Writer> writerInterceptor_;
	private ResponseInterceptor<OutputStream> streamInterceptor_;
	private String contentEncoding_;
}
