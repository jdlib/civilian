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


import java.lang.reflect.AnnotatedElement;
import org.civilian.resource.pathparam.PathParam;
import org.civilian.resource.pathparam.PathParamMap;
import org.civilian.util.Check;
import org.civilian.util.StringUtil;


/**
 * ResourceExt describes the extension step of an existing resource,
 * either by a segment or by a path param.
 */
class ResourceExt
{
	public static class Factory
	{
		public Factory(PathParamMap pathParams)
		{
			pathParams_ = Check.notNull(pathParams, "pathParams");
		}
		
		
		/**
		 * Reads @PathParam and @Segment annotations for package-info and controller classes.
		 * @param segment the default segment derived from the last package part or 
		 * 		the simple name of the controller class. 
		 */
		public ResourceExt getExtension(AnnotatedElement annotated, String defaultSegment, boolean allowEmptySegment)
		{
			PathParam<?> pp = null;
			String segment 	= null;
			
			if (annotated != null)
			{
				pp = extractPathParam(annotated);
				segment = extractSegment(annotated, allowEmptySegment);
				
				if ((pp != null) && (segment != null))
					throw new ScanException(annotated + ": cannot specify both @PathParam and @Segment annotations");
			}
			
			if (pp != null)
				return new ResourceExt(null, pp);
			else if (segment != null)
				return new ResourceExt(segment, null);
			else if (defaultSegment != null)
				return new ResourceExt(defaultSegment, null);
			else
				return null;
		}
		
		
		/**
		 * Evaluate @PathParam.
		 */
		private PathParam<?> extractPathParam(AnnotatedElement annotated)
		{
			PathParam<?> pp = null;
			org.civilian.annotation.PathParam aParam = annotated.getAnnotation(org.civilian.annotation.PathParam.class);
			if (aParam != null)
			{
				pp = pathParams_.get(aParam.value());
				if (pp == null)
					throw new ScanException(annotated + ": annotation @PathParam specifies unknown path parameter '" + aParam.value() + "'");
			}
			return pp;
		}
		
		
		/**
		 * Evaluate @Segment.
		 */
		private String extractSegment(AnnotatedElement annotated, boolean allowEmptySegment)
		{
			String segment = null;
			org.civilian.annotation.Segment aSegment = annotated.getAnnotation(org.civilian.annotation.Segment.class);
			if (aSegment != null)
			{
				segment = aSegment.value().trim();
				segment = StringUtil.cutLeft(segment, "/");
				segment = StringUtil.cutRight(segment, "/").trim();
				if (StringUtil.isBlank(segment))
				{
					if (!allowEmptySegment)
						segmentError(annotated,  aSegment, "results in an empy segment");
					segment = null;
				}
				else if (segment.indexOf('/') >= 0)
					segmentError(annotated, aSegment, "may not contain a '/' character");
			}
			return segment;
		}
		
		
		private void segmentError(AnnotatedElement annotated, org.civilian.annotation.Segment aSegment, String msg)
		{
			throw new ScanException(annotated + ": @Segment annotation '" + aSegment.value() + "' " + msg);
		}
		
		
		private final PathParamMap pathParams_;
	}
	
	
	private ResourceExt(String segment, PathParam<?> pathParam)
	{
		this.segment 	= segment;
		this.pathParam 	= pathParam;
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
	public final PathParam<?> pathParam;
}
