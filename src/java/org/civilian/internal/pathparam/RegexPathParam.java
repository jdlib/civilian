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


import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.civilian.resource.PathParam;
import org.civilian.resource.PathScanner;
import org.civilian.response.UriEncoder;
import org.civilian.util.Check;


/**
 * A PathParam based on a regular expression.
 */
public class RegexPathParam extends PathParam<String>
{
	/**
	 * Creates a new RegexPPFormat.
	 * @param type the type of the path parameter values defined by this pattern. 
	 * @param matchPattern a regex pattern used to implement the accept method. The pattern must
	 * 		contain exactly one match group and the value of the match group must be
	 * 		the string value of path parameter.
	 * @param buildPattern a build pattern used to implement the buildPath method. It must contain
	 * 		exactly one "*" which is replaced by the stringified path parameter value when building
	 * 		a path.
	 */
	public RegexPathParam(String name, Pattern matchPattern, String buildPattern)
	{
		super(name);
		matchPattern_ = Check.notNull(matchPattern, "matchPattern");
		buildPattern_ = Check.notNull(buildPattern, "buildPattern");
	}

	
	@Override public Class<String> getType()
	{
		return String.class;
	}

	
	/**
	 * Tests if the regex matches the begin of the path as given by the scanner.
	 * If it matches, then the first result group of the regex will be used
	 * as value of the path parameter.
	 */
	@Override public String parse(PathScanner scanner)
	{
		String value = null;
		
		MatchResult result = scanner.matchPattern(matchPattern_);
		if (result != null)
		{
			value = result.group(1);
			if (value != null)
			{
				// pattern matched: advance the scanner
				scanner.next(result);
			}
		}
		return value;
	}


	/**
	 * Builds a string path for the path parameter, given the value of a path parameter.
	 */
	@Override public void buildPath(String value, UriEncoder encoder, StringBuilder path)
	{
		String segment = buildPattern_.replace("*", value);
		buildPathSegment(segment, encoder, path);
	}
	
	
	@Override protected String getPatternString()
	{
		return matchPattern_.toString();
	}

	
	protected final Pattern matchPattern_;
	protected final String buildPattern_;
}
