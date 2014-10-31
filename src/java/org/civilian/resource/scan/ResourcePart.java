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
 * ResExtension describes how an existing resource is extended by a step,
 * either a segment or a path param.
 */
class ResourcePart
{
	public ResourcePart(String segment, String pathAnnotation)
	{
		this(Check.notNull(segment, "segment"), pathAnnotation, null);
	}

	
	public ResourcePart(PathParam<?> pathParam)
	{
		this(null, null, Check.notNull(pathParam, "pathParam"));
	}

	
	private ResourcePart(String segment, String pathAnnotation, PathParam<?> pathParam)
	{
		this.segment 		= segment;
		this.pathAnnotation	= pathAnnotation;
		this.pathParam 		= pathParam;
	}
	
	
	public final String segment;
	public final String pathAnnotation;
	public final PathParam<?> pathParam;
}
