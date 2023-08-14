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
 package org.civilian.samples.jpa.shared.web;


import org.civilian.form.Button;
import org.civilian.form.Form;
import org.civilian.form.TextField;
import org.civilian.request.RequestProvider;


public class IndexForm extends Form
{
	public IndexForm(RequestProvider rp)
	{
		super(rp);
		add(code	= new TextField("code"), "Enter a person code").setRequired();
		add(ok		= Button.submit("OK"));
	}


	public TextField code;
	public Button ok;
}
