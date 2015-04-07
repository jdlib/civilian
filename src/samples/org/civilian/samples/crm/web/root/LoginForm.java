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
 package org.civilian.samples.crm.web.root;


import org.civilian.Controller;
import org.civilian.form.Button;
import org.civilian.form.Form;
import org.civilian.form.HiddenField;
import org.civilian.form.PasswordField;
import org.civilian.form.Select;
import org.civilian.form.TextField;
import org.civilian.samples.crm.text.Message;
import org.civilian.samples.crm.web.CrmConstants;
import org.civilian.text.LocaleService;
import org.civilian.text.keys.KeyList;


public class LoginForm extends Form
{
	public LoginForm(Controller res)
	{
		super(res);
		
		KeyList<LocaleService> langKeys = res.getApplication().getLocaleServices().getServiceKeys();
		
		add(name 		= new TextField("name"), res.msg(Message.Login));
		add(password 	= new PasswordField("password"), res.msg(Message.Password));
		add(ok 			= Button.submit(res.msg(Message.OK)));
		add(language	= new Select<>("language", langKeys), res.msg(Message.Language));
		add(path		= HiddenField.create(CrmConstants.LOGIN_PATH_PARAM));
		
		name.setRequired(true);
		password.setRequired(true);
		language.removeDefaultOption();
		language.setRequired(true);
	}
	
	
	public Button ok;
	public TextField name;
	public Select<LocaleService> language;
	public PasswordField password;
	public HiddenField<String> path;
}
