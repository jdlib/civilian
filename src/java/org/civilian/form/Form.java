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


import java.util.ArrayList;
import org.civilian.Request;
import org.civilian.Response;
import org.civilian.annotation.Get;
import org.civilian.annotation.Post;
import org.civilian.provider.RequestProvider;
import org.civilian.template.HtmlUtil;
import org.civilian.template.TemplateWriter;
import org.civilian.util.Check;


/**
 * Form represents a HTML form.
 * The form class helps to implement request-response interactions involving HTML forms.<br>
 * You build a form by adding controls to the form (@link {@link #add(Control)}).<br>
 * A form can then be used to print a form in a HTML response (initial presentation to the client). 
 * When the form data is sent back to the server, you instantiate the Form object again,
 * and let the controls read the submitted values from the request. 
 * Controls are typed, therefore low level extraction of parameter values and conversion
 * to the target type is handled by the controls. Error information about missing
 * or invalid form values is also available.<p>
 * It is good practice to derive own classes from Form for your application forms
 * and add the controls within the constructor of the implementation class.<br>   
 * Form objects are constructed and used only for a single request.
 * This is how to implement a typical request-response cycle using forms:
 * <ol>
 *  <li>In the first (GET)-request to a resource, you would construct the form
 * 		and print it to the response.
 *  <li>When the client submits the form,
 *  	you detect that the form was submitted (@link #isSubmitted()}.
 *  	You now read the values of the controls {@link #read()}.
 *  	If the form is invalid (control values are missing, have wrong types, or
 *  	fail your custom validation) you would simply print the form again,
 *  	which is then presented to the client with his own input and any
 *  	additional error information.
 *  <li>If the submitted form is valid, you can extract the control values and process
 *  	them. 
 *  </ol>
 *  The same resource URL can be used to implement this cycle. To differentiate between
 *  the request phase (initial request or subsequent submits) you can use {@link #isSubmitted()}
 *  and proceed conditionally within your resource method. If POST is used for form submission,
 *  you can also create two different action methods with a {@link Get @Get} and {@link Post @Post} annotation to
 *  let the framework dispatch the request to the correct method. 
 *  <p>
 *  Besides submit detection a form can also recognize a special form of submit which is called reload:
 *  The use case for this are forms which have conditionally parts: Depending on user input
 *  additional controls are presented to the client. To implement this you would typically
 *  submit the form from a Javascript event handler. On the server-side you want to read the request,
 *  add additional controls to the form (depending on the so far entered input), but no error should be triggered yet. 
 */
public class Form implements RequestProvider
{
	static final String RELOADED = "reloaded";
	
	
	/**
	 * Creates a new form.
	 * @param requestProvider allows the form to access the request and read
	 * 		parameters from the request.
	 */
	public Form(RequestProvider requestProvider)
	{
		requestProvider_ = Check.notNull(requestProvider, "requestProvider");
		setName("f" + getClass().getName().hashCode());
	}

	
	//---------------------------------------------
	// accessors
	//---------------------------------------------
	
	
	/**
	 * Returns the request associated with the form.
	 */
	@Override public Request getRequest()
	{
		Request request = requestProvider_.getRequest();
		if (request == null)
			throw new IllegalStateException(requestProvider_.getClass() + " returned null request");
		return request;
	}
	

	/**
	 * Sets the form name. By default a form name is automatically 
	 * generated. When the form is printed, its name is printed 
	 * as hidden field. The hidden field then allows to detect
	 * - given a request - if the form was submitted.
	 */
	public void setName(String name)
	{
		name_ = name;
	}

	
	/**
	 * Returns the form name. By default a unique form name 
	 * based on the form class is automatically generated. 
	 */
	public String getName()
	{
		return name_;
	}
	

	/**
	 * Sets the target attribute of the HTML form element.
	 * @see #getTarget()
	 */
	public void setTarget(String target)
	{
		target_ = target;  
	}

	
	/**
	 * Returns the target attribute of the HTML form element.
	 * The default is null.
	 * @see #setTarget(String)
	 */
	public String getTarget()
	{
		return target_;
	}
	
	
	/**
	 * Returns the action attribute of the HTML form element.
	 * The default is null, which in case of a submit will
	 * call the originating resource.
	 */
	public String getAction()
	{
		return action_;
	}
	
	
	/**
	 * Sets the action attribute of the form element. It is automatically
	 * URL encoded, when printed.
	 */
	public void setAction(String action)
	{
		action_ = action;
	}

	
	/**
	 * Returns the method attribute of the form. It is set to POST by default.
	 */
	public String getMethod()
	{
		return method_;
	}
	
	
	/**
	 * Sets the value of the method attribute.
	 */
	public void setMethod(String method)
	{
		method_ = method;
	}

	
	/**
	 * Sets the value of the method attribute to POST.
	 */
	public void setPostMethod()
	{
		setMethod("POST");
	}

	
	/**
	 * Sets the value of the method attribute to GET.
	 */
	public void setGetMethod()
	{
		setMethod("GET");
	}

	
	/**
	 * Returns the multipart encoded flag previously set by 
	 * setMultipartEncoded(). The default value is false.
	 * @see #setMultipartEncoded
	 */
	public boolean isMultipartEncoded()
	{
		return multipartEncoded_;
	}
	
	
	/**
	 * Sets if the form is multipart encoded,
	 * i.e. the enctype attribute of the form should have value
	 * "multipart/form-data". If you add a {@link FileField} to
	 * the form, the form is automatically multipart encoded.
	 */
	public void setMultipartEncoded(boolean mode)
	{
		multipartEncoded_ = mode;
	}

	
	/**
	 * Sets an attribute on the HTML form element.
	 * @param name the attribute name
	 * @param value the attribute value
	 */
	public void setAttribute(String name, String value)
	{
		attribute_ = new Attribute(name, value, attribute_);
	}
	
	
	/**
	 * Returns the value of an attribute, previously set
	 * by {@link #setAttribute(String, String)}.
	 */
	public String getAttribute(String name)
	{
		Attribute attr = Attribute.getAttribute(attribute_, name);
		return attr == null ? null : attr.value;
	}
	
	
	//---------------------------------
	// controls
	//---------------------------------


