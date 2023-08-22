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
package org.civilian.controller.scan;


class ControllerPackage
{
	public final String name;
	public final ResourceInfo resInfo;
	public final ControllerPackage parent;

	
	public ControllerPackage(ResourceInfo rootInfo, String name)
	{
		this.name 		= name;
		this.resInfo	= rootInfo;
		this.parent 	= null;
		rootInfo.setPackage(this);
	}
	
	
	public ControllerPackage(ControllerPackage parent, String name, ResourceExt ext)
	{
		this.name 		= name;
		this.parent 	= parent;
		this.resInfo 	= parent.resInfo.getChild(ext);
		this.resInfo.setPackage(this);
	}
	
	
	@Override public String toString()
	{
		return name;
	}
}
