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


import org.civilian.type.TypeLib;


/**
 * An text field for editing passwords.
 */
public class PasswordField extends InputField<String>
{
	/**
	 * Creates a new PasswordField.
	 * @param name the field name
	 */
	public PasswordField(String name)
	{
		super(TypeLib.STRING, name);
		setInputType(INPUT_TYPE_PASSWORD);
	}

	
	/**
	 * Returns "", to suppress printing of passwords
	 * when a form is represented again after submit.
	 */
	@Override public String format()
	{
		return "";
	}
}
