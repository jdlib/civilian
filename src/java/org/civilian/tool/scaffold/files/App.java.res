package !{webPackage};


import org.civilian.Application;
import org.civilian.application.AppConfig;


/**
 * The application class.
 */ 
public class !{appClass} extends Application
{
	/**
	 * Required default constructor.
	 */
	public !{appClass}()
	{
		super(!{pathParamsClass}.PARAMS, null /* default controller package */);
	}
	
	
	/**
	 * Initializes the application.
	 */
	@Override protected void init(AppConfig config) throws Exception
	{
		// register the generated resource root
		config.setResourceRoot(!{resourcesClass}.root);
	}	
	
	
	/**
	 * Closes all application resources.
	 */
	@Override protected void close() throws Exception
	{
	}	
}