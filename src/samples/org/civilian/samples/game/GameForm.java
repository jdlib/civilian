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
package org.civilian.samples.game;


import org.civilian.form.Button;
import org.civilian.form.Form;
import org.civilian.form.IntField;
import org.civilian.response.ResponseProvider;


public class GameForm extends Form
{
	public GameForm(ResponseProvider rp)
	{
		super(rp);
		
		add(guess 	= new IntField("guess"), "Your guess").setRequired();
		add(ok 		= Button.submit("OK"));
		add(reset 	= Button.submit("Reset")).setNoValidate();
	}
	
	
	public final IntField guess;
	public final Button ok;
	public final Button reset;
}
