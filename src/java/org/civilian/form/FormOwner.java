package org.civilian.form;


import org.civilian.request.Request;
import org.civilian.request.RequestProvider;
import org.civilian.response.Response;
import org.civilian.response.ResponseProvider;
import org.civilian.util.Check;


public interface FormOwner extends RequestProvider, ResponseProvider 
{
	public static FormOwner of(RequestProvider reqprov, ResponseProvider resprov)
	{
		Request request = Check.notNull(reqprov, "reqprov").getRequest();
		Response response = Check.notNull(resprov, "resprov").getResponse();
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