	/**
	 * Returns the number of controls contained in the form.
	 */
	public int size()
	{
		return controls_.size();
	}

	
	/**
	 * Returns the control.
	 */
	public Control<?> get(int i)
	{
		return controls_.get(i);
	}

	
	/**
	 * Returns the control with the given name.
	 */
	public Control<?> get(String name)
	{
		if (name != null)
		{
			int n = size();
			for (int i=0; i<n; i++)
			{
				Control<?> control = get(i);
				if (name.equals(control.getName()))
					return control;
			}
		}
		return null;
	}

	
	/**
	 * Adds a control to the form. 
	 * @param control the control. It is ignored if null.
	 */
	public <T extends Control<?>> T add(T control)
	{
		if (control != null)
		{
			control.setForm(this);
			controls_.add(control);
		}
		return control;
	}
	
	
	/**
	 * Adds a control to the form and sets the label of the control. 
	 * @param control the control. It is ignored if null.
	 * @see Control#setLabel(Object)
	 */
	public <T extends Control<?>> T add(T control, Object label)
	{
		if (control != null)
		{
			control.setLabel(label);		
			return add(control);
		}
		else
			return null;
	}
	
	
	/**
	 * Removes a control from the form.
	 */
	public void remove(Control<?> control)
	{
		controls_.remove(control);
	}
	
	
	/**
	 * Sets the required flag of all controls.
	 */
	public void setRequired(boolean required)
	{
		int size = size();
		for (int i=0; i<size; i++)
			get(i).setRequired(required);
	}

	
	/**
	 * Sets the read-only flag of all controls.
	 * @see Control#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean readOnly)
	{
		int size = size();
		for (int i=0; i<size; i++)
			get(i).setReadOnly(readOnly);
	}


	//---------------------------------------------
	// default button
	//---------------------------------------------
	

	/**
	 * Returns the default button. The default button is either the first submit button
	 * added to the form or the button which was explicitly set by {@link #setDefaultButton}.
	 * If the form was submitted by pressing enter in a text field then
	 * the {@link Button#isClicked() isClicked()}-method of the default button will return true.
	 */
	public Button getDefaultButton()
	{
		return defaultButton_;
	}


	/**
	 * Sets the default button.
	 * @see #getDefaultButton()
	 */
	public void setDefaultButton(Button button)
	{
		defaultButton_ = button;
	}

	
	//---------------------------------------------
	// request processing
	//---------------------------------------------
	
	
	/**
	 * Reads control values from the request. If the form was only reloaded but not submitted,
	 * then the required flag of controls is ignored.
	 * After all control values are read, the {@link #validate(boolean)}-method is called.
	 * @return true if no control has an error when reading its value from the request.
	 * 		An error could be a type conversion error, a missing value
	 * 		for a control, or a value which failed the validation
	 * 		of the control. 
	 * @throws Exception thrown when an exception during request processing occurs.
	 */
	public boolean read() throws Exception
	{
		boolean isReloaded = isReloaded();
		boolean ok = true;
		clearErrorControl();

		Request request = getRequest();
		int size = size();
		for (int i=0; i<size; i++)
		{
			Control<?> control = get(i);
			boolean setRequired = false;
			
			if (isReloaded && (setRequired = control.isRequired()))
				control.setRequired(false);
				
			if (!control.read(request))
				ok = false;	

			if (setRequired)
				control.setRequired(true);
		}
		
		if (!isReloaded)
			ok &= validate(ok);
		
		return ok;
	}
	
	
	/**
	 * Validates the form. Called after all controls have read their values from the request.
	 * The method is not called when a form was reloaded.
	 * The default implementation returns true. 
	 * @param ok true if all controls of the form are valid, false if some controls have errors
	 * @return return if true if all controls have valid values, false otherwise. In the later
	 * 		case you should have changed the {@link Control#setStatus(org.civilian.form.Control.Status)
	 * 		status} of invalid controls.
	 */
	protected boolean validate(boolean ok) throws Exception
	{
		return true;
	}
	
	
	//---------------------------------------------
	// status
	//---------------------------------------------

	
	/**
	 * Returns true if every control is OK.
	 * @see Control#isOk()
	 */
	public boolean isOk()
	{
		int count = size();
		for (int i=0; i<count; i++)
		{
			if (!get(i).isOk())
				return false;
		}
		return true;		
	}
	

