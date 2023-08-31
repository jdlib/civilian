package org.civilian.response;


import java.io.IOException;
import java.io.OutputStream;


/**
 * AsyncWriteListener is notified when it is possible to
 * write to a async response without blocking.  
 */
public interface AsyncWriteListener 
{
    /**
     * This method will be called by the when it is possible
     * to write data to the outputstream of a async response.
     * @throws IOException if an I/O related error has occurred
     */
    public void onWritePossible(OutputStream out) throws IOException;
    

    /**
     * Invoked when an error occurs writing data using the non-blocking APIs.
     * @param error a throwable
     */
    public void onError(Throwable error);
}
