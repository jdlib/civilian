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


import java.util.regex.Pattern;


/**
 * SegmentWcPPFormat is a special String-typed RegexPathParam
 * which is defined with a pattern string, containing a wildcard '*'. 
 */
public class SegmentWcPathParam extends RegexPathParam
{
	/**
	 * Creates a SegmentWcPathParam.
	 * @param name the param name
	 * @param buildPattern a string describing the segment. It must contain
	 * 		a single wildcard '*' which represents the variable part of the segment.
	 */
	public SegmentWcPathParam(String name, String buildPattern)
	{
		super(name, Pattern.compile(buildPattern.replace("*", "([^/]+)")), buildPattern);						
	}

	
	/**
	 * Returns the originaö build pattern passed to the constructor. 
	 */
	@Override protected String getPatternString()
	{
		return buildPattern_;
	}
}
