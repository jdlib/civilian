package !{webPackage};


import org.civilian.resource.PathParam;
import org.civilian.resource.PathParams;
import org.civilian.resource.PathParamMap;


/**
 * Contains the path parameters of application !{appClass}.
 */ 
public interface !{pathParamsClass}
{
	// define your path params here 
	public static final PathParam<Integer> CUSTOMERID	= PathParams.forSegment("customerId").converting(TypeLib.INTEGER);
	public static final PathParam<Integer> USERID       = PathParams.forSegment("userId").converting(TypeLib.INTEGER);

	// add all to map
	public static final PathParamMap PARAMS             = new PathParamMap(QsPathParams.class, CUSTOMERID, USERID);
}
