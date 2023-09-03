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
import java.util.List;


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


	public boolean hasFields()
	{
		return (args != null) || !mixins.isEmpty() || standalone;
	}


	public String packageName;
	public String className;
	public final ImportList imports = new ImportList();
	public final List<String> prolog = new ArrayList<>();
	public String args;
	public final List<Argument> arguments = new ArrayList<>();
	public final List<MixinField> mixins = new ArrayList<>();
	public String superArgs;
	public String extendsClass;
	public String writerClass;
	public String writerClassSimple;
	public String implementsList;
	public String exception = "Exception";
	public String superCall;
	public int superCallLine;
	public boolean hasMainTemplate;
	public boolean standalone;
	public boolean isPublic = true;
	public boolean isAbstract;
}


