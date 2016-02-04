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


import org.civilian.Request;
import org.civilian.template.HtmlUtil;
import org.civilian.template.TemplateWriter;
import org.civilian.type.ListType;
import org.civilian.type.Type;
import org.civilian.type.TypeLib;
import org.civilian.type.fn.TypeSerializer;
import org.civilian.type.fn.StandardSerializer;
import org.civilian.util.Check;


/**
 * Control is a base class for classes which model HTML form elements.
 * A Control class has
 * <ul>
 * <li>a name corresponding to the name of the form element.
 * <li>a {@link #getType() type}
 * <li>a value of that type 
 * <li>a reference to its {@link Form}
 * <li>an enabled-flag,
 * <li>a required-flag,
 * <li>status information,
 * <li>the capability to read its value from a Request object,
 * <li>the capability to print its markup to a TemplateWriter
 * </ul>
 */
public abstract class Control<T> implements TemplateWriter.Printable
{
	/**
	 * Status is an Enum for the status of a Control.
	 * @see Control#getStatus()
	 */
	public enum Status
	{
		/**
		 * OK status.
		 */
		OK,
	
		/**
		 * A status constant indicating that the Control is required but has no value,
		 * after reading its value from a request.
		 * @see #isRequired()
		 * @see #read(Request)
		 */
		MISSING,
	
		/**
		 * A status constant indicating that the Control could not   
		 * read its value from a Request because of a parse error.
		 * @see #getStatus()
		 * @see #read(Request)
		 */
		PARSE_ERROR,
	
		/**
		 * A status constant indicating that the Control value    
		 * failed validation.
		 * @see #getStatus()
		 * @see Form#validate(boolean)
		 */
		VALIDATION_ERROR
	}


	/**
	 * Each control has a category, depending on its visual appearance
	 * and function.
	 * @see Control#getCategory()
	 */
	public enum Category
	{
		/**
		 * A normal control, used to input values.
		 */
		INPUT,
		
		/**
		 * A button.
		 */
		BUTTON,
		
		/**
		 * A hidden field.
		 */
		HIDDEN,
	}
		
		
	/**
	 * Creates a new control.
	 * @param type the type of the control
	 */
	protected Control(Type<T> type)
	{
		type_ = Check.notNull(type, "type");
	}

	
	/**
	 * Creates a new control.
	 * @param type the type of the controls value
	 * @param name the control name
	 */
	protected Control(Type<T> type, String name)
	{
		this(type);
		setName(name);
	}

	
	/**
	 * Returns the type.
	 */
	public Type<T> getType()
	{
		return type_;
	}
	
	
	/**
	 * Tests if the given type equals the type of this control.
	 */
	protected void checkType(Type<?> type)
	{
		if (type_ != type)
			throw new IllegalArgumentException("invalid type '" + type + "'");
	}
	
	
	/**
	 * Returns the name of the Control.
	 */
	public String getName()
	{
		return name_;
	}
	
	
	/**
	 * Sets the name of the Control.
	 */
	public void setName(String name)
	{
		name_ = name;
	}

	
	/**
	 * Sets the form. Called by the form, when the control is {@link Form#add(Control) added}
	 * to the form.
	 */
	protected void setForm(Form form)
	{
		if (form_ != null)
			throw new IllegalStateException("already added to form " + form_);
		form_ = form;
	}
	
	
	/**
	 * Returns the form to which the control was added.
	 * Returns null when called before the control was added to a form.
	 * @see Form#add(Control)
	 */
	public Form getForm()
	{
		return form_;
	}

	
	/**
	 * Returns the form to which the control was added.
	 * @throws IllegalStateException when the field was not yet added to a form.
	 */
	public Form getSafeForm()
	{
		if (form_ == null)
			throw new IllegalStateException("form is null");
		return form_;
	}
	

