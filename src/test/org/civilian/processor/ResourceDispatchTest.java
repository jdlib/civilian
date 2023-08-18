package org.civilian.processor;


import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.civilian.CivTest;
import org.civilian.controller.Controller;
import org.civilian.controller.ControllerService;
import org.civilian.controller.ControllerType;
import org.civilian.request.Request;
import org.civilian.resource.Path;
import org.civilian.resource.Resource;
import org.civilian.resource.pathparam.PathParam;
import org.civilian.response.Response;


public class ResourceDispatchTest extends CivTest
{
	@Test public void test() throws Exception
	{
		Resource root 		= mock(Resource.class);
		Resource.Tree tree 	= mock(Resource.Tree.class);
		Request request 	= mock(Request.class);
		Response response	= mock(Response.class);
		Resource idSegment	= mock(Resource.class);
		Resource idPP		= mock(Resource.class);

		when(root.getControllerType()).thenReturn(null);
		when(root.getTree()).thenReturn(tree);
		when(tree.getControllerService()).thenReturn(mock(ControllerService.class));
		ResourceDispatch dispatch = new ResourceDispatch(root);

		// request path is "/id" during the test
		when(request.getRelativePath()).thenReturn(new Path("/id"));
		
		// incomplete match
		when(root.match("/id")).thenReturn(new Resource.Match(root, false, null));
		assertFalse(dispatch.process(request, response, ProcessorChain.EMPTY));
		verify(request, times(0)).setResource(root);

		// complete match, no path params
		when(root.match("/id")).thenReturn(new Resource.Match(idSegment, true, null));
		assertFalse(dispatch.process(request, response, ProcessorChain.EMPTY));
		verify(request).setResource(idSegment);
		verify(request).setPathParams(null);

		// complete match, with path params
		Map<PathParam<?>,Object> pathParams = new LinkedHashMap<>();
		when(root.match("/id")).thenReturn(new Resource.Match(idPP, true, pathParams));
		assertFalse(dispatch.process(request, response, ProcessorChain.EMPTY));
		verify(request).setResource(idPP);
		verify(request).setPathParams(pathParams);
		
		// complete match, with path params and controllerType
		ControllerType type  = mock(ControllerType.class);
		Controller controller = mock(Controller.class);
		when(idPP.getControllerType()).thenReturn(type);
		when(type.createController()).thenReturn(controller);
		
		assertTrue(dispatch.process(request, response, ProcessorChain.EMPTY));
		verify(request, times(2)).setResource(idPP);
		verify(request, times(2)).setPathParams(pathParams);
		verify(controller).process(request, response);
	}
}
