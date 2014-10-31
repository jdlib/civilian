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
import org.civilian.util.StringUtil;


class Resource
{
	public Resource(String name, String content)
	{
		name_ 		= name;
		content_	= content;
		file_		= StringUtil.cutRight(StringUtil.cutRight(name, ".res"), ".min");
		
		int p = file_.indexOf('$');
		if (p >= 0)
			file_ = file_.substring(p + 1);
	}
	
	
	public String getName()
	{
		return name_;
	}

	
	public Resource setFile(String file)
	{
		file_ = file;
		return this;
	}

	
	public Resource setFile(ClassVar var)
	{
		return setFile(var.file);
	}

	
	public String getContent()
	{
		return content_;
	}
	
	
	public Resource replace(Variable var)
	{
		return replace(var.key, var.value);
	}
	
	
	public Resource replace(Variable... vars)
	{
		for (Variable var : vars)
			replace(var);
		return this;
	}

	
	public Resource replace(String key, String value)
	{
		content_ = content_.replace(key, value);
		return this;
	}
	
	
	public Resource replace(VariableMap vars)
	{
		StringBuilder s = new StringBuilder();
		int start = 0;
		int p;
		while((p = content_.indexOf(Variable.START_KEY, start)) >= 0)
		{
			s.append(content_, start, p);
			int q = content_.indexOf('}', p);
			if (q < 0)
			{
				int end = Math.max(content_.length(), p + 30);
				throw new IllegalArgumentException("closing } not found at '" + content_.substring(p, end) + "'");
			}
			String v = vars.getSafe(content_.substring(p + 2, q));
			s.append(v);
			start = q + 1;
		}
		s.append(content_, start, content_.length());
		content_ = s.toString();
		return this;
	}

	
	public void writeTo(Project.Dir dir) throws IOException
	{
		writeTo(dir, null);
	}
	
	
	public void writeTo(Project.Dir dir, String encoding) throws IOException
	{
		dir.write(file_, content_, encoding);
		if (content_.indexOf(Variable.START_KEY) >= 0)
			throw new IllegalArgumentException(name_ + " still contains variables");
	}
	
	
	private String file_;
	private String name_;
	private String content_;
}
