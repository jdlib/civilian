package !{webPackage};


import org.civilian.annotation.Get;
import org.civilian.annotation.Produces;


/**
 * Controller for the resource "/".
 */
public class IndexController extends !{appController}
{
	@Get @Produces("text/html") 
	public void renderHtml() throws Exception
	{
		getResponse().writeTemplate(new IndexTemplate());
	} 
}
