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
package org.civilian.testcase1;


import org.civilian.resource.PathParam;
import org.civilian.resource.PathParamMap;
import org.civilian.resource.PathParams;


public class Test1PathParams
{
	public static final PathParamMap MAP = new PathParamMap(Test1PathParams.class);
	
	public static final PathParam<String> BETA 		= MAP.add(PathParams.forSegment("beta")); 
	public static final PathParam<Integer> GAMMA 	= MAP.addAndSeal(PathParams.forIntSegment("gammaId")); 
}
