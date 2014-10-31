package !{webPackage};


import org.civilian.Controller;


/**
 * Common base class for all !{appPrefix} controllers.
 */
public abstract class !{appController} extends Controller implements !{resourcesClass} 
{
	/**
	 * Casts the application to the !{appClass} class.
	 */
	public !{appClass} get!{appClass}()
	{
		return (!{appClass})getApplication();
	}
}
