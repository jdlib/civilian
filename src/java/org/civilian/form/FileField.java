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
import org.civilian.request.Upload;
import org.civilian.template.HtmlUtil;
import org.civilian.template.CspWriter;
import org.civilian.type.InvalidType;
import org.civilian.type.Type;


/**
 * FileField is a control which allows the user to upload 
 * one or more files. On the HTML side it is input element with
 * type attribute set to "file" and - for multiple file upload -
 * its multiple attribute set.<br>
 * The value of the FileField is an array of {@link Upload} objects:
 * <ul>
 * <li>If the FileField was not yet initialized from a request, the value is null.
 * <li>If is was read from a request, the value is a non-null array.
 *		It has length 0, if the request did not contain any uploaded files with the FileFields
 * 		parameter name. If you did not allow {@link #setMultiple(boolean) multiple} files,
 * 		then the max length of the array is 1.
 * </ul>
 * If included in a form, the form will be automatically multipart encoded.<br>
 * When the form is submitted the FileField will make the corresponding
 * {@link Upload} object available via the convenience methods {@link #getUpload()} or {@link #getUploads()}.
 * @see Form#setMultipartEncoded(boolean)
 * @see Request#getUpload(String)
 */
public class FileField extends Control<Upload[]>
{
	/**
	 * Creates the FileField.
	 * @param name the field name
	 */
	public FileField(String name)
	{
		super(name);
	}
	
	
	@Override public Type<Upload[]> getType()
	{
		return InvalidType.<Upload[]>instance();
	}

	
	/**
	 * Set if multiple files can be uploaded. The default is false.
	 * @return this 
	 */
	public FileField setMultiple(boolean multiple)
	{
		multiple_ = multiple;
		return this;
	}
	
	
	/**
	 * Prints the field.
	 */
	@Override public void print(CspWriter out, String... attrs)
	{
		out.print("<input type=\"file\"");
		HtmlUtil.attr(out, "name", getName());
		if (isReadOnly())
			out.print(" readonly");
		if (isDisabled())
			out.print(" disabled");
		if (isRequired())
			out.print(" required");
		printAttrs(out, attrs);
		if (multiple_)
			out.print(" multiple");
		out.print('>');
	}
	
	
	/**
	 * Returns "". A FileField can only receive values from
	 * the client, but does not push values to it.
	 */
	@Override public String format()
	{
		return "";
	}
	
	
	@Override protected void parse(Request request)
	{
		uploads_ = request.getUploads().getAll(getName());
		setValue(uploads_);
	}
	
	
	/**
	 * Makes sure that the form is multipart encoded.
	 */
	@Override protected void setForm(Form form)
	{
		super.setForm(form);
		form.setMultipartEncoded(true);
	}
	
	
	/**
	 * Returns the first upload object
	 * after the field value was read from a request.
	 * @return the upload or null, if the request did not contain any Uploads.  
	 */
	public Upload getUpload()
	{
		return uploads_.length > 0 ? uploads_[0] : null;
	}
	
	
	/**
	 * Returns the Uploads of this file field,
	 * after the field value was read from a request.
	 * @return an array of Upload objects. Length is 0, if the 
	 * 	  	the field was not yet initialized from a request or
	 * 		the request does not contain any uploaded files.
	 * 		If this FileField does not allow multiple uploads,
	 * 		the length is 0 or 1.
	 */
	public Upload[] getUploads()
	{
		return uploads_;
	}

	
	private Upload[] uploads_ = Upload.EMPTY_LIST;
	private boolean multiple_;
}