	/**
	 * Returns the value.
	 */
	public T getValue()
	{
		return value_;
	}
	
	
	/**
	 * Returns if the {@link #getValue() control value} is not null. 
	 */
	public boolean hasValue()
	{
		return value_ != null;
	}

	
	/**
	 * Sets the value.
	 */
	public void setValue(T value)
	{
		setValue(value, null, null);
	}
	
	
	/**
	 * Unchecked version of setValue()
	 */
	@SuppressWarnings("unchecked")
	private void setValueUc(Object value)
	{
		setValue((T)value);
	}

	
	/**
	 * Casts the stored value to a Number and returns its integer value.
	 */
	public int getIntValue()
	{
		return ((Number)value_).intValue();
	}

	
	/**
	 * Sets the value.
	 * @throws IllegalArgumentException if the type of this Value
	 * 		is not Integer.
	 */
	public void setIntValue(int value)
	{
		checkType(TypeLib.INTEGER);
		setValueUc(Integer.valueOf(value));
	}

	
	/**
	 * Casts the stored value to a Boolean and returns its boolean value.
	 */
	public boolean getBooleanValue()
	{
		return value_ == null ? false : ((Boolean)value_).booleanValue();
	}

	
	/**
	 * Sets the value of a boolean parameter.
	 * @throws IllegalArgumentException if the type of this Value
	 * 		is not Boolean.
	 */
	public void setBooleanValue(boolean value)
	{
		checkType(TypeLib.BOOLEAN);
		setValueUc(value ? Boolean.TRUE : Boolean.FALSE);
	}

	
	/**
	 * Casts the stored value to a Number and returns its double value.
	 */
	public double getDoubleValue()
	{
		return ((Number)value_).doubleValue();
	}

	
	/**
	 * Sets the value of a double parameter.
	 * @throws IllegalArgumentException if the type of this Value
	 * 		is not Double.
	 */
	public void setDoubleValue(double value)
	{
		checkType(TypeLib.DOUBLE);
		setValueUc(new Double(value));
	}

	
	protected void setValue(T value, Exception error, String errorValue)
	{
		value_      = value;
		error_ 		= error;
		errorValue_ = errorValue;
		updateStatus();
	}

	
	/**
	 * Returns any parse exception which was caught during a 
	 * previous call to parse.
	 */
	public Exception getError()
	{
		return error_;
	}
	
	
	/**
	 * Returns the original string value, which was passed to parse()
	 * and caused a parse exception
	 */
	public String getErrorValue()
	{
		return errorValue_;
	}

	
	/**
	 * Returns if the Value has an error, because of an 
	 * unsuccessful parse operation.
	 */
	public boolean hasError()
	{
		return error_ != null;
	}
	
	
	/**
	 * Clears any error and error value.
	 */
	public void clearError()
	{
		setValue(value_, null, null);
	}
	
	
	/**
	 * Returns the number of rows of the control. The default implementation returns 1.
	 * The number of rows can be used for layout decisions, e.g. for vertical alignment
	 * of a control in a HTML table cell. 
	 */
	public int getRows()
	{
		return 1;
	}
	
	
	/**
	 * Returns the control category. 
	 * The default implementation returns Category.INPUT.
	 */
	public Category getCategory()
	{
		return Category.INPUT;
	}

	
	/**
	 * Returns the label of the control.
	 * The label is always a non-null string. By default it equals to "". 
	 */
	public String getLabel()
	{
		return label_;
	}
	
	
	/**
	 * Sets the label of the control.
	 * @return this
	 */
	public Control<T> setLabel(String label)
	{
		label_ = label == null ? "" : label;
		return this;
	}
	
	
	/**
	 * Returns if the control has a label.
	 */
	public boolean hasLabel()
	{
		return label_.length() > 0;
	}
	

