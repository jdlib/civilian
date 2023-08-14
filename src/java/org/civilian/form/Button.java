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


import org.civilian.request.Request;
import org.civilian.template.HtmlUtil;
import org.civilian.template.TemplateWriter;
import org.civilian.type.Type;
import org.civilian.type.TypeLib;
import org.civilian.util.Check;


/**
 * Button models the various HTML buttons:
 * {@code
 * <input value="..." type="submit">
 * <input value="..." type="reset" >
 * <input value="..." type="button">
 * <button>...</button>
 * <button type="submit">...</button>
 * <button type="reset">...</button>
 * }
 */
public class Button extends Control<String>
{
	/**
	 * Variant is an enum for the various HTML button variants.
	 */
	public enum Variant
	{
		/**
		 * Printed as &lt;input type="submit" value="..."&gt;
		 */
		INPUT_SUBMIT(false, "submit"),	

		/**
		 * Printed as &lt;input type="reset" value="..."&gt;
		 */
		INPUT_RESET(false, "reset"),

		/**
		 * Printed as &lt;input type="button" value="..."&gt;
		 */
		INPUT_BUTTON(false, "button"),
		
		/**
		 * Printed as &lt;button type="submit"&gt;{value}&lt;/button&gt;
		 */
		BUTTON_SUBMIT(true, "submit"),

		/**
		 * Printed as &lt;button type="reset"&gt;{value}&lt;/button&gt;
		 */
		BUTTON_RESET(true, "reset"),
		
		/**
		 * Printed as &lt;button&gt;{value}&lt;/button&gt;
		 */
		BUTTON(true, null);
		
		
		Variant(boolean buttonTag, String type)
		{
			this.buttonTag = buttonTag;
			this.type = type;
		}
		

		/**
		 * Returns either "button" or "input". 
		 */
		public final String tag()
		{
			return buttonTag ? "button" : "input";
		}
		
		
		/**
		 * Returns if the variant describes a submit button. 
		 */
		public final boolean isSubmitVariant()
		{
			return "submit".equals(type);
		}
		

		public final boolean buttonTag;
		public final String type;
	}

	
	/**
	 * Creates a Button for variant {@link Variant#BUTTON}.
	 * @param label the button label
	 */
	public static Button button(String label)
	{
		return new Button(Variant.BUTTON, label); 
	}
		
	
	/**
	 * Creates a Button for variant {@link Variant#BUTTON_RESET}.
	 * @param label the button label
	 */
	public static Button reset(String label)
	{
		return new Button(Variant.BUTTON_RESET, label); 
	}

	
	/**
	 * Creates a Button for variant {@link Variant#BUTTON_SUBMIT}.
	 * @param label the button label
	 */
	public static Button submit(String label)
	{
		return new Button(Variant.BUTTON_SUBMIT, label); 
	}

	
	/**
	 * Creates a Button for variant {@link Variant#INPUT_BUTTON}.
	 * @param label the button label
	 */
	public static Button inputButton(String label)
	{
		return new Button(Variant.INPUT_BUTTON, label); 
	}
		
	
	/**
	 * Creates a Button for variant {@link Variant#INPUT_RESET}.
	 * @param label the button label
	 */
	public static Button inputReset(String label)
	{
		return new Button(Variant.INPUT_RESET, label); 
	}

	
	/**
	 * Creates a Button for variant {@link Variant#INPUT_SUBMIT}.
	 * @param label the button label
	 */
	public static Button inputSubmit(String label)
	{
		return new Button(Variant.INPUT_SUBMIT, label); 
	}

	
	/**
	 * Creates a button.
	 * @param variant the button variant
	 * @param value the button label
	 */
	public Button(Variant variant, String value)
	{
		super(null);
		variant_ = Check.notNull(variant, "variant");
		setValue(value);
	}
	
	
	@Override public Type<String> getType()
	{
		return TypeLib.STRING;
	}
	

	/**
	 * Returns Category.BUTTON.
	 */
	@Override public Category getCategory()
	{
		return Category.BUTTON;
	}

	
	/**
	 * Sets the value of the "onclick" attribute.
	 */
	public Button setOnClick(String function)
	{
		setAttribute("onclick", function);
		return this;
	}
	
	
	/**
	 * Sets the formnovalidate attribute of the button.
	 * If set then HTML5 validations will not be performed when the button
	 * is clicked and submitting the form. The use case are buttons with a 
	 * cancel or reset-function, which is implemented on the server-side. 
	 * When clicked, the form input should not be validated, but just submitted,
	 * and the server-side logic will execute the cancel or reset functionality. 
	 */
	public Button setNoValidate(boolean noValidate)
	{
		noValidate_ = noValidate;
		return this;
	}

	
	/**
	 * Calls setNoValidate(true).
	 */
	public Button setNoValidate()
	{
		return setNoValidate(true);
	}
	

	/**
	 * Returns the no-validate flag of the button.
	 */
	public boolean getNoValidate()
	{
		return noValidate_;
	}

	
	@Override protected void setForm(Form form)
	{
		super.setForm(form);
		if (getName() == null)
			setName(form.getName() + "_button");
		if (variant_.isSubmitVariant() && form.getDefaultButton() == null)
			form.setDefaultButton(this);
	}
	
	
	/**
	 * Does nothing.
	 */
	@Override protected void parse(Request request)
	{
	}
	
	
	/**
	 * Returns if the form was submitted by clicking this button, or if the form
	 * was submitted by pressing enter in a control and this button
	 * was registered as default button.
	 */
	public boolean isClicked()
	{
		Form form = getForm();
		if (form == null)
			return false;
		
		String param = form.getRequest().getParameter(getName());
		if (param != null)
			return param.equals(getValue());
		else
			return (form.getDefaultButton() == this) && form.isSubmitted();
	}
	
	
	/**
	 * Returns if the form was submitted by clicking this button. 
	 */
	public boolean isDirectlyClicked()
	{
		Form form = getForm();
		if (form == null)
			return false;
		else
		{
			String param = form.getRequest().getParameter(getName());
			return (param != null) && param.equals(getValue());
		}
	}
	
	
	/**
	 * Prints the button markup.
	 */
	@Override public void print(TemplateWriter out, String... attrs)
	{
		start(out, attrs);
		if (variant_.buttonTag)
		{
			HtmlUtil.text(out, getValue());
			end(out);
		}
	}
	

	public void start(TemplateWriter out)
	{
		start(out, (String[])null);
	}
	
	
	/**
	 * Prints the button start tag.
	 */
	public void start(TemplateWriter out, String... attrs)
	{
		out.print('<');
		out.print(variant_.tag());
		if (variant_.type != null)
			HtmlUtil.attr(out, "type", variant_.type);
		if (getName() != null) 
			HtmlUtil.attr(out, "name", getName());
		if (isDisabled())
			out.print(" disabled");
		if (noValidate_)
			out.print(" formnovalidate");
		// even if the button uses a <button> tag, we print
		// the value attribute, because we need the value as
		// parameter value
		if (!variant_.buttonTag || variant_.isSubmitVariant())
			HtmlUtil.attr(out, "value", getValue());
		printAttrs(out, attrs);
		out.print('>');
	}
	

	/**
	 * Prints the button end tag.
	 */
	public void end(TemplateWriter out)
	{
		if (variant_.buttonTag)
		{
			out.print("</");
			out.print(variant_.tag());
			out.print('>');
		}
	}

	
	private Variant variant_;
	private boolean noValidate_;
}
