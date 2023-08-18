package org.civilian.exchange;


import org.civilian.request.Request;
import org.civilian.request.RequestProvider;
import org.civilian.response.Response;
import org.civilian.response.ResponseProvider;
import org.civilian.util.Check;


/**
 * A service which can provide a Request and Response.
 */
public interface RequestResponseProvider extends RequestProvider, ResponseProvider 
{
	public static RequestResponseProvider of(Request request, Response response)
	{
		Check.notNull(request, "request");
		Check.notNull(response, "response");
		return new RequestResponseProvider() {
			@Override public Request getRequest() 
			{
				return request;
			}
			

			@Override public Response getResponse() 
			{
				return response;
			}};
	}
}
