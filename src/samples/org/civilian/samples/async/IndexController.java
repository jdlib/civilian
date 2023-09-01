package org.civilian.samples.async;


import org.civilian.annotation.Get;
import org.civilian.annotation.Produces;
import org.civilian.content.ContentType;
import org.civilian.controller.Controller;


public class IndexController extends Controller 
{
	@Get @Produces(ContentType.Strings.TEXT_HTML) 
	public IndexTemplate get()
	{
		return new IndexTemplate();
	}
}
