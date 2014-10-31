package !{webPackage}.users.id;


import !{webPackage}.!{pathParamsClass};
import !{webPackage}.users.UsersController;


/**
 * Base class for all controllers below "/users/{userId}".
 */
public abstract class UserController extends UsersController
{
	// add code shared by all derived controllers
	

	public Integer getUserId()
	{
		return getRequest().getPathParam(!{pathParamsClass}.USERID);	
	}
}
