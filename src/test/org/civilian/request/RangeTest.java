package org.civilian.request;


import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import org.civilian.CivTest;
import org.civilian.context.test.TestApp;
import org.civilian.context.test.TestRequest;
import org.civilian.context.test.TestResponse;
import org.civilian.request.Range.Part;
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
		assertNull(range);
	}


	@Test public void testParseOk()
	{
		assertParseOk("bytes=0-", 0, -1);
		assertParseOk("bytes=-500", -1, 500);
		assertParseOk("bytes=200-500", 200, 500);
		assertParseOk("bytes=200-100", 200, 100);
		assertParseOk("bytes=0-0,-1", 0, 0, -1, 1);
	}
	
	
	private void assertParseOk(String rangeHeader, long... startEnds)
	{
		Range range = Range.parse(rangeHeader);
		assertNotNull(range);
		assertEquals(range.size() * 2, startEnds.length);
		
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
			request.getHeaders().set(Range.HEADER, new Range().add(0, 2).toString());
			
			Range.writeRange(file, request);
			assertEquals(3, response.getHeaders().getInt("content-length"));
			assertEquals("bytes", response.getHeaders().get("accept-ranges"));
			assertEquals("bytes 0-2/6", response.getHeaders().get("content-range"));
			assertEquals("ABC", response.getContentText(true));

			response.clear();
			request.getHeaders().set(Range.HEADER, new Range().addStart(2).addEnd(2).toString());
			Range.writeRange(file, request);
			assertEquals(null, response.getHeaders().get("content-length"));
			assertEquals("multipart/byteranges; boundary=MIME_BOUNDARY", response.getContentType());
			assertEquals("bytes", response.getHeaders().get("accept-ranges"));
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
