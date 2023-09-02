package org.civilian.controller;


import org.civilian.resource.Resource;
import org.civilian.testcase1.AlphaController;
import org.junit.Test;


public class ControllerTypeProviderTest 
{
	@Test public void testTouch() throws Exception
	{
		Resource root = new Resource();
		root.setData(new ControllerSignature(AlphaController.class));
		
		new Resource(root, "a");
		
		ControllerTypeProvider.touchControllerClasses(root);
	}
}
