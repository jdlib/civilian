/*
 * Copyright (C) 2014 Civilian Framework.
 *
 * Licensed under the Civilian License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.civilian-framework.org/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.civilian.resource;


import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * PathScanner is a helper class used during resource dispatch.
 * It allows iteration of the segments of a request path.
 */
// PathScanner avoids splitting the path at segment boundaries which
public class PathScanner
{
	/**
	 * Creates a new PathScanner for a path string.
	 * If the last segment of the path has an extension
	 * it will be ignored.
	 * If the then last segment equals "/index" it will also be ignored.
	 */
	public PathScanner(String path)
	{
		path_ 	= path != null ? path : "";
		end_	= path_.length();
		
		// ignore trailing extension
		int lastSlashPos	= path_.lastIndexOf('/');
		int extPos 			= path_.indexOf('.', lastSlashPos + 1);
		if (extPos >= 0)
			end_ = extPos;
		
		// ignore trailing '/index'
		if ((end_ >= 6) && path_.regionMatches(end_ - 6, "/index", 0, 6))
			end_ -= 6;
		
		// ignore leading '/'
		if ((end_ > 0) && (path_.charAt(0) == '/'))
			segmentStart_ = 1;
			
		initNextSegment();
	}
	

	/**
	 * Returns the whole path of the scanner.
	 */
	public String getPath()
	{
		return path_;
	}

	
	/**
	 * Returns the current scan position in the path.
	 */
	public int getPosition()
	{
		return segmentStart_;
	}
	
	
	public void setPosition(int position)
	{
		segmentStart_ = position;
		initNextSegment();
	}
	
	
	private void initNextSegment()
	{
		int p = path_.indexOf('/', segmentStart_);
		segmentEnd_ = p < 0 ? end_ : p;
		segmentLen_	= segmentEnd_ - segmentStart_;
	}
	

	/**
	 * Returns true if the scanner has not yet reached
	 * the end of the path.
	 */
	public boolean hasMore()
	{
		return segmentStart_ < end_;
	}
	

	/**
	 * Returns the current segment.
	 */
	public String getSegment()
	{
		return path_.substring(segmentStart_, segmentEnd_);
	}
	
	
	/**
	 * Returns if the current segment equals the given segment. 
	 */
	public boolean matchSegment(String segment)
	{
		return (segment.length() == segmentLen_) && 
				path_.regionMatches(false, segmentStart_, segment, 0, segmentLen_);
	}
	

	/**
	 * Returns if the path substring starting at the current scan position
	 * matches the pattern. The pattern can match one or more path segments
	 * (therefore allowing a lookahead for multiple segments).
	 * It can also match only whole segments.
	 */
	public MatchResult matchPattern(Pattern pattern)
	{
		Matcher matcher = pattern.matcher(path_);
		matcher.region(segmentStart_, end_);
		return matcher.lookingAt() && isSegmentEnd(matcher.end()) ?
			matcher.toMatchResult() :
			null;
	}
	

	/**
	 * Advances the scanner to the next segment.
	 * Does nothing if the scanner is already at the end of the path.
	 */
	public void next()
	{
		segmentStart_ = segmentEnd_ + 1;
		if (!hasMore())
		{
			segmentEnd_ = segmentStart_;
			segmentLen_ = 0;
		}
		else
			initNextSegment();
	}
	
	
	/**
	 * Positions the scanner at the given position.
	 * @throws IllegalArgumentException if the position is not a valid segment boundary. 
	 */
	public void next(int end)
	{
		if (!isSegmentEnd(end))
			throw new IllegalArgumentException("not a segment or path end: " + end);
		segmentEnd_ = end;
		next(); 
	}


	/**
	 * Positions the scanner at the end of the matched string.
	 * @param result a MatchResult obtained by a call to {@link #matchPattern(Pattern)}
	 */
	public void next(MatchResult result)
	{
		next(result.end());
	}
	
	
	private boolean isSegmentEnd(int pos)
	{
		return (pos > segmentStart_) && ((pos == end_ ) || ((pos < end_) && (path_.charAt(pos) == '/')));
	}
	
	
	public Mark mark()
	{
		return new Mark();
	}
	
	
	public class Mark
	{
		private Mark()
		{
			mEnd_		= end_;
			msegStart_	= segmentStart_;
			msegEnd_	= segmentEnd_;
			msegLen_	= segmentLen_;
		}
		
		
		public void revert()
		{
			if (mEnd_ < end_)
			{
				end_ 			= mEnd_;
				segmentStart_	= msegStart_;
				segmentEnd_		= msegEnd_; 
				segmentLen_		= msegLen_;
			}
		}

	
		private int mEnd_;
		private int msegStart_;
		private int msegEnd_;
		private int msegLen_;
	}
	
	
	private int end_;
	private final String path_;
	private int segmentStart_;
	private int segmentEnd_;
	private int segmentLen_;
}
