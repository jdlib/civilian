package org.civilian.request;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import org.civilian.Request;
import org.civilian.Response;
import org.civilian.Response.Status;
import org.civilian.response.ResponseHeaders;
import org.civilian.util.Check;
import org.civilian.util.IoUtil;


/**
 * Range represents a parsed Range Header. 
 * (https://tools.ietf.org/html/rfc7233#section-3.1)
 */
public class Range extends AbstractList<Range.Part>
{
	public static final String HEADER = "Range";
	private static final String MIME_BOUNDARY = "MIME_BOUNDARY";

	
	/**
	 * Parses the range of a request and if not missing writes the requested range to the response,
	 * else write the whole file to the response
	 * @param file a non-null file
	 * @param request a non-null request
	 * @return the parsed Range or null if no range header was present
	 */
	public static Range writeRange(File file, Request request) throws Exception
	{
		Check.notNull(file, "file");
		Check.notNull(request, "request");
		
		Range range = parse(request);
		writeRange(file, request.getResponse(), range);
		return range;
	}
	
	
	/**
	 * Writes the range of the given file to the response.
	 * @param file a non-null file
	 * @param response a non-null response
	 * @param range a range. If the range is null, the whole file is written. 
	 * 		If the range is not valid a {@link Status#SC416_REQUESTED_RANGE_NOT_SATISFIABLE} is sent.
	 */
	public static void writeRange(File file, Response response, Range range) throws Exception
	{
		Check.notNull(file, "file");
		Check.notNull(response, "response");
		long fileLength = file.length();
		
		if (range == null)
		{
			// no range: write the whole file
			response.setContentLength(fileLength);
			try (FileInputStream in = new FileInputStream(file))
			{
				IoUtil.copy(in, response.getContentStream());
			}
			return;
		}

		ResponseHeaders headers = response.getHeaders();
		if (!range.isValid())
		{
			// range contains syntax errors
			headers.set("Content-Range", "bytes */" + fileLength); // Required in 416.
	        response.sendError(Status.SC416_REQUESTED_RANGE_NOT_SATISFIABLE);
	        return;
		}

		response.setStatus(Status.PARTIAL_CONTENT);
		headers.set("Accept-Ranges", "bytes");
		
		try (RandomAccessFile ra = new RandomAccessFile(file, "r"))
		{
			OutputStream out = response.getContentStream();
			byte[] buffer = new byte[8192];
			
			if (range.size() == 1)
			{
				Part part 		= range.get(0).adjust(fileLength);
				long partLength = part.length();
				headers.set("Content-Range", part.toContentRange(fileLength));
	            response.setContentLength(partLength);
	            part.copy(ra, out, buffer);
			}
			else
			{
				String partContentType = response.getContentType();   
	            response.setContentType("multipart/byteranges; boundary=" + MIME_BOUNDARY);
	            for (Part part : range)
	            {
					part = part.adjust(fileLength);
	                write(out, "\r\n");
	                write(out, "--" + MIME_BOUNDARY + "\r\n");
	                if (partContentType != null)
	                    write(out, "Content-Type: " + partContentType);
	                write(out, "Content-Range: " + part.toContentRange(fileLength) + "\r\n");

		            part.copy(ra, out, buffer);
	            }
			}
		}
	}
	
	
	private static void write(OutputStream out, String s) throws IOException
	{
		out.write(s.getBytes(StandardCharsets.ISO_8859_1));
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
		

		private String toContentRange(long fileLength)
		{
			return "bytes " + start + "-" + end + "/" + fileLength;
		}
		
		
		@Override public String toString()
		{
			return (start < 0 ? "" : String.valueOf(start)) + '-' + (end < 0 ? "" : String.valueOf(end)); 
		}

		
		public final long start;
		public final long end;
	}

	
	public static Builder build()
	{
		return new Builder();
	}
	
	
	public static class Builder
	{
		public Builder add(long start, long end)
		{
			return add(new Part(start, end));
		}
		
		
		public Builder addStart(long start)
		{
			return add(new Part(start, -1));
		}
	
		
		public Builder addEnd(long end)
		{
			return add(new Part(-1, end));
		}
		
		
		private Builder add(Part part)
		{
			parts_.add(part);
			return this;
		}
		
		
		private Range invalid()
		{
			return end(false);
		}

		
		public Range end()
		{
			return end(true);
		}
		
		
		private Range end(boolean valid)
		{
			return new Range(valid && parts_.size() > 0, parts_);
		}
		
		
		private final List<Part> parts_ = new ArrayList<>();
	}
	
	
	private Range(boolean valid, List<Part> parts)
	{
		valid_ = valid;
		parts_ = parts;
	}


	@Override public Part get(int index)
	{
		return parts_.get(index);
	}


	@Override public int size()
	{
		return parts_.size(); 
	}
	
	
	public boolean isValid()
	{
		return valid_;
	}
	
	
	@Override public String toString()
	{
		return "bytes=" + parts_.stream().map(Object::toString).collect(Collectors.joining(","));
	}
	

	/**
	 * Parses the HTTP range header.
	 * @param request the request containing the range header
	 * @return the parsed Range
	 */
	public static Range parse(Request request)
	{
		return parse(request.getHeaders().get(HEADER));
	}
	
	
	/**
	 * Parses a range header.
	 * @param rangeHeader a value of a range header
	 * @return the Range parsed from the input. 
	 * 	If the header is null, then null is returned
	 * 	Else a non-null range is returned. Check its {@link Range#isValid()} method to see if it is a  valid Range.	
	 */
	public static Range parse(String rangeHeader)
	{
		if (rangeHeader == null)
			return null;
		
		Check.notNull(rangeHeader, "rangeHeader");
		Builder builder = build();
		
		if (!rangeHeader.startsWith("bytes="))
			return builder.invalid();
		
		rangeHeader = rangeHeader.substring(6);
		StringTokenizer st 	= new StringTokenizer(rangeHeader, ",");
		try
		{
			while (st.hasMoreTokens())
			{
				String partDef = st.nextToken().trim();
	
				int dashPos = partDef.indexOf('-');
				if (dashPos == -1)
					return builder.invalid();

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
	
				builder.add(new Part(start, end));
			}
		}
		catch (NumberFormatException e)
		{
			return builder.invalid();
		}
		
		return builder.end();
	}
	
	
	private final List<Part> parts_;
	private final boolean valid_;
}
