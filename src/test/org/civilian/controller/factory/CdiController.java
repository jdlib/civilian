package org.civilian.controller.factory;


import javax.inject.Inject;
import org.civilian.controller.Controller;


public class CdiController extends Controller
{
	@Inject public String service;
}
