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


import org.civilian.template.HtmlUtil;
import org.civilian.template.CspWriter;
import org.civilian.text.NumberStyle;
import org.civilian.type.Type;
import org.civilian.type.TypeLib;


/**
 * A text field for editing double values.
 * The values should be formatted with the request locale.<br> 
 * HTML5 features are not turned on by default on the double field:
 * The type attribute is set to "text" and not "number" since number
 * cannot work with numbers formatted not in an english locale.
 * You could use {@link #setPattern(String)} to constrain input of values.
 */
public class DoubleField extends InputField<Double>
{
	/**
	 * Creates the DoubleField.
	 * @param name the field name
	 */
	public DoubleField(String name)
	{
		super(name);
	}
	
	
	/**
	 * Returns TypeLib.DOUBLE.
	 */
	@Override public Type<Double> getType()
	{
		return TypeLib.DOUBLE;
	}
	
	
	/**
	 * Sets the minimum allowed value.
	 * @return this
	 */
	public DoubleField setMin(double min)
	{
		min_ = Double.valueOf(min);
		return this;
	}


	/**
	 * Sets the maximum allowed value.
	 * @return this
	 */
	public DoubleField setMax(double max)
	{
		max_ = Double.valueOf(max);
		return this;
	}


	/**
	 * Sets the minimum and maximum allowed values.
	 * @return this
	 */
	public DoubleField setMinMax(double min, double max)
	{
		setMin(min);
		setMax(max);
		return this; 
	}

	
	/**
	 * Sets the step attribute of the text field.
	 * The step attribute specifies the legal number intervals for an element.
	 * @return this
	 */
	public DoubleField setStep(double step)
	{
		step_ = Double.valueOf(step);
		return this;
	}
	
	
	

	/**
	 * Returns the style object previously set by #setStyle().
	 */
	@Override public NumberStyle getStyle()
	{
		return style_;
	}
	
	
	/**
	 * Sets the style object which should be used when formatting the control value.
	 */
	public void setStyle(NumberStyle style)
	{
		style_ = style;
	}

	
	@Override protected void open(CspWriter out, String type, String... attrs)
	{
		super.open(out, type, attrs);
		if (min_ != null)
			HtmlUtil.attr(out, "min", String.valueOf(min_.doubleValue()));
		if (max_ != null)
			HtmlUtil.attr(out, "max", String.valueOf(max_.doubleValue()));
		if (step_ != null)
			HtmlUtil.attr(out, "step", String.valueOf(step_.doubleValue()));
	}
	
	
	private Double min_;
	private Double max_;
	private Double step_;
	private NumberStyle style_;
}
