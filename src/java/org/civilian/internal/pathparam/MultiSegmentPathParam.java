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


import java.util.LinkedList;
import org.civilian.resource.PathParam;
import org.civilian.resource.PathScanner;
import org.civilian.response.UriEncoder;
import org.civilian.util.Check;


public class MultiSegmentPathParam extends PathParam<String[]>
{
	public MultiSegmentPathParam(String name, int minSize)
	{
		super(name);
		minSize_ = Check.greaterEquals(minSize, 0, "minSize");
	}

	
	@SuppressWarnings("unchecked")
	@Override public Class<String[]> getType()
	{
		return (Class<String[]>)PROTOTYPE.getClass();
	}
	

	@Override public String[] parse(PathScanner scanner)
	{
		LinkedList<String> list = new LinkedList<>();
		int pos = scanner.getPosition();
		while(scanner.hasMore())
		{
			list.add(scanner.getSegment());
			scanner.next();
		}
		if (list.size() >= minSize_)
			return list.toArray(new String[list.size()]);
		else
		{
			// revert scanner
			scanner.setPosition(pos);
			return null;
		}
	}
	

	@Override public void buildPath(String[] value, UriEncoder encoder, StringBuilder path)
	{
		for (String part : value)
			buildPathSegment(part, encoder, path); 
	}
	

	@Override protected String getPatternString()
	{
		return "minSize=" + minSize_;
	}
	
	
	private int minSize_; 
	private static final String[] PROTOTYPE = new String[0];
}
