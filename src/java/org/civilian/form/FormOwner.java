package org.civilian.form;


import org.civilian.request.Request;
import org.civilian.request.RequestProvider;
import org.civilian.response.Response;
import org.civilian.response.ResponseProvider;
import org.civilian.util.Check;


public interface FormOwner extends RequestProvider, ResponseProvider 
{
	public static FormOwner of(Request request, Response response)
	{
		Check.notNull(request, "request");
		Check.notNull(response, "response");
		return new FormOwner() {
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
