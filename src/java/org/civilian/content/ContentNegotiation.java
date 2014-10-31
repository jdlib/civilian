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


/**
 * ContentNegotation implements the content negotiation algorithm.
 * From a list of content-types acceptable by the client (accepted types)
 * and for a list of content-types producible by the server
 * (produced types), it returns the best fitting content-type.
 * Both client and server types can contain wildcards and specify a
 * quality parameter indicating their preference to accept or produce the type.
 * <p>
 * For all pairs (accepted type, produced type) the {@link CombinedContentType} 
 * is built, and given a score.  
 * The best combined content type determines the resulting content type,
 * (which may be null, if accepted and produced types are incompatible).
 */
public class ContentNegotiation
{
	/**
	 * Creates a ContentNegotation object.
	 * @param acceptedTypes the list of content types
	 * 		accepted by the client
	 */
	public ContentNegotiation(ContentTypeList acceptedTypes)
	{
		acceptedTypes_ = acceptedTypes;
	}
	
	
	/**
	 * Creates a ContentNegotation object.
	 * @param acceptedTypes the list of content types
	 * 		accepted by the client
	 */
	public ContentNegotiation(ContentType... acceptedTypes)
	{
		this(new ContentTypeList(acceptedTypes));
	}
	
	
	/**
	 * Evaluates if the ContentType is a suitable content type.
	 * @param produced a ContentType which can be produced by the server. 
	 * @return true if the ContentType is suitable for the client and is 
	 *		better than the previous best content type.
	 *		If true is returned, then {@link #bestType} contains the
	 *		CombinedContentType which should be used by the server
	 *		to produce the response.
	 */
	public boolean evaluate(ContentType produced)
	{
		int n = acceptedTypes_.size();
		for (int i=0; i<n; i++)
		{
			ContentType accepted = acceptedTypes_.get(i);
			if (test(accepted, produced))
				return true;
		}
		return false;
	}
	
	
	/**
	 * Evaluates if one of the content types in the given list
	 * is a suitable content type and finds the best matching type. 
	 * @param produced a list of ContentTypes which can be produced by the server. 
	 * @return true if one ContentType is suitable for the client and is 
	 *		better than the previous best content type.
	 *		If true is returned, then {@link #bestType} contains the
	 *		CombinedContentType which should be used by the server
	 *		to produce the response.
	 */
	public boolean evaluate(ContentTypeList produced)
	{
		boolean result = false;
		int n = produced.size();
		for (int i=0; i<n; i++)
		{
			if (evaluate(produced.get(i)))
				result = true;
		}
		return result;
	}
	
	
	private boolean test(ContentType accepted, ContentType produced)
	{
		CombinedContentType candidate = CombinedContentType.negotiate(accepted, produced, bestType);
		if (candidate != null)
		{
			bestType = candidate;
			return true;
		}
		else
			return false;
	}
	
	
	private ContentTypeList acceptedTypes_;
	public CombinedContentType bestType;
}
