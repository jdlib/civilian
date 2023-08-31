package org.civilian.response;


import java.io.FilterOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;


/**
 * InterceptedOutputStream is a helper class to implement Response.flushBuffer().
 * If the response output is not intercepted, then the response outputstream or writer
 * is the original servlet outputstream or writer.
 * In this case we just need to call resetBuffer on the underlying ServletResponse.
 * If the response output is intercepted then the user has obtained the 
 * following chain of OutpuStreams (upto the InterceptedOutputStream)
 * or writer
 * <ol>
 * <li>ServletOutputStream
 * <li>InterceptedStream 1, e.g. GzipOutputStream, ... 
 * <li>InterceptedStream n
 * <li>InterceptedOutputStream, returned by Response.getContentStream()
 * <li>OutputStreamWriter
 * </ol>
 * In case of a resetBuffer() call we still forward it to ServletResponse.resetBuffer().
 * But the writer and outputstream chain needs some handling:
 * <ul>
 * <li>Stations in the chain may have buffered data (e.g. OutputStreamWriter
 * 		may keep an encoding buffer, GzipOutputStream has a deflater buffer, ...).
 * <li>InterceptedStream may have written initialization data (e.g. GzipOutputStream
 * 		initialize writes a gzip header, which is lost when the buffer is reset). 
 * </ul>
 * Therefore we do the following:
 * <ol>
 * <li>InterceptedOutputStream.out is temporarily set to a Nil outputstream.
 * <li>Any Writer on top of the InterceptedOutputStream is flushed (and the data goes into Nil)
 * <li>InterceptedOutputStream.out is reconstructed from the interceptor chain.
 * 		{@link ResponseStreamInterceptor#intercept(OutputStream)} may be called
 * 		multiple times.
 * </ol>
 */
class InterceptedOutputStream extends FilterOutputStream implements InterceptedOutput
{
	public InterceptedOutputStream(OutputStream originalStream, 
		ResponseInterceptor<OutputStream> interceptor)
		throws IOException
	{
		super(ResponseInterceptorChain.intercept(originalStream, interceptor));
		
		originalStream_ = originalStream;
		interceptor_	= interceptor;
	}
	
	
	public void setWriter(Writer writer)
	{
		writer_ = writer;
	}
	
	
	@Override public void reset()
	{
		try
		{
			if (writer_ != null)
			{
				out = new Nil();
				writer_.flush();
			}
			
			out = ResponseInterceptorChain.intercept(originalStream_, interceptor_);
		}
		catch(IOException e)
		{
			throw new IllegalStateException("could not reset response output", e);
		}
	}
	
	
	/**
	 * An OutputStream that does nothing.
	 */
	private static class Nil extends OutputStream
	{
		@Override public void write(int b)
		{
		}

		
		@Override public void write(byte b[], int off, int len)
		{
		}
	}

	
	private OutputStream originalStream_;
	private ResponseInterceptor<OutputStream> interceptor_;
	private Flushable writer_;
}
