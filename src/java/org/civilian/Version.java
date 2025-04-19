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
package org.civilian;


/**
 * Defines the version number of Civilian. 
 */
public class Version
{
	/**
	 * Holds the version number of the Civilian framework.
	 */
	public static final String VALUE = "4.0";

	
	/**
	 * Prints the version number to System.out.
	 * If the first argument equals "-v", then a property like
	 * line "civilian.version = (version)" is printed.
	 * @param args the commandline arguments 
	 */
	public static void main(String[] args)
	{
		boolean verbose = (args.length > 0) && "-v".equals(args[0]);
		if (verbose)
			System.out.print("civilian.version = ");
		System.out.println(VALUE);
	}
}
