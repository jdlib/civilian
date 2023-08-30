package org.civilian.request;


import java.util.Iterator;
import java.util.Map;
import org.civilian.application.Application;
import org.civilian.util.Check;


/**
 * Uploads represents the uploads associated with a request.
 */
public interface Uploads 
{
	/**
	 * An empty Upload objects.
	 */
	public static final Uploads EMPTY = of(Map.of());
	
	
	/**
	 * Returns a Uploads object which serves the uploads contained in the given map.
	 * @param map mapping Upload name to Upload objects
	 * @return the new Uploads object
	 */
	public static Uploads of(Map<String,Upload[]> map)
	{
		return new UploadsImpl(map, null);
	}
	
	
	public static Uploads of(Upload upload)
	{
		return of(upload != null ? Map.of(upload.getName(), new Upload[] { upload }) : null);
	}

	
	/**
	 * Returns a Uploads object for the error occured during upload.
	 * @param error the error
	 * @return the new Uploads object
	 */
	public static Uploads of(Exception error)
	{
		return new UploadsImpl(null, Check.notNull(error, "error"));
	}

	
	/**
	 * @returns the number of Uploads.
	 */
	public int size();

	
	/**
	 * @return if the request contains uploads.
	 */
	public default boolean isEmpty()
	{
		return size() == 0;
	}

	
	/**
	 * Returns the first Upload object with the given name.
	 * In Servlet terms the Upload object corresponds to a javax.servlet.http.Part object 
	 * in a multipart/form-data request whose content disposition contains
	 * a filename parameter and whose name equals the given name.
	 * @return the upload or null
	 */
	public Upload get(String name);

	
	/**
	 * Returns all Upload objects with the given name.
	 * In Servlet terms the Upload object corresponds to a javax.servlet.http.Part object 
	 * in a multipart/form-data request whose content disposition contains
	 * a filename parameter and whose name equals the given name.
	 * @return the uploads
	 */
	public Upload[] getAll(String name);

	
	/**
	 * Returns an exception if the request contains uploaded files and
	 * one ore more files violated constraints defined by the {@link Application#getUploadConfig() UploadConfig}.
	 * In this case the request parameters may not be properly initialized.
	 * Therefore for upload requests you should check upload errors first, before you evaluate request parameters.
	 * @return the upload error  
	 */
	public Exception error();

	
	/**
	 * Returns an iterator for all Upload names.
	 * @see #get(String)
	 */
	public Iterator<String> names();
}


class UploadsImpl implements Uploads
{
	public UploadsImpl(Map<String,Upload[]> map, Exception error)
	{
		map_ 	= map != null ? map : Map.of();
		error_ 	= error;
	}
	

	@Override public int size() 
	{
		return map_.size();
	}

	
	@Override public Upload get(String name) 
	{
		Upload[] uploads = getAll(name);
		return uploads.length > 0 ? uploads[0] : null;
	}
	

	@Override public Upload[] getAll(String name) 
	{
		Upload[] uploads = map_.get(name);
		return uploads != null ? uploads : Upload.EMPTY_LIST;
	}
	

	@Override public Iterator<String> names() 
	{
		return map_.keySet().iterator();
	}
	
	
	@Override public Exception error() 
	{
		return error_;
	}


	private final Map<String,Upload[]> map_;
	private final Exception error_;
}