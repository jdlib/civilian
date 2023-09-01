package org.civilian.response;


import java.io.IOException;


/**
 * AsyncOutputListener is notified when it is possible to
 * write to a async output without blocking.  
 */
public interface AsyncOutputListener 
{
    /**
     * This method will be called when it is possible
     * to write data to the outputstream of a async response.
     * @throws IOException if an I/O related error has occurred
     */
    public void onOutputPossible() throws IOException;
    

    /**
     * Invoked when an error occurs writing data using the non-blocking APIs.
     * @param error a throwable
     */
    public void onOutputError(Throwable error);
}
