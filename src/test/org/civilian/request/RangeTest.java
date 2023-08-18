package org.civilian.request;


import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import org.civilian.CivTest;
import org.civilian.request.Range.Part;
import org.civilian.server.test.TestApp;
import org.civilian.server.test.TestRequest;
import org.civilian.server.test.TestResponse;
import org.civilian.util.http.HttpHeaders;
import org.junit.Test;


public class RangeTest extends CivTest
{
	@Test public void testParseError()
	{
		assertParseError("");
		assertParseError("a");
		assertParseError("bytes");
		assertParseError("bytes=");
		assertParseError("bytes=1");
		assertParseError("bytes=1;");
	}
	
	
	private void assertParseError(String rangeHeader)
	{
		Range range = Range.parse(rangeHeader);
		assertNotNull(range);
		assertFalse(range.isValid());
	}


	@Test public void testParseOk()
	{
		assertParseOk("bytes=0-", 0, -1);
		assertParseOk("bytes=-500", -1, 500);
		assertParseOk("bytes=200-500", 200, 500);
		assertParseOk("bytes=200-100", 200, 100);
		assertParseOk("bytes=0-0,-1", 0, 0, -1, 1);
		assertParseOk("bytes=9437184-10485759", 9437184, 10485759); 
		assertParseOk("bytes=12582912-13631487", 12582912, 13631487); 
		assertParseOk("bytes=28311552-29360127", 28311552, 29360127); 
	}
	
	
	private void assertParseOk(String rangeHeader, long... startEnds)
	{
		Range range = Range.parse(rangeHeader);
		assertNotNull(range);
		assertEquals(range.size() * 2, startEnds.length);
		assertTrue(range.isValid());
		
		int n = 0;
		for (Part part : range)
		{
			assertEquals(startEnds[n++], part.start);
			assertEquals(startEnds[n++], part.end);
		}
	}

	
	@Test public void testToString()
	{
		assertToString("bytes=0-");
		assertToString("bytes=-500");
		assertToString("bytes=200-500");
		assertToString("bytes=0-0,-1");
		assertToString("bytes=0-0, -1", "bytes=0-0,-1");
	}


	private void assertToString(String rangeHeader)
	{
		assertToString(rangeHeader, rangeHeader);
	}
	
	
	private void assertToString(String rangeHeader, String expected)
	{
		Range range = Range.parse(rangeHeader);
		assertNotNull(range);
		assertEquals(expected, range.toString());
	}
	
	
	@Test public void testAdjust()
	{
		Range range = Range.parse("bytes=12582912-13631487");
		assertEquals(1, range.size());
		Range.Part part = range.get(0);
		assertEquals(12582912, part.start);
		assertEquals(13631487, part.end);
		assertEquals(1048576,  part.length());
		Part adjusted = part.adjust(40702100);
		assertEquals(part, adjusted);
	}
	
	
	@Test public void testWrite() throws Exception
	{
		File file = File.createTempFile("test", ".tmp");
		file.deleteOnExit();
		try
		{
			try (FileOutputStream out = new FileOutputStream(file)) 
			{
				out.write("ABCDEF".getBytes(StandardCharsets.ISO_8859_1));
			}
			assertEquals(6, file.length());
			
			TestApp app = new TestApp();
			TestRequest request = new TestRequest(app);
			TestResponse response = request.getTestResponse();
			request.getHeaders().set(HttpHeaders.RANGE, Range.build().add(0, 2).end().toString());
			
			Range.writeRange(file, request);
			assertEquals(3, response.getHeaders().getInt("content-length"));
			assertEquals("bytes", response.getHeaders().get(HttpHeaders.ACCEPT_RANGES));
			assertEquals("bytes 0-2/6", response.getHeaders().get(HttpHeaders.CONTENT_RANGE));
			assertEquals("ABC", response.getContentText(true));

			response.clear();
			request.getHeaders().set(HttpHeaders.RANGE, Range.build().addStart(2).addEnd(2).end().toString());
			Range.writeRange(file, request);
			assertEquals(null, response.getHeaders().get(HttpHeaders.CONTENT_LENGTH));
			assertEquals("multipart/byteranges; boundary=MIME_BOUNDARY", response.getContentType());
			assertEquals("bytes", response.getHeaders().get(HttpHeaders.ACCEPT_RANGES));
			assertEquals("\r\n" + 
				"--MIME_BOUNDARY\r\n" + 
				"Content-Range: bytes 2-5/6\r\n" + 
				"CDEF\r\n" + 
				"--MIME_BOUNDARY\r\n" + 
				"Content-Range: bytes 4-5/6\r\n" + 
				"EF", response.getContentText(true));
		}
		
		finally
		{
			file.delete();
		}
	}
}
