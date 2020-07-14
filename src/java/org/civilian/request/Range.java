package org.civilian.request;


import java.util.ArrayList;
import java.util.StringTokenizer;
import org.civilian.util.Check;


/**
 * Range represents a parsed Range Header. 
 * https://tools.ietf.org/html/rfc7233#section-3.1
 */
@SuppressWarnings("serial")
public class Range extends ArrayList<Range.Part>
{
	public static class Part
	{
		public Part(long start, long end)
		{
			this.start 	= start;
			this.end 	= end;
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
