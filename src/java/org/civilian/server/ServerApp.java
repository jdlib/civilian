package org.civilian.server;


import org.civilian.resource.Path;
import org.civilian.resource.PathProvider;


/**
 * ServerApp represents an app running in a {@link Server}.
 */
public abstract class ServerApp implements PathProvider 
{
	/**
	 * Lifecycle status of the Application. 
	 */
	public enum Status
	{
		/**
		 * The application was created, but not yet initialized.
		 */
		CREATED,
		
		/**
		 * The application was successfully initialized and is now running.
		 */
		RUNNING,
		
		/**
		 * The application threw an error during initialization.
		 */
		ERROR,
		
		/**
		 * The application was closed.
		 */
		CLOSED
	}

	
	/**
	 * @return the application status.
	 */
	public abstract Status getStatus();

	
	/**
	 * Returns the application id.
	 * The id is used to identify the application within
	 * the {@link Server}. The id was defined within
	 * <code>civilian.ini</code>.
	 * @see Server#getApplication(String) 
	 * @return the id
	 */
	public abstract String getId();
	
	
	/**
	 * @return the path from the server root to the application.
	 */
	@Override public abstract Path getPath();
	
	
	/**
	 * @return the relative path from the server to the application.
	 */
	public abstract Path getRelativePath();

	
	/**
	 * @return the server in which the application is running.
	 */
	public abstract Server getServer();
	
	
	/**
	 * Stores an attribute under the given name in the application.
	 * @param name the attribute name 
	 * @param value the attribute value 
	 */
	public abstract void setAttribute(String name, Object value);

	
	/**
	 * Returns an attribute which was previously associated with
	 * the application.
	 * @param name the attribute name
	 * @return the attribute value
	 * @see #setAttribute(String, Object)
	 */
	public abstract Object getAttribute(String name);

	
	/**
	 * Closes the application. Called when the server shuts down
	 * or the application is removed from the server.
	 * @throws Exception if an exception occurs
	 */
	protected abstract void runClose() throws Exception;
	
	
	protected abstract void init(Server.AppInitData data);
}
