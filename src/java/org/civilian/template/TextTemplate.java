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
package org.civilian.template;


import org.civilian.Template;


/**
 * TextTemplate is a Template implementation for simple string output.
 */
public class TextTemplate extends Template
{
	public TextTemplate(String text)
	{
		text_ = text;
	}

	
	@Override protected void print() throws Exception
	{
		if (text_ != null)
			out.print(text_);
	}


	private String text_;
}
