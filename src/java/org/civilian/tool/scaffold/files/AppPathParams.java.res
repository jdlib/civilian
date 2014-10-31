package !{webPackage};


import org.civilian.resource.PathParam;
import org.civilian.resource.PathParams;
import org.civilian.resource.PathParamMap;


/**
 * Contains the path parameters of application !{appClass}.
 */ 
public interface !{pathParamsClass}
{
	public static final PathParamMap PARAMS             = new PathParamMap(!{pathParamsClass}.class);
	
	// define your path params here and seal the map when adding the last 
	public static final PathParam<Integer> CUSTOMERID	= PARAMS.add(PathParams.forIntSegment("customerId"));
	public static final PathParam<Integer> USERID       = PARAMS.addAndSeal(PathParams.forIntSegment("userId"));
}
