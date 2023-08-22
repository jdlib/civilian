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
package org.civilian.resource.scan;


/**
 * ScanException is an exception thrown for errors during ResourceScan.
 */
public class ScanException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	
	/**
	 * Creates a ScanException.
	 */
	public ScanException(String message)
	{
		super(message);
	}


	/**
	 * Creates a ScanException.
	 */
	public ScanException(String message, Exception cause)
	{
		super(message, cause);
	}
}
