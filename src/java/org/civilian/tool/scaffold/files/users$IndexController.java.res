package !{webPackage}.users;


import org.civilian.annotation.Get;
import org.civilian.annotation.Produces;


/**
 * Controller for the resource "/users".
 */
public class IndexController extends UsersController
{
	@Get @Produces("text/html") public void render() throws Exception
	{
		getResponse().writeText("list users...");
	} 
}
