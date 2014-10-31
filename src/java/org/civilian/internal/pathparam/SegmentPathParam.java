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
package org.civilian.internal.pathparam;


import org.civilian.resource.PathScanner;
import org.civilian.response.UriEncoder;
import org.civilian.type.Type;


/**
 * SegmentPathParam is a PathParam which matches a single segment
 * of a path. The value of the associated path parameter is the segment,
 * converted to the type of the SegmentPathParam.  
 */
public class SegmentPathParam<T> extends TypeBasedPathParam<T>
{
	/**
	 * Creates a new SegmentPathParam.
	 */
	public SegmentPathParam(String name, Type<T> type)
	{
		super(name, type);
	}

	
	@Override public T parse(PathScanner scanner)
	{
		// try to parse the current scanner segment 
		T value = parse(scanner.getSegment());
		if (value != null)
		{
			scanner.next(); // success: consume the segment
			return value;
		}
		else
			return null; // invalid for our type
	}


	@Override public void buildPath(T value, UriEncoder encoder, StringBuilder path)
	{
		buildPathSegment(format(value), encoder, path);
	}

	
	@Override protected String getPatternString()
	{
		return null;
	}
}
