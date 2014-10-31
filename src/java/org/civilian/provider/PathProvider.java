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
package org.civilian.provider;


import org.civilian.resource.Path;


/**
 * A PathProvider is an object which is associated
 * with a path from the server root.
 */
public interface PathProvider 
{
	/**
	 * Returns the path from the server root to the resource. 
	 */
	public Path getPath();

	
	/**
	 * Returns a path from an ancestor resource to this resource.
	 * The ancestor resource depends on the implementation. 
	 */
	public Path getRelativePath();
}