	/**
	 * Returns true if the form was reloaded. Reload means, that the 
	 * form was presented in a HTML page, and based on user interaction
	 * programmatically submitted to the server in order to change, add or remove controls,
	 * and then to be presented again.
	 */
	public boolean isReloaded()
	{
		return RELOADED.equals(getNameParam());
	}

	
	/**
	 * Returns true, if the form was submitted.
	 * This test is implemented by checking the request parameter
	 * for the hidden name field of the form.
	 * If it is part of the request then we assume that the form was
	 * submitted.    
	 */
	public boolean isSubmitted()
	{
		return "".equals(getNameParam());
	}
	
	
	private String getNameParam()
	{
		return name_ != null ? getRequest().getParameter(name_) : null;
	}

	
	//---------------------------------------------
	// error control
	//---------------------------------------------
	
	
	/**
	 * Returns the first control which contains an error.
	 * The error control is either set by an explicit call to {@link #setErrorControl(Control)}
	 * or when an error status of the control is set ({@link Control#setStatus(org.civilian.form.Control.Status)}. 
	 */
	public Control<?> getErrorControl()
	{
		return errorControl_;
	}
	
	
	/**
	 * Clears the error control.
	 * @see #getErrorControl()
	 */
	public void clearErrorControl()
	{
		errorControl_ = null;
	}
	
	
	/**
	 * Sets the error control of the form. If the form has already
	 * a error control it will be ignored.
	 * @see #getErrorControl()
	 */
	public final void setErrorControl(Control<?> control)
	{
		if (errorControl_ == null)
			errorControl_ = control;
	}
	
	
	//---------------------------------------------
	// print
	//---------------------------------------------

	
	/**
	 * Prints the form start tag and all hidden fields.
	 */
	public void start(TemplateWriter out)
	{
		start(out, (String[])null);
	}
	
	
	/**
	 * Prints the form start tag and all hidden fields.
	 * @param attrs a list of attribute names and values which
	 * 		should be printed in the start tag of the control element.
	 */
	public void start(TemplateWriter out, String... attrs)
	{
		out.print("<form");
		printAttrs(out);
		HtmlUtil.attrs(out, attrs);
		out.println('>');
		printHiddenFields(out);
	}
	
	
	public void printHiddenFields(TemplateWriter out)
	{
		// each form has a hidden field equal to the form name
		// it is used to decide if the form was submitted by
		// hitting enter inside a text field
		if (name_ != null)
		{
			out.print("<input type=\"hidden\" value=\"\"");
			HtmlUtil.attr(out, "name", name_);
			out.println(">");
		}
		
		// print hidden fields
		int size = size();
		for (int i=0; i<size; i++)
		{
			Control<?> control = get(i);
			if (control.getCategory() == Control.Category.HIDDEN)
			{
				control.print(out);
				out.println();
			}
		}
	}
	
	
	/**
	 * Prints the attributes of the form start tag.
	 */
	public void printAttrs(TemplateWriter out)
	{
		Response response = getRequest().getResponse();
		HtmlUtil.attr(out, "method", method_);
		String action = getAction();
		if (action != null)
			HtmlUtil.attr(out, "action", response.addSessionId(action));
		HtmlUtil.attr(out, "name", name_);
		HtmlUtil.attr(out, "target", target_);
		if (isMultipartEncoded())
		{
			HtmlUtil.attr(out, "enctype", "multipart/form-data", false);
			String encoding = response.getContentEncoding();
			if (encoding != null)
				HtmlUtil.attr(out, "accept-charset", encoding);
		}
		if (attribute_ != null) 
			out.print(attribute_);
	}
	
	
	/**
	 * Prints the form end tag.
	 * Calls {@link #end(TemplateWriter, Control) end(out, null)};
	 */
	public void end(TemplateWriter out)
	{
		end(out, null);		
	}

	
	/**
	 * Prints the form end tag.
	 * If the form contains controls which need scripts to be printed after the end tag,
	 * these scripts are also printed.
	 */
	public void end(TemplateWriter out, Control<?> focusControl)
	{
		out.println("</form>");
		focus(out, focusControl);
	}
	
	
	public void focus(TemplateWriter out, Control<?> focusControl)
	{
		if (getErrorControl() != null)
			focusControl = getErrorControl();
		if (focusControl != null)
			focusControl = focusControl.toFocusControl();
		if (focusControl != null)
			focusControl.focus(out, true);
	}
	

	private String name_;
	private String action_;
	private String method_ = "POST";
	private String target_;
	private Attribute attribute_;
	private Button defaultButton_;
	private Control<?> errorControl_;
	private boolean multipartEncoded_;
	private RequestProvider requestProvider_;
	private ArrayList<Control<?>> controls_ = new ArrayList<>();
}
