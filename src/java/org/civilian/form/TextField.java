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
package org.civilian.form;


import org.civilian.type.Type;
import org.civilian.type.TypeLib;


/**
 * An InputField for text input.
 */
public class TextField extends InputField<String>
{
	/**
	 * Creates a new text field.
	 * @param name the field name
	 */
	public TextField(String name)
	{
		super(name);
	}
	
	
	/**
	 * Returns TypeLib.STRING.
	 */
	@Override public Type<String> getType()
	{
		return TypeLib.STRING;
	}


	@Override protected String formatValue()
	{
		String s = getValue();   
		return s != null ? s : "";
	}
}
