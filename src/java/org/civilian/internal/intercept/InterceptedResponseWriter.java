package org.civilian.internal.intercept;


import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.civilian.response.ResponseStreamInterceptor;
import org.civilian.response.ResponseWriter;
import org.civilian.response.ResponseWriterInterceptor;


public class InterceptedResponseWriter extends ResponseWriter implements InterceptedOutput
{
	private static Writer createWriter(OutputStream originalStream, 
		ResponseStreamInterceptor streamInterceptor,
		String contentEncoding) throws IOException
	{
		OutputStream out = RespStreamInterceptorChain.intercept(originalStream, streamInterceptor);
		return new OutputStreamWriter(out, contentEncoding);
	}
	
	
	public InterceptedResponseWriter(Writer originalWriter, 
		ResponseWriterInterceptor writerInterceptor) 
		throws IOException
	{
		super(originalWriter);
		originalWriter_		= originalWriter;
		writerInterceptor_ 	= writerInterceptor;
		init(true);
	}

	
	public InterceptedResponseWriter(OutputStream originalStream, 
		ResponseStreamInterceptor streamInterceptor, 
		ResponseWriterInterceptor writerInterceptor,
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
			this.out = RespWriterInterceptorChain.intercept(originalWriter_, writerInterceptor_);
		}
		else
		{
			if (!fromCtor)
				this.lock = this.out = createWriter(originalStream_, streamInterceptor_, contentEncoding_);
			this.out = RespWriterInterceptorChain.intercept(this.out, writerInterceptor_); 
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
	private ResponseWriterInterceptor writerInterceptor_;
	private ResponseStreamInterceptor streamInterceptor_;
	private String contentEncoding_;
}
