package org.civilian.request;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import org.civilian.Request;
import org.civilian.Response;
import org.civilian.Response.Status;
import org.civilian.response.ResponseHeaders;
import org.civilian.util.Check;


/**
 * Range represents a parsed Range Header. 
 * https://tools.ietf.org/html/rfc7233#section-3.1
 */
@SuppressWarnings("serial")
public class Range extends ArrayList<Range.Part>
{
	public static final String HEADER = "Range";
	private static final String MIME_BOUNDARY = "MIME_BOUNDARY";
	
	
	public static boolean writeRange(File file, Request request) throws Exception
	{
		String rangeHeader = request.getHeaders().get(HEADER);
		if (rangeHeader == null)
			return false;

		long fileLength 		= file.length();
		Range range 			= Range.parse(rangeHeader);
		Response response 		= request.getResponse();
		ResponseHeaders headers = response.getHeaders();
		if (range == null)
		{
			// range contains syntax errors
			headers.set("Content-Range", "bytes */" + fileLength); // Required in 416.
	        response.sendError(Status.SC416_REQUESTED_RANGE_NOT_SATISFIABLE);
	        return true;
		}

		response.setStatus(Status.PARTIAL_CONTENT);
		response.getHeaders().set("Accept-Ranges", "bytes");
		
		try (RandomAccessFile ra = new RandomAccessFile(file, "r"))
		{
			OutputStream out = response.getContentStream();
			byte[] buffer = new byte[8192];
			
			if (range.size() == 1)
			{
				Part part 		= range.get(0).adjust(fileLength);
				long partLength = part.length();
				headers.set("Content-Range", "bytes " + part.start + "-" + part.end + "/" + partLength);
	            response.setContentLength(partLength);
	            part.copy(ra, out, buffer);
			}
			else
			{
				String partContentType = response.getContentType();   
	            response.setContentType("multipart/byteranges; boundary=" + MIME_BOUNDARY);
	            for (Part part : range)
	            {
	                write(out, "\r\n");
	                write(out, "--" + MIME_BOUNDARY + "\r\n");
	                if (partContentType != null)
	                    write(out, "Content-Type: " + partContentType);
	                write(out, "Content-Range: bytes " + part.start + "-" + part.end + "/" + part.length() + "\r\n");

		            part.copy(ra, out, buffer);
	            }
			}
			return true;
		}
	}
	
	
	private static void write(OutputStream out, String s) throws IOException
	{
		out.write(s.getBytes(StandardCharsets.ISO_8859_1));
	}
	
	
	public Range add(long start, long end)
	{
		add(new Part(start, end));
		return this;
	}
	
	
	@Override public String toString()
	{
		return "bytes=" + stream().map(Object::toString).collect(Collectors.joining(","));
	}
	

	public static class Part
	{
		public Part(long start, long end)
		{
			this.start 	= start;
			this.end 	= end;
		}

	
		public long length()
		{
			return Math.max(0, end - start + 1);
		}
		
		
		public Part adjust(long fileLength)
		{
			long start = this.start;
			long end   = this.end;

			if (start < 0)
			{
				// suffix part
				start = end >= fileLength ? 0L : fileLength - end;
				end   = fileLength - 1;
			}
			else
			{
				if ((end < 0) || (end >= fileLength))
					end = fileLength - 1;
				if (start > end)
					start = end;
			}
			return new Part(start, end);
		}
		
		
		private void copy(RandomAccessFile file, OutputStream out, byte[] buffer) throws IOException
		{
			file.seek(start);
			
			int length = (int)length();
			int done   = 0;  
			while (done < length)
			{
				int len  = Math.min(buffer.length, length - done);
				int read = file.read(buffer, 0, len);
				if (read < 0)
					break;
				out.write(buffer, 0, read);
				done += read;
			}
		}
		
		
		@Override public String toString()
		{
			return (start < 0 ? "" : String.valueOf(start)) + '-' + (end < 0 ? "" : String.valueOf(end)); 
		}

		
		public final long start;
		public final long end;
	}


	/**
	 * Parses a range header.
	 * @param rangeHeader the header text
	 * @return a set of ranges parsed from the input, or null if not valid
	 */
	public static Range parse(String rangeHeader)
	{
		Check.notNull(rangeHeader, "rangeHeader");
		if (!rangeHeader.startsWith("bytes="))
			return null;
		
		rangeHeader = rangeHeader.substring(6);
		Range range = new Range();
		StringTokenizer st = new StringTokenizer(rangeHeader, ",");
		try
		{
			while (st.hasMoreTokens())
			{
				String partDef = st.nextToken().trim();
	
				int dashPos = partDef.indexOf('-');
				if (dashPos == -1)
					return null;

				long start, end;
				if (dashPos == 0)
				{
					start 	= -1;
					end 	= Long.parseLong(partDef.substring(1));
				}
				else
				{
					start = Long.parseLong(partDef.substring(0, dashPos));
					if (dashPos < partDef.length() - 1)
						end = Long.parseLong(partDef.substring(dashPos + 1, partDef.length()));
					else
						end = -1L;
				}
	
				range.add(new Part(start, end));
			}
		}
		catch (NumberFormatException e)
		{
			return null;
		}
		
		return range.size() == 0 ? null : range;
	}
}
