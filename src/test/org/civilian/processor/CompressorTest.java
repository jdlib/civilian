package org.civilian.processor;


import org.civilian.CivTest;
import org.civilian.response.ResponseHeaders;
import org.civilian.server.test.TestRequest;
import org.civilian.server.test.TestResponse;
import org.civilian.util.http.HeaderNames;
import org.junit.Test;


public class CompressorTest extends CivTest
{
	@Test public void test() throws Exception
	{
		request_ 	= new TestRequest();
		response_	= request_.getTestResponse();
		compressor_ = new Compressor();
		
		// no Accept-encoding header
		assertProcess(null, null, null, null, "a");

		// Accept-encoding = identity
		assertProcess("identity", null, null, null, "a");

		// Accept-encoding = deflate
		assertProcess("deflate", "Accept-Encoding", "deflate", null, (byte)120, (byte)-100);

		// Accept-encoding = deflate, also modifies etag
		response_.getHeaders().set(HeaderNames.ETAG, "a");
		assertProcess("deflate", "Accept-Encoding", "deflate", "a-deflate", (byte)120, (byte)-100);

		// no compression if someone else applied a content-encoding
		response_.getHeaders().set(HeaderNames.CONTENT_ENCODING, "someotherenc");
		assertProcess("deflate", "Accept-Encoding", "someotherenc", null, "a");

		// no compression if someone else applied a content-encoding
		response_.getHeaders().setNull(HeaderNames.CONTENT_ENCODING);
		response_.setAttribute(Compressor.NO_COMPRESSION, Boolean.TRUE);
		assertProcess("deflate", "Accept-Encoding", null, null, "a");
	}
	
	
	private void assertProcess(String acceptEncoding, String vary, String contentEncoding, String etag, String content) throws Exception
	{
		assertProcess(acceptEncoding, vary, contentEncoding, etag, content.getBytes());
	}
	
	
	private void assertProcess(String acceptEncoding, String vary, String contentEncoding, String etag, byte... content) throws Exception
	{
		request_.getHeaders().set(HeaderNames.ACCEPT_ENCODING, acceptEncoding);
		ResponseHeaders headers = response_.getHeaders();
		
		// process always returns false
		assertFalse(compressor_.process(request_, response_, ProcessorChain.EMPTY));
		
		response_.getContentWriter().print("a");
		assertArrayEquals(content, response_.getContentBytes(true));
		
		assertEquals(vary, headers.get(HeaderNames.VARY));
		assertEquals(contentEncoding, headers.get(HeaderNames.CONTENT_ENCODING));
		assertEquals(etag, headers.get(HeaderNames.ETAG));

		response_.clear();
	}
	
	
	private TestRequest request_;
	private TestResponse response_;
	private Compressor compressor_;
}
