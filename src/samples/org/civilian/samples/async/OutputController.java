package org.civilian.samples.async;


import org.civilian.annotation.Get;
import org.civilian.controller.Controller;
import org.civilian.response.AsyncContext;


public class OutputController extends Controller 
{
	@Get
	public void get()
	{
		@SuppressWarnings("unused")
		AsyncContext context = getResponse().startAsync();
	}
}
