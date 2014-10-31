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
package org.civilian.tool.scaffold;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.civilian.util.ClassUtil;
import org.civilian.util.IoUtil;
import org.civilian.util.StringUtil;


class ResourceDir
{
	public ResourceDir(VariableMap vars)
	{
		this(vars, "");
	}
	
	
	public ResourceDir(VariableMap vars, String name)
	{
		vars_ = vars;
		path_ = ClassUtil.getPackageName(getClass()).replace('.', '/') + "/files/" + name;
		path_ = StringUtil.haveRight(path_, "/");
	}

	
	@Override public String toString()
	{
		return path_;
	}
	
	
	public Resource resource(String name) throws IOException
	{
		Resource resource = new Resource(name, getContent(name));
		if (vars_ != null)
			resource.replace(vars_);
		return resource;
	}
	
	
	public String getContent(String name) throws IOException
	{
		try(Reader in = getReader(name))
		{
			return IoUtil.readString(in);
		}
	}
	
	
	public Reader getReader(String name) throws IOException
	{
		String path = path_ + name;
		InputStream in = getClass().getClassLoader().getResourceAsStream(path);
		if (in == null)
			throw new IllegalArgumentException("cannot find resource '" + path + "'");
		return new InputStreamReader(in, "UTF-8"); // the resources are all in UTF-8
	}


	private String path_;
	private VariableMap vars_;
}
