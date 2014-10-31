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
 package org.civilian.samples.upload;


import org.civilian.Controller;
import org.civilian.form.Button;
import org.civilian.form.FileField;
import org.civilian.form.Form;


public class IndexForm extends Form
{
	public IndexForm(Controller resource)
	{
		super(resource);
		
		file 	= add(new FileField("file"), "File");
		ok 		= add(Button.submit("OK"));
		
		file.setMultiple(true).setRequired(true);
	}

	
	public FileField file;
	public Button ok;
}