	/**
	 * Returns data associated with the control.
	 * @see #setData(Object)
	 */
	public Object getData()
	{
		return data_;
	}
	
	
	/**
	 * Associates the control with arbitrary data.
	 * @return this
	 */
	public Control<T> setData(Object data)
	{
		data_ = data;
		return this;
	}

	
	/**
	 * Sets the value of an HTML attribute on the control.
	 * Use this method if the control class does not provide
	 * an explicit access method for that attribute.
	 * @return this
	 */
	public Control<T> setAttribute(String name, String value)
	{
		attribute_ = new Attribute(name, value, attribute_);
		return this;
	}
	
	
	/**
	 * Returns the value of an HTML attribute previously set 
	 * by {@link #setAttribute(String, String)}.
	 */
	public String getAttribute(String name)
	{
		return Attribute.getValue(attribute_, name);
	}
	
	
	/**
	 * Returns the HTML id of the control. By default it is null.
	 */
	public String getId()
	{
		return getAttribute("id");
	}

	
	/**
	 * Sets the HTML id of the control.
	 */
	public Control<T> setId(String id)
	{
		return setAttribute("id", id);
	}

	
	//-----------------------
	// request
	//-----------------------

	
	/**
	 * Reads the control value from the request and updates its status.
	 * If the control is marked as disabled or readonly then the method does nothing.
	 * <ul>
	 * <li>If the request value could not be parsed, the status is {@link Status#PARSE_ERROR}.
	 * <li>If the request value is OK the status is {@link Status#OK}.
	 * <li>If there was no request value and the control is not required, the status is {@link Status#OK}.
	 * <li>Else (no request value, but control required) the status is {@link Status#MISSING}.
	 * </ul>
	 * Controls may add custom validation to that logic and could change the status
	 * to {@link Status#VALIDATION_ERROR}, if custom validation fails. Another entry point 
	 * for custom validation is {@link Form#validate(boolean)} which is called when the whole
	 * form was {@link Form#read() read} from the request.
	 * @return returns if the new status of the control is Status#OK.
	 * @see #isOk()
	 */
	public boolean read(Request request)
	{
		if (!isDisabled() && !isReadOnly())
			parse(request);
		return isOk();
	}
	
	
	/**
	 * Reads the control value, using the request of the form
	 * to which the field belongs.
	 * @see #read(Request).
	 */
	public boolean read()
	{
		return read(getSafeForm().getRequest());
	}

	
	/**
	 * Reads the value from the request, but does
	 * not change the value and status in case of an error or a missing value.
	 * @return true iif the Control has a value
	 */
	public boolean readDefault(Request request)
	{
		T value = getValue();
		read();
		if (!isOk())
			setValue(value);
		return hasValue();
	}
	

	/**
	 * Parses the value from the request and updates the status.
	 * Called by read(Request). Overwrite this method if the Control
	 * has a special way to parse a request value.
	 */
	protected void parse(Request request)
	{
		if (getType().category() != Type.Category.LIST)
			parseSimple(request);
		else
			parseList(request);
	}
	
	
	private void parseSimple(Request request)
	{
		// simple type
		String s = getRequestString(request);
		if (s == null)
			setValue(null);
		else
		{
			try
			{
				T value = s != null ? request.getLocaleSerializer().parse(type_, s) : null;
				setValue(value);
			}
			catch(Exception e)
			{
				setValue(null, e, s);
			}
		}
	}

	
	private <E> void parseList(Request request)
	{
		// request.getParameters() always returns a non-null array
		String s[] = request.getParameters(getName());
		try
		{
			@SuppressWarnings("unchecked")
			ListType<T,E> listType = (ListType<T,E>)getType();
			E[] values = request.getLocaleSerializer().getParser().parse(listType.getElementType(), s); 
			setValue(listType.create(values));
		}
		catch(Exception e)
		{
			setValue(null, e, null);
		}
	}
	
	
	private String getRequestString(Request request)
	{
		String s = request.getParameter(getName());
		if (s != null)
		{
			s = s.trim();
			if (s.length() == 0)
				s = null;
		}
		return s;
	}
	
	
	//------------------------------
	// status
	//------------------------------

	
	/**
	 * Returns if the control is required. A required control with no value
	 * has status STATUS_MISSING.
	 * @see #getStatus()
	 * @see #read(Request)
	 */
	public boolean isRequired()
	{
		return isRequired_;
	}

	
	/**
	 * Marks the control as required. The default is not required.
	 * The required flag influences the status of the control
	 * when its value is read from a request.
	 */
	public Control<T> setRequired(boolean isRequired)
	{
		if (isRequired_ != isRequired)
		{
			isRequired_ = isRequired;
			updateStatus();
		}
		return this;
	}

	
	/**
	 * Sets a control required.
	 */
	public void setRequired()
	{
		setRequired(true);
	}
	
	
	/**
	 * Returns if the control is read-only.
	 */
	public boolean isReadOnly()
	{
		return isReadOnly_;
	}

	
	/**
	 * Sets if the control is readonly.
	 */
	public Control<T> setReadOnly(boolean isReadOnly)
	{
		if (isReadOnly_ != isReadOnly)
		{
			isReadOnly_ = isReadOnly;
			updateStatus();
		}
		return this;
	}


