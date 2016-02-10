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
package org.civilian.content;


import org.civilian.util.Check;


/**
 * CombinedContentType is a content type which represents
 * a merger of an acceptable content type of the client and a producible
 * content type of the server. CombinedContentTypes are used by content negotiation
 * to find the best suiting server content type to respond to a client request.<br>
 * The combined content type consists of:
 * <ul>
 * <li>the merged content-types (see below)
 * <li>the quality of the accepted type
 * <li>the quality of the produced type
 * <li>the distance of accepted type and produced type (see below).
 * </ul>   
 * Given two content-types <code>main1/sub1</code> and <code>main2/sub2</code> the merged content-type
 * is <code>merged(main1,main2)/merged(sub1,sub2)</code>. Certain combinations increase
 * the distance of the combined type which is 0,1 or 2 and is a measure how many parts changed
 * in the merger. 
 * <ul>
 * <li><code>merged(&#42;,&#42;) = &#42;</code>   
 * <li><code>merged(&#42;,x) = x</code>, distance++   
 * <li><code>merged(x,&#42;) = x</code>, distance++   
 * <li><code>merged(x,x) = x</code>   
 * <li><code>merged(x,y) = error</code>, combined type does not exist
 * </ul>
 * The score of a combined content-type c is the array
 * { specificity(c), client-quality(c), server-quality(c), 2-distance }.<br>
 * For combined types c1 and c2 we define the absolute order
 * c1 &lt; c2, if exist i in 0..3 with score(c1)[i] &lt; score(c2)[i] and score(c1)[j] == score(c2)[j] for j in 0..(i-1)  
 */
public class CombinedContentType extends ContentType
{
	/**
	 * Returns a CombinedContentType object built from the accepted type (e.g. given via a HTTP Accept-Header)
	 * and a produced type (e.g. given by the @Produces annotation on a controller action method).  
	 * If the content types are not compatible (i.e. their types or subtypes are not compatible)
	 * null is returned.
	 */
	public static CombinedContentType create(ContentType acceptedType, ContentType producedType)
	{
		return negotiate(acceptedType, producedType, null);
	}
	
	
	/**
	 * Returns a CombinedContentType object built from the accepted and produced type, but only if it is higher 
	 * ranked than the actual best combined type. If such a type exists the new combined type is returned, 
	 * else null is returned.
	 */
	public static CombinedContentType negotiate(ContentType acceptedType, ContentType producedType, CombinedContentType actualBest)
	{
		Combined combined = new Combined(acceptedType, producedType);
		if (combined.distance < 0)
			return null; // incompatible 
		
		boolean isBetter = actualBest == null;
		if (!isBetter)
		{
			int combinedSpecificity = getSpecificity(combined.type, combined.subType);
			int actualSpecificity   = actualBest.getSpecificity();
			
			isBetter = combinedSpecificity > actualSpecificity;
			if (!isBetter && (combinedSpecificity == actualSpecificity))
			{
				double qdiff = acceptedType.getQuality() - actualBest.getQuality(); 
				isBetter = qdiff > 0;
				if (!isBetter && (qdiff == 0))
				{
					double qsdiff = producedType.getQuality() - actualBest.getServerQuality(); 
					isBetter = qsdiff > 0;
					if (!isBetter && (qsdiff == 0))
						isBetter = combined.distance < actualBest.getDistance();
				}
			}
		}
		
		return isBetter ? 
			new CombinedContentType(combined.type, combined.subType, acceptedType.getQuality(), producedType.getQuality(), combined.distance) :
			null;
	}
	
	
	/**
	 * Creates a new CombinedContentType.
	 * @param type the type
	 * @param subType the subtype
	 * @param clientQuality the client quality 
	 * @param serverQuality the server quality
	 * @param distance the distance
	 */
	public CombinedContentType(String type, String subType, double clientQuality, double serverQuality, int distance)
	{
		super(type, subType, clientQuality);
		serverQuality_ 	= checkQuality(serverQuality);
		distance_ 		= Check.between(distance, 0, 2, "distance"); 
	}
	
	
	/**
	 * Returns the server quality parameter.
	 */
	public double getServerQuality()
	{
		return serverQuality_;
	}


	/**
	 * Returns the distance of the combined content type. It is the 
	 * the number of type and subtype wildcards replaced with a concrete
	 * value (therefore distance is 0, 1 or 2).
	 */
	public int getDistance()
	{
		return distance_;
	}

	
	static class Combined
	{
		public Combined(ContentType acceptedType, ContentType producedType)
		{
			this.type = combinePart(acceptedType.getMainPart(), producedType.getMainPart());
			if (distance >= 0)
				subType = combinePart(acceptedType.getSubPart(), producedType.getSubPart());
		}
		
		private String combinePart(String part1, String part2)
		{
			if (part1 == null) 
			{
				if (part2 == null)
					return null;
				else
				{
					distance++;
					return part2;
				}
			}
			
			// now: part1 != null
			if (part2 == null)
			{
				distance++;
				return part1;
			}
			
			// now: part1 != null && part2 != null
			if (!part1.equals(part2))
			{
				distance = -1;
				return null;
			}
			
			return part1;
		}
		
		
		String type;
		String subType;
		int distance;
	}


	private double serverQuality_;
	private int distance_;
}
