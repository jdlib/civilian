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
package org.civilian.tool.csp;


import java.util.ArrayList;


class ClassData
{
	public ClassData(String className)
	{
		this.className = className;
	}

	
	public boolean needsCtor()
	{
		return (args != null) || (superArgs != null) || (superCall != null);
	}
	

	public String packageName;
	public String className;
	public ImportList imports = new ImportList();
	public ArrayList<String> prolog = new ArrayList<>();
	public String args;
	public ArrayList<Argument> arguments;
	public ArrayList<MixinField> mixins;
	public String superArgs;
	public String extendsClass;
	public String writerClass;
	public String writerClassSimple;
	public String implementsList;
	public String exception = "Exception";
	public String superCall;
	public boolean hasMainTemplate;
	public int superCallLine;
	public boolean standalone;
	public boolean isPublic = true;
}


