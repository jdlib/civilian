package org.civilian.processor;


import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.civilian.CivTest;
import org.civilian.controller.Controller;
import org.civilian.controller.ControllerSignature;
import org.civilian.controller.ControllerType;
import org.civilian.controller.ControllerTypeProvider;
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
		Request request 	= mock(Request.class);
		Response response	= mock(Response.class);
		Resource idSegment	= mock(Resource.class);
		Resource idPP		= mock(Resource.class);

		when(root.getData()).thenReturn(null);
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

		// complete match, with path params
		Map<PathParam<?>,Object> pathParams = new LinkedHashMap<>();
		when(root.match("/id")).thenReturn(new Resource.Match(idPP, true, pathParams));
		assertFalse(dispatch.process(request, response, ProcessorChain.EMPTY));
		
		// complete match, with path params and controllerType
		ControllerType type  = mock(ControllerType.class);
		Controller controller = mock(Controller.class);
		ControllerSignature sig = new ControllerSignature(controller.getClass());
		ControllerTypeProvider tp = () -> type; 
		sig.setData(tp);
		when(idPP.getData()).thenReturn(sig);
		when(type.createController()).thenReturn(controller);
		
		assertTrue(dispatch.process(request, response, ProcessorChain.EMPTY));
		verify(request).setResource(idPP);
		verify(request).setPathParams(pathParams);
		verify(controller).process(request, response);
	}
}
