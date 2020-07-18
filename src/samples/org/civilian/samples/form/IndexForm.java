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
 package org.civilian.samples.form;


import org.civilian.Controller;
import org.civilian.form.*;


public class IndexForm extends Form
{
	public IndexForm(Controller controller)
	{
		super(controller);
		
		add(counter		= HiddenField.create("counter", 0));
		add(lastName 	= new TextField("lastName"), 	"Last Name")	.setRequired();
		add(firstName 	= new TextField("firstName"),   "First Name");
		add(zip		 	= new TextField("zip"), 		"ZIP")			.setPlaceholder("00000").setPattern("[0-9]{5}").setMaxLength(5);
		add(city	 	= new TextField("city"), 		"City")			.setSize(30);
		add(range	 	= new IntField("range"), 		"Range")		.asRangeField().setStep(10).setMinMax(0, 200);
		add(password	= new PasswordField("pwd"),		"Password");
		add(newsletter	= new Checkbox("check")).setText("Send me the newsletter");
		add(ok 			= Button.submit("OK"));
		add(cancel		= Button.submit("Cancel").setNoValidate());	// we only want HTML5 validation
		add(reset 		= Button.reset("Reset").setNoValidate());	// (e.g. required checking...)
		add(delete		= Button.button("Delete").setNoValidate()); // for the OK-Button
		
		newsletter.reloadOnChange(Boolean.FALSE);
		if (newsletter.getBooleanValue())
			add(email	= new TextField("email"), "Email").setInputType(TextField.INPUT_TYPE_EMAIL);

		delete.setDisabled();
	}
	
	
	
	public HiddenField<Integer> counter;
	public TextField lastName;
	public TextField firstName;
	public TextField email;
	public TextField zip;
	public TextField city;
	public IntField range;
	public PasswordField password;
	public Checkbox newsletter;
	public Button ok;
	public Button cancel;
	public Button reset;
	public Button delete;
}
