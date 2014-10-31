package org.civilian.processor;


import java.io.ByteArrayOutputStream;
import java.util.zip.DeflaterOutputStream;
import org.civilian.CivTest;
import org.civilian.context.test.TestRequest;
import org.junit.Test;


public class DecompressorTest extends CivTest
{
	@Test public void test() throws Exception
	{
		request_ = new TestRequest();
		decompressor_ = new Decompressor();
		
		request_.setContent("a");
		assertProcess(null, "a");
		assertProcess("unknown", "a");
		assertProcess("identity", "a");
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DeflaterOutputStream  dout = new DeflaterOutputStream(bout);
		dout.write('a');
		dout.close();
		request_.setContent(bout.toByteArray());
		
		assertProcess("deflate", "a");
	}

	
	private void assertProcess(String contentEncoding, String readContent) throws Exception
	{
		request_.getHeaders().set("Content-Encoding", contentEncoding);
		
		// process always returns false
		assertFalse(decompressor_.process(request_, ProcessorChain.EMPTY));
		
		assertEquals(readContent, request_.readContent(String.class));
		
		request_.resetContentInput();
	}
	
	
	private TestRequest request_;
	private Decompressor decompressor_;
}
