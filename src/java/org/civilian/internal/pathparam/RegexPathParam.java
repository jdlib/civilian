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
import org.civilian.resource.PathScanner;
import org.civilian.response.UriEncoder;
import org.civilian.type.Type;
import org.civilian.util.Check;


/**
 * A PathParam based on a regular expression.
 */
public class RegexPathParam<T> extends TypeBasedPathParam<T>
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
	public RegexPathParam(String name, Type<T> type, Pattern matchPattern, String buildPattern)
	{
		super(name, type);
		matchPattern_ = Check.notNull(matchPattern, "matchPattern");
		buildPattern_ = Check.notNull(buildPattern, "buildPattern");
	}

	
	/**
	 * Tests if the regex matches the begin of the path as given by the scanner.
	 * If it matches, then the first result group of the regex will be used
	 * as value of the path parameter.
	 */
	@Override public T parse(PathScanner scanner)
	{
		MatchResult result = scanner.matchPattern(matchPattern_);
		if (result != null)
		{
			T value = parse(result.group(1));
			if (value != null)
			{
				// a) pattern matched
				// b) value was a valid instance of our type 
				// now advance the scanner
				scanner.next(result);
				return value;
			}
		}
		// did not match, or was invalid for our type
		return null;
	}


	/**
	 * Builds a string path for the path parameter, given the value of a path parameter.
	 */
	@Override public void buildPath(T value, UriEncoder encoder, StringBuilder path)
	{
		String segment = buildPattern_.replace("*", format(value));
		buildPathSegment(segment, encoder, path);
	}
	
	
	@Override protected String getPatternString()
	{
		return matchPattern_.toString();
	}

	
	protected Pattern matchPattern_;
	protected String buildPattern_;
}
