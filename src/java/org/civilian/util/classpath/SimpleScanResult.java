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
package org.civilian.internal.classpath;


import java.util.HashSet;
import java.util.Set;


public class SimpleScanResult implements ScanResult
{
	public SimpleScanResult(ClassFilter filter)
	{
		filter_ = filter;
	}

	
	@Override public void scanned(String className)
	{
		if ((filter_ == null) || filter_.accept(className))
			classes_.add(className);
	}
	

	public Set<String> getClasses()
	{
		return classes_;
	}
	
	
	private final Set<String> classes_ = new HashSet<>();
	private final ClassFilter filter_;
}
