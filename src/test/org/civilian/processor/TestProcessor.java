package org.civilian.processor;


import org.civilian.request.Request;
import org.civilian.response.Response;


public class TestProcessor extends Processor
{
	@Override public String getInfo() 
	{
		return getClass().getSimpleName();
	}

	
	@Override public boolean process(Request request, Response response, ProcessorChain chain)
		throws Exception
	{
		return false;
	}
}