	/**
	 * Sets a control to readonly.
	 */
	public Control<T> setReadOnly()
	{
		return setReadOnly(true);
	}
	
	
	/**
	 * Returns if the control is disabled.
	 */
	public boolean isDisabled()
	{
		return isDisabled_;
	}


	/**
	 * Sets if the control is disabled.
	 */
	public Control<T> setDisabled(boolean disabled)
	{
		if (isDisabled_ != disabled)
		{
			isDisabled_ = disabled;
			updateStatus();
		}
		return this;
	}

	
	/**
	 * Sets the control disabled.
	 */
	public Control<T> setDisabled()
	{
		return setDisabled(true);
	}

	
	//------------------------------
	// status
	//------------------------------

	
	/**
	 * Returns the status of the control.
	 */
	public Status getStatus()
	{
		return status_;
	}

	
	/**
	 * Returns if the control has a certain status.
	 */
	public boolean hasStatus(Status status)
	{
		return status_ == status;
	}

	
	/**
	 * Sets the status of the control.
	 * If the field is part of a form, and the status is not Status.OK,
	 * the field will set itself as the forms error control
	 * @see Form#setErrorControl(Control)
	 */
	public Control<T> setStatus(Status status)
	{
		Check.notNull(status, "status");
		status_ = status;
		if ((status_ != Status.OK) && (form_ != null))
			form_.setErrorControl(this);
		return this;
	}

	
	/**
	 * Called when the value or one of the flags which influence the status
	 * is changed. 
	 */
	private void updateStatus()
	{
		// the control receives status PARSE_ERROR, if 
		// - control has an error set
		// the control receives status OK, if one of the following is true
		// - control has a value
		// - control is disabled
		// - control is readonly
		// - control is not required
		// the control receives status MISSING, if the following is true
		// - control has no value and is required, enabled, and not readonly
		if (hasError())
			setStatus(Status.PARSE_ERROR);
		else if (hasValue() || isDisabled_ || isReadOnly_ || !isRequired_)
			setStatus(Status.OK);
		else
			setStatus(Status.MISSING);
	}

	
	/**
	 * Returns if the control has {@link Status#OK}.
	 */
	public boolean isOk()
	{
		return status_ == Status.OK;
	}
	
	
	//------------------------------------------
	// reload
	//------------------------------------------

	
	/**
	 * Adds a javascript event handler to the control to reload the form if
	 * the control value changes.
	 * The control must already be added to a form, else an exception is thrown.
	 * Additionally, the control determines if the form was {@link Form#isReloaded() reloaded}.
	 * In this case it reads its value from the request, without setting any error (see {@link #readDefault(Request)}.
	 * If the form was submitted, the field reads its value from the request.
	 */
	public void reloadOnChange(T defaultValue)
	{
		setAttribute("onchange", "this.form[this.form.name].value = '" + Form.RELOADED + "'; this.form.submit();");
		Form form = getSafeForm();
		if (form.isReloaded())
			readDefault(form.getRequest());
		else if (form.isSubmitted())
			read(form.getRequest());
	}

	
	//------------------------------------------
	// format
	//------------------------------------------
	
	
	/**
	 * Returns the control value formatted as a string.
	 * If the control has an {@link #getErrorValue()} because
	 * it was initialized from an invalid request, the error value
	 * is returned. Else the value converted to a string is returned.
	 */
	public String format()
	{
		return getErrorValue() != null ? getErrorValue() : formatValue();
	}
	
	
	/**
	 * Returns the control value formatted as a string.
	 */
	protected String formatValue()
	{
		return formatValue(value_);
	}

	
	/**
	 * Returns the control value formatted as a string.
	 */
	protected String formatValue(T value)
	{
		return getResponseSerializer().format(type_, value, getStyle());
	}

	
	protected TypeSerializer getResponseSerializer()
	{
		return form_ != null ?
			form_.getRequest().getResponse().getLocaleSerializer() :
			StandardSerializer.INSTANCE;
	}
	
	
	/**
	 * Returns a style object which is passed to the TypeSerializer when
	 * formatting a value.
	 */
	protected Object getStyle()
	{
		return null;
	}
	
	
	//------------------------------------------
	// print
	//------------------------------------------
	
	
	/**
	 * Prints the control.
	 */
	@Override public void print(TemplateWriter out)
	{
		print(out, (String[])null);
	}
	
	
	/**
	 * Prints the control.
	 * @param attrs a list of attribute names and values which
	 * 		should be printed in the start tag of the control element.
	 */
	public abstract void print(TemplateWriter out, String... attrs);

	
	/**
	 * Prints the generic attributes stored in {@link #attribute_} and
	 * runtime attributes.
	 */
	protected void printAttrs(TemplateWriter out, String... attrs)
	{
		if (attribute_ != null)
			out.print(attribute_);
		HtmlUtil.attrs(out, attrs);
	}
	
	
	/**
	 * Prints a javascript snippet to set the focus to this control.
	 * @param printScript if true the snippet is surrounded by script-tags.
	 */
	public void focus(TemplateWriter out, boolean printScript)
	{
		if (printScript)
			out.println("<script>");
		out.print("document.forms.");
		out.print(getSafeForm().getName());
		out.print(".elements");
		out.print("['");
		out.print(getName());
		out.println("'].focus();");
		if (printScript)
			out.println("</script>");
	}
	

	
	//------------------------------------------
	// conversion
	//------------------------------------------
	
	
	/**
	 * Returns this control or a sub-control of this control which is able to receive the focus.
	 * Returns null if the control (and none of its sub-controls) may not receive the focus
	 * (e.g. HiddenFields). 
	 * The default implementation returns null.
	 */
	public Control<?> toFocusControl()
	{
		return null;
	}
	
	
	/**
	 * Returns this control or a subcontrol of this control which is able to receive input from a user.
	 * Returns null if the control (and none of its sub-control) may not receive input.
	 * (e.g. HiddenFields, Buttons). 
	 * The default implementation returns this.
	 */
	public Control<?> toInputField()
	{
		return this;
	}

	
	/**
	 * Returns a debug representation of the control.
	 */
	@Override public String toString()
	{
		StringBuilder s = new StringBuilder(getClass().getSimpleName());
		s.append('[');
		if (getName() != null)
		{
			s.append(getName());
			s.append('=');
		}
		s.append(getValue());
		s.append(']');
		return s.toString();
	}
	
	
	/**
	 * Tests if two objects are equal
	 */
	protected static boolean equals(Object o1, Object o2)
	{
		return o1 == null ? o2 == null : o1.equals(o2);
	}

	
	private Type<T> type_;
	private T value_;
	private Exception error_;
	private String errorValue_;
	private String name_;
	private boolean isDisabled_;
	private boolean isReadOnly_;
	private boolean isRequired_;
	protected Attribute attribute_;
	protected String label_ = "";
	private Form form_;
	private Object data_;
	private Status status_ = Status.OK;
}
