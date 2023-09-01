package org.civilian.samples.async;


import java.io.PrintWriter;
import org.civilian.annotation.Get;
import org.civilian.annotation.Produces;
import org.civilian.content.ContentType;
import org.civilian.controller.Controller;
import org.civilian.response.AsyncContext;


public class RunnableController extends Controller 
{
	@Get @Produces(ContentType.Strings.TEXT_PLAIN) 
	public void get()
	{
		String origThread = threadName();
		AsyncContext async = getResponse().startAsync();
		async.start(() -> {
			PrintWriter out = async.getResponse().getContentWriter();

			out.println("calling AsyncContext.start(Runnable):");
			out.println("orig thread " + origThread);
			out.println("cur  thread " + threadName());
			
			async.complete();
		});
	}
	
	
	private static String threadName()
	{
		return Thread.currentThread().getName();
	}
}
