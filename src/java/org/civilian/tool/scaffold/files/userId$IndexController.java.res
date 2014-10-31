package !{webPackage}.users.id;


import org.civilian.annotation.Get;
import org.civilian.annotation.Produces;


/**
 * Controller for the resource "/users/{userId}".
 */
public class IndexController extends UserController
{
	@Get @Produces("text/html") public void render() throws Exception
	{
		getResponse().writeText("the user id is: " + getUserId());
	} 
}
