package org.civilian.processor;


import org.civilian.CivTest;
import org.civilian.response.ResponseHeaders;
import org.civilian.server.test.TestRequest;
import org.civilian.util.http.HttpHeaders;
import org.junit.Test;


public class CompressorTest extends CivTest
{
	@Test public void test() throws Exception
	{
		request_ = new TestRequest();
		compressor_ = new Compressor();
		
		// no Accept-encoding header
		assertProcess(null, null, null, null, "a");

		// Accept-encoding = identity
		assertProcess("identity", null, null, null, "a");

		// Accept-encoding = deflate
		assertProcess("deflate", "Accept-Encoding", "deflate", null, (byte)120, (byte)-100);

		// Accept-encoding = deflate, also modifies etag
		request_.getTestResponse().getHeaders().set(HttpHeaders.ETAG, "a");
		assertProcess("deflate", "Accept-Encoding", "deflate", "a-deflate", (byte)120, (byte)-100);

		// no compression if someone else applied a content-encoding
		request_.getTestResponse().getHeaders().set(HttpHeaders.CONTENT_ENCODING, "someotherenc");
		assertProcess("deflate", "Accept-Encoding", "someotherenc", null, "a");

		// no compression if someone else applied a content-encoding
		request_.getTestResponse().getHeaders().setNull(HttpHeaders.CONTENT_ENCODING);
		request_.setAttribute(Compressor.NO_COMPRESSION, Boolean.TRUE);
		assertProcess("deflate", "Accept-Encoding", null, null, "a");
	}
	
	
	private void assertProcess(String acceptEncoding, String vary, String contentEncoding, String etag, String content) throws Exception
	{
		assertProcess(acceptEncoding, vary, contentEncoding, etag, content.getBytes());
	}
	
	
	private void assertProcess(String acceptEncoding, String vary, String contentEncoding, String etag, byte... content) throws Exception
	{
		request_.getHeaders().set(HttpHeaders.ACCEPT_ENCODING, acceptEncoding);
		ResponseHeaders headers = request_.getResponse().getHeaders();
		
		// process always returns false
		assertFalse(compressor_.process(request_, ProcessorChain.EMPTY));
		
		request_.getResponse().getContentWriter().print("a");
		assertArrayEquals(content, request_.getTestResponse().getContentBytes(true));
		
		assertEquals(vary, headers.get(HttpHeaders.VARY));
		assertEquals(contentEncoding, headers.get(HttpHeaders.CONTENT_ENCODING));
		assertEquals(etag, headers.get(HttpHeaders.ETAG));

		request_.getTestResponse().clear();
	}
	
	
	private TestRequest request_;
	private Compressor compressor_;
}
