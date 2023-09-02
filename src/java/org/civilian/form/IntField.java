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
 * A text field for editing integers.
 */
public class IntField extends InputField<Integer>
{
	/**
	 * Creates the IntegerField.
	 * @param name the field name
	 */
	public IntField(String name)
	{
		super(name);
		setInputType(INPUT_TYPE_NUMBER);
	}
	
	
	/**
	 * @return TypeLib.INTEGER.
	 */
	@Override public Type<Integer> getType()
	{
		return TypeLib.INTEGER;
	}

	
	/**
	 * Sets that the field is presented as HTML5 range field.
	 * @return this
	 */
	public IntField asRangeField()
	{
		setInputType("range");
		return this;
	}

	
	/**
	 * Sets the minimum allowed value.
	 * @param min the minimum value
	 * @return this
	 */
	public IntField setMin(int min)
	{
		min_ = Integer.valueOf(min);
		return this;
	}
	
	
	/**
	 * @return the minimum value. 
	 */
	public Integer getMin()
	{
		return min_;
	}
	

	/**
	 * Sets the maximum allowed value.
	 * @param max the maximum value
	 * @return this
	 */
	public IntField setMax(int max)
	{
		max_ = Integer.valueOf(max);
		return this;
	}


	/**
	 * @return the maximum value. 
	 */
	public Integer getMax()
	{
		return max_;
	}
	

	/**
	 * Sets the minimum and maximum allowed values.
	 * @param min the minimum value
	 * @param max the maximum value
	 * @return this
	 */
	public IntField setMinMax(int min, int max)
	{
		setMin(min);
		setMax(max);
		return this; 
	}

	
	/**
	 * Sets the step attribute of the text field.
	 * The step attribute specifies the legal number intervals for an element.
	 * @param step the step value
	 * @return this
	 */
	public IntField setStep(int step)
	{
		step_ = Integer.valueOf(step);
		return this;
	}
	
	
	/**
	 * @return the step value. 
	 */
	public Integer getStep()
	{
		return step_;
	}
	

	/**
	 * @return the style object previously set by #setStyle().
	 */
	@Override public NumberStyle getStyle()
	{
		return style_;
	}
	
	
	/**
	 * Sets the style object which should be used when formatting the control value.
	 * @param style the style
	 */
	public void setStyle(NumberStyle style)
	{
		style_ = style;
	}

	
	@Override protected void open(CspWriter out, String type, String... attrs)
	{
		super.open(out, type, attrs);
		if (min_ != null)
			HtmlUtil.attr(out, "min", min_.intValue());
		if (max_ != null)
			HtmlUtil.attr(out, "max", max_.intValue());
		if (step_ != null)
			HtmlUtil.attr(out, "step", step_.intValue());
	}
	
	
	private Integer min_;
	private Integer max_;
	private Integer step_;
	private NumberStyle style_;
}
