package org.civilian.processor;


import org.civilian.Processor;
import org.civilian.Request;


public class TestProcessor extends Processor
{
	@Override public boolean process(Request request, ProcessorChain chain)
		throws Exception
	{
		return false;
	}
}
