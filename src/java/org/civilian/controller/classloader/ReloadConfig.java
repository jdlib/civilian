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
package org.civilian.controller.classloader;


/**
 * ReloadConfig controls which classes are included or excluded
 * from class reloading.
 */
public class ReloadConfig
{
	public ClassLoader createClassLoader()
	{
		return new NonDelegatingClassLoader(getClass().getClassLoader(), includes(), excludes());
	}
	
	
	public ClassList includes()
	{
		return includes_;
	}
	
	
	public ClassList excludes()
	{
		return excludes_;
	}

	
	private ClassList excludes_ = new ClassList();
	private ClassList includes_ = new ClassList();
}
