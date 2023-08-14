package org.civilian.processor;


import org.civilian.request.Request;


public class TestProcessor extends Processor
{
	@Override public String getInfo() 
	{
		return getClass().getSimpleName();
	}

	
	@Override public boolean process(Request request, ProcessorChain chain)
		throws Exception
	{
		return false;
	}
}
