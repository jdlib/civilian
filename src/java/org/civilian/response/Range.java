package org.civilian.response;


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
import org.civilian.request.Request;
import org.civilian.response.Response.Status;
import org.civilian.util.Check;
import org.civilian.util.http.HeaderNames;


/**
 * Range represents a parsed Range Header. 
 * (https://tools.ietf.org/html/rfc7233#section-3.1)
 */
public class Range extends AbstractList<Range.Part>
{
	private static final String MIME_BOUNDARY = "MIME_BOUNDARY";

	
	/**
	 * Parses the range of a request and if not missing writes the requested range to the response,
	 * else write the whole file to the response
	 * @param file a non-null file
	 * @param request a non-null request
	 * @param response a non-null response
	 * @return the parsed Range or null if no range header was present
	 * @throws IOException if an I/O error occurs
	 */
	public static Range writeRange(File file, Request request, Response response) throws IOException
	{
		Check.notNull(file, "file");
		Check.notNull(request, "request");
		Check.notNull(response, "response");
		
		Range range = parse(request);
		writeRange(file, response, range);
		return range;
	}
	
	
	/**
	 * Writes the range of the given file to the response.
	 * @param file a non-null file
	 * @param response a non-null response
	 * @param range a range. If the range is null, the whole file is written. 
	 * 		If the range is not valid a {@link Status#SC416_REQUESTED_RANGE_NOT_SATISFIABLE} is sent.
	 * @throws IOException if an I/O error occurs
	 */
	public static void writeRange(File file, Response response, Range range) throws IOException 
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
				in.transferTo(response.getContentStream());
			}
			return;
		}

		ResponseHeaders headers = response.getHeaders();
		if (!range.isValid())
		{
			// range contains syntax errors
			headers.set(HeaderNames.CONTENT_RANGE, "bytes */" + fileLength); // Required in 416.
	        response.sendError(Status.SC416_REQUESTED_RANGE_NOT_SATISFIABLE);
	        return;
		}

		response.setStatus(Status.PARTIAL_CONTENT);
		headers.set(HeaderNames.ACCEPT_RANGES, "bytes");
		
		try (RandomAccessFile ra = new RandomAccessFile(file, "r"))
		{
			OutputStream out = response.getContentStream();
			byte[] buffer = new byte[8192];
			
			if (range.size() == 1)
			{
				Part part 		= range.get(0).adjust(fileLength);
				long partLength = part.length();
				headers.set(HeaderNames.CONTENT_RANGE, part.toContentRange(fileLength));
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
		
		
		@Override public int hashCode()
		{
			return Long.hashCode(start) ^ Long.hashCode(end); 
		}
		
		
		@Override public boolean equals(Object other)
		{
			if (other instanceof Part) 
			{
				Part p = (Part)other;
				return (start == p.start) && (end == p.end); 
			}
			else
				return false; 
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
			return new Range(valid && !parts_.isEmpty(), parts_);
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
		return parse(request.getHeaders().get(HeaderNames.RANGE));
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
