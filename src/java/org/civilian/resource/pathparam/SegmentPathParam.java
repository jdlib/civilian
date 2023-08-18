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
package org.civilian.resource.pathparam;


import org.civilian.util.PathScanner;
import org.civilian.util.http.UriEncoder;


/**
 * SegmentPathParam is a PathParam which matches a single segment
 * of a path. The value of the associated path parameter is the segment,
 * converted to the type of the SegmentPathParam.  
 */
public class SegmentPathParam extends PathParam<String>
{
	/**
	 * Creates a new SegmentPathParam.
	 */
	public SegmentPathParam(String name)
	{
		super(name);
	}


	@Override public Class<String> getType()
	{
		return String.class;
	}

	
	@Override public String parse(PathScanner scanner)
	{
		return scanner.consumeSegment();
	}


	@Override public void buildPath(String value, UriEncoder encoder, StringBuilder path)
	{
		buildPathSegment(value, encoder, path);
	}

	
	@Override protected String getPatternString()
	{
		return "/<segment>";
	}
}
