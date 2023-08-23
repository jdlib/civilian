package org.civilian.controller;


import org.civilian.resource.Resource;
import org.civilian.testcase1.AlphaController;
import org.junit.Test;


public class ControllerResourceDataTest 
{
	@Test public void testTouch() throws Exception
	{
		Resource root = new Resource();
		root.setData(new ControllerResourceData(new ControllerSignature(AlphaController.class)));
		
		new Resource(root, "a");
		
		ControllerResourceData.touchControllerClasses(root);
	}
}
