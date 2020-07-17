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
package org.civilian.resource.scan;


import org.civilian.resource.PathParam;
import org.civilian.util.Check;


/**
 * ResourceExt describes the extension step of an existing resource,
 * either by a segment or by a path param.
 */
class ResourceExt
{
	/**
	 * Creates a ResourceExt for a URL segment.
	 * @param segment the segment
	 * @param pathAnnotation optional: the pathAnnotation present on the segment
	 */
	public ResourceExt(String segment, String pathAnnotation)
	{
		this(Check.notNull(segment, "segment"), pathAnnotation, null);
	}

	
	/**
	 * Creates a ResourceExt for a PathParam.
	 * @param pathParam the PathParam
	 */
	public ResourceExt(PathParam<?> pathParam)
	{
		this(null, null, Check.notNull(pathParam, "pathParam"));
	}

	
	private ResourceExt(String segment, String pathAnnotation, PathParam<?> pathParam)
	{
		this.segment 		= segment;
		this.pathAnnotation	= pathAnnotation;
		this.pathParam 		= pathParam;
	}
	
	
	@Override public int hashCode()
	{
		return pathParam != null ? pathParam.hashCode() : segment.hashCode();
	}
	
	
	@Override public boolean equals(Object other)
	{
		if (other instanceof ResourceExt)
		{
			ResourceExt o = (ResourceExt)other;
			return pathParam != null ? pathParam == o.pathParam : segment.equals(o.segment); 
		}
		else
			return false;
	}
	
	
	public final String segment;
	public final String pathAnnotation;
	public final PathParam<?> pathParam;
}
