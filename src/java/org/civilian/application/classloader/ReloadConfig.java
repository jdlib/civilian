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
package org.civilian.application.classloader;


import java.util.function.Predicate;


/**
 * ReloadConfig controls which classes are included or excluded
 * from class reloading.
 */
public class ReloadConfig implements Predicate<String>
{
	public ClassList includes()
	{
		return includes_;
	}
	
	
	public ClassList excludes()
	{
		return excludes_;
	}
	
	
	public boolean isValid() 
	{
		return includes_.size() > 0;
	}

	
	@Override public boolean test(String name) 
	{
		if (excludes_.contains(name))
			return false;
		else 
			return includes_.contains(name); 
	}

		
	private final ClassList excludes_ = new ClassList();
	private final ClassList includes_ = new ClassList();
}
