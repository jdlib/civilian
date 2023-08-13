/*
 * Copyright (C) 2014 Civilian Framework.
 *
 * Licensed under the Civilian License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.civilian-framework.org/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package org.civilian.samples.crm.web;


import org.civilian.resource.PathParam;
import org.civilian.resource.PathParamMap;
import org.civilian.resource.PathParams;
import org.civilian.type.TypeLib;


public interface CrmPathParams
{
	public static final PathParam<Integer> CUSTOMERID 		= PathParams.forSegment("customerId").converting(TypeLib.INTEGER);
	public static final PathParam<Integer> OPPORTUNITYID 	= PathParams.forSegment("opportunityId").converting(TypeLib.INTEGER);
	public static final PathParam<Integer> CONTACTID 		= PathParams.forSegment("contactId").converting(TypeLib.INTEGER);
	public static final PathParam<Integer> USERID		 	= PathParams.forSegment("userId").converting(TypeLib.INTEGER);
	public static final PathParamMap MAP 					= new PathParamMap(CrmPathParams.class, CUSTOMERID, OPPORTUNITYID, CONTACTID, USERID);
}
