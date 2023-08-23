package org.civilian.controller;


import org.civilian.server.Server;
import org.civilian.text.service.LocaleServiceList;


/**
 * ControllerOwner provides context services to a Controller.
 */
public interface ControllerOwner 
{
	public Server getServer();
	
	
	public LocaleServiceList getLocaleServices();
}
