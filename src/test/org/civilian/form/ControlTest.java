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


import static org.mockito.Mockito.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;
import org.junit.BeforeClass;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.Request;
import org.civilian.request.Upload;
import org.civilian.template.TestTemplateWriter;
import org.civilian.text.keys.KeyList;
import org.civilian.text.keys.KeyLists;
import org.civilian.text.service.LocaleService;
import org.civilian.type.TypeLib;


public class ControlTest extends CivTest
{
	@BeforeClass public static void beforeClass()
	{
		request = mock(Request.class);
		when(out.response.getRequest()).thenReturn(request);
		when(request.getLocaleService()).thenReturn(new LocaleService(Locale.ENGLISH));
	}
	
	
	@Test public void testSelect()
	{
		Select<String> select = new Select<>("name", KEYS);

		assertSame(KEYS, select.getKeyList());
		assertEquals(0, select.getRows());
		select.setRows(1);
		assertEquals(1, select.getRows());
		assertSame(select, select.toFocusControl());
		
		assertFalse(select.useOptionGroups());
		select.setUseOptionGroups(true);
		assertTrue(select.useOptionGroups());

		select.setUseOptionGroups(false);
		assertFalse(select.useOptionGroups());
		
		assertEquals(-1, select.getSelectedIndex());
		assertEquals(null, select.getSelectedText());
		
		when(request.getParameter(select.getName())).thenReturn(null);
		assertTrue(select.read(request));
		select.setRequired(true);
		assertFalse(select.read(request));
		when(request.getParameter(select.getName())).thenReturn("a");
		assertTrue(select.read(request));
		assertEquals("a", select.getValue());
		when(request.getParameter(select.getName())).thenReturn("x");
		assertFalse(select.read(request));
		assertNull(select.getValue());
		assertEquals("x", select.getErrorValue());

		Select<String> select2 = new Select<>("name2", KeyLists.<String>empty());
		assertOut(select2, "<select name='name2'>\n\t<option value=''></option>\n</select>");
		select2.removeDefaultOption();
		select2.setDisabled();
		select2.setRows(2);
		assertOut(select2, "<select name='name2' size='2' disabled>\n</select>");
	}


	@Test public void testTextField()
	{
		TextField field = new TextField("email");
		assertEquals("email", field.getName());

		assertEquals("text", field.getInputType());
		field.setInputType(TextField.INPUT_TYPE_EMAIL);
		assertEquals("email", field.getInputType());
		
		assertSame(field, field.toFocusControl());
		assertSame(field, field.toInputField());
		
		assertEquals(0, field.getMaxLength());
		assertEquals(0, field.getSize());

		field.setMaxLength(100);
		assertEquals(50, field.getSize());
		assertEquals(100, field.getMaxLength());
		
		field.setSize(25);
		assertEquals(25, field.getSize());
		
		assertEquals(100, field.getMaxLength());
		
		assertNull(field.getPattern());
		field.setPattern("*@*");
		assertEquals("*@*", field.getPattern());
		
		assertNull(field.getPlaceholder());
		field.setPlaceholder("enter email");
		assertEquals("enter email", field.getPlaceholder());
		
		assertNull(field.getId());
		field.setId("xyz");
		assertEquals("xyz", field.getId());
		
		assertNull(field.getLabel());
		assertFalse(field.hasLabel());
		field.setLabel("label");
		assertEquals("label", field.getLabel());
		field.setLabel(null);
		assertNull(field.getLabel());

		assertNull(field.getData());
		field.setData(this);
		assertSame(this, field.getData());
		
		assertEquals(1, field.getRows());
		assertEquals(Control.Category.INPUT, field.getCategory());
		assertEquals("", field.format());
		
		assertEquals("TextField[email=null]", field.toString());
		
		assertOut(field, "<input type='email' name='email' value='' size='25' maxlength='100' id='xyz' placeholder='enter email' pattern='*@*'>");
	}

	
	@Test public void testDateField()
	{
		DateField<LocalDate> field = new DateField<>(TypeLib.DATE_LOCAL, "d");
		
		field.setValue(LocalDate.of(2014, 01, 31));
		assertOut(field, "<input type='text' name='d' value='20140131' size='10' maxlength='10'>");
	}
	
	
	@Test public void testDoubleField()
	{
		DoubleField field = new DoubleField("d");
		
		field.setMin(20.0);
		field.setStep(2.2);
		field.setDoubleValue(40.2);
		assertOut(field, "<input type='text' name='d' value='40.2' min='20.0' step='2.2'>");
	}
	
	
	@Test public void testIntegerField()
	{
		IntField field = new IntField("n");
		assertOut(field, "<input type='number' name='n' value=''>");
		
		field.setName("ifield");
		when(request.getParameter("ifield")).thenReturn("a");
		assertFalse(field.read(request));
		assertEquals(Control.Status.PARSE_ERROR, field.getStatus());
		assertTrue(field.getError() instanceof ParseException);
		assertTrue(field.getError().getCause() instanceof NumberFormatException);
		assertNull(field.getValue());
		assertEquals("a", field.getErrorValue());
		assertEquals("a", field.format());
		
		field.setReadOnly(true);
		assertFalse(field.read(request));
		
		field.setReadOnly(false);
		when(request.getParameter("ifield")).thenReturn("");
		assertTrue(field.read(request));
		assertEquals(null, field.getValue());

		field.setRequired(true);
		assertFalse(field.read(request));
		assertEquals(null, field.getValue());
		
		field = new IntField("n");
		field.setMin(20);
		field.setMax(30);
		field.setStep(2);
		assertOut(field, "<input type='number' name='n' value='' min='20' max='30' step='2'>");
	}
	

	@Test public void testTextArea()
	{
		TextArea field = new TextArea("area");
		assertSame(field, field.toFocusControl());
		assertSame(field, field.toInputField());
		
		assertEquals(TextArea.DEFAULT_ROWS, field.getRows());
		field.setRows(10);
		assertEquals(10, field.getRows());

		assertEquals(TextArea.DEFAULT_COLS, field.getCols());
		field.setCols(100);
		assertEquals(100, field.getCols());
		
		assertEquals(null, field.getValue());
		field.setValue("abc");
		assertEquals("abc", field.getValue());
		
		assertFalse(field.isReadOnly());
		field.setReadOnly(true);
		assertTrue(field.isReadOnly());

		assertOut(field, "<textarea name='area' rows='10' cols='100' readonly>abc</textarea>");
	}
	
	
	@Test public void testCheckbox()
	{
		Checkbox field = new Checkbox("c");
		
		assertNull(field.getText());
		field.setText("gamma");
		assertEquals("gamma", field.getText());

		assertFalse(field.isRequired());
		field.setRequired();
		assertFalse(field.isRequired());
		
		assertTrue(field.isOk());
		assertSame(field, field.toFocusControl());

		assertOut(field, "<label><input type='checkbox' name='c' value='on'>gamma</label>");
		
		field = new Checkbox();
		assertNull(field.getName());

		field = new Checkbox("d", null);
		field.setBooleanValue(true);
		field.setDisabled();
		field.setReadOnly(false);
		assertEquals("d", field.getName());
		assertEquals(null, field.getText());
		assertOut(field, "<input type='checkbox' name='d' disabled checked='checked' value='on'>");
		
		field.setDisabled(false);
		field.setStatus(Checkbox.Status.VALIDATION_ERROR);
		field.setBooleanValue(true);
		field.read(request);
		assertEquals(Checkbox.Status.OK, field.getStatus());
		assertFalse(field.getBooleanValue());
		
		setParam(field.getName(), "on");
		field.read(request);
		assertTrue(field.getBooleanValue());
	}
	
	
	@Test public void testCheckboxGroup()
	{
		CheckboxGroup<String> group = new CheckboxGroup<>("cg", KEYS);
		assertNull(group.getValue());
		assertFalse(group.isChecked("a"));
		group.setValue(new String[] { "a" });
		assertTrue(group.isChecked("a"));
		assertFalse(group.isChecked("b"));
		group.getPrinter(out).print("a", "alpha");
		out.assertOutNormed("<label><input type='checkbox' name='cg' value='a' checked>alpha</label>");
		group.getPrinter(out).print(null, null);
		out.assertOutNormed("<input type='checkbox' name='cg' value=''>");
		
		assertSame(KEYS, group.getKeyList());
		group.getPrinter(out).print(1);
		out.assertOutNormed("<label><input type='checkbox' name='cg' value='b'>beta</label>");
		group.setValue(null);
		group.print(out);
		out.assertOutNormed("<label><input type='checkbox' name='cg' value='a'>alpha</label><br>\n<label><input type='checkbox' name='cg' value='b'>beta</label><br>\n");
	}
	
	
	@Test public void testRadioGroup()
	{
		RadioGroup<String> group = new RadioGroup<>("rg", KEYS);
		assertSame(KEYS, group.getKeyList());

		group.setValue("b");
		group.getPrinter(out).print(null, "alpha");
		out.assertOutNormed("<label><input type='radio' name='rg' value=''>alpha</label>");
		group.getPrinter(out).print("b", null);
		out.assertOutNormed("<input type='radio' name='rg' value='b' checked>");

		group.setValue(null);
		group.getPrinter(out).print(1);
		out.assertOutNormed("<label><input type='radio' name='rg' value='b'>beta</label>");
		group.getPrinter(out).print("b");
		out.assertOutNormed("<label><input type='radio' name='rg' value='b'>beta</label>");
		group.print(out);
		out.assertOutNormed("<label><input type='radio' name='rg' value='a'>alpha</label>\n<label><input type='radio' name='rg' value='b'>beta</label>\n");
		group.setHorizontal(false).print(out);
		out.assertOutNormed("<label><input type='radio' name='rg' value='a'>alpha</label><br>\n<label><input type='radio' name='rg' value='b'>beta</label><br>\n");
	}
	
	
	@Test public void testHiddenField()
	{
		HiddenField<String> field = HiddenField.create("hidden");
		assertOut(field, "<input type='hidden' name='hidden' value=''>");
		assertEquals(Control.Category.HIDDEN, field.getCategory());
		assertNull(field.toInputField());

		field = HiddenField.create("hidden", "some");
		assertEquals("some", field.getValue());
		
		HiddenField<Integer> ihidden = new HiddenField<>("ihidden", TypeLib.INTEGER); 
		ihidden.setIntValue(12);
		assertOut(ihidden, "<input type='hidden' name='ihidden' value='12'>");
	}
	
	
	@Test public void testFileField()
	{
		FileField field = new FileField("photo");
		
		Form form = new Form(request);
		assertFalse(form.isMultipartEncoded());
		form.add(field);
		assertTrue(form.isMultipartEncoded());

		assertOut(field, "<input type='file' name='photo'>");
		
		Upload upload = mock(Upload.class);
		when(request.getUploads("photo")).thenReturn(new Upload[] { upload });
		field.read(request);
		assertSame(upload, field.getUpload());
	}
	

	@Test public void testPasswordField()
	{
		PasswordField field = new PasswordField("pwd");
		field.setValue("1234");
		assertOut(field, "<input type='password' name='pwd' value=''>");
	}
	

	@Test public void testButton()
	{
		Form form = new Form(request);
		Button button; 

		button = Button.button("OK");
		assertOut(button, "<button>OK</button>");
		assertEquals(null, button.getName());
		form.add(button);
		assertEquals(form.getName() + "_button", button.getName());

		button = Button.reset("Reset");
		button.setDisabled();
		assertOut(button, "<button type='reset' disabled>Reset</button>");

		button = Button.submit("Submit");
		button.setName("submit");
		assertOut(button, "<button type='submit' name='submit' value='Submit'>Submit</button>");
		form.add(button);
		assertEquals("submit", button.getName());
		Button defaultButton = button; 
		assertSame(form.getDefaultButton(), button);

		button = Button.inputButton("OK");
		assertOut(button, "<input type='button' value='OK'>");

		button = Button.inputReset("Reset");
		assertOut(button, "<input type='reset' value='Reset'>");

		button = Button.inputSubmit("Submit");
		button.setOnClick("alert()");
		assertOut(button, "<input type='submit' value='Submit' onclick='alert()'>");
		button.end(out);
		out.assertOut("");
		form.add(button);
		assertSame(defaultButton, form.getDefaultButton());

		// misc
		assertTrue(button.read(null));
		assertEquals(Control.Category.BUTTON, button.getCategory());

		assertFalse(defaultButton.isClicked());
		setParam(form.getName(), "");
		assertTrue(defaultButton.isClicked());
		assertFalse(button.isClicked());
		setParam(form.getName(), "reload");
		assertFalse(defaultButton.isClicked());
		setParam(defaultButton.getName(), "x");
		assertFalse(defaultButton.isClicked());
		setParam(defaultButton.getName(), defaultButton.getValue());
		assertTrue(defaultButton.isClicked());
		
		setParam(defaultButton.getName(), null);
		assertFalse(defaultButton.isDirectlyClicked());
		setParam(defaultButton.getName(), "x");
		assertFalse(defaultButton.isDirectlyClicked());
		setParam(defaultButton.getName(), defaultButton.getValue());
		assertTrue(defaultButton.isDirectlyClicked());
	}
	
	
	@Test public void testFormAdd() throws Exception
	{
		Form form = new Form(request);
		assertEquals(0, form.size());
		
		Button button = Button.button("OK");
		form.add(button);
		assertEquals(1, form.size());
		
		TextField field = new TextField("name");
		form.add(field, "Name");
		form.add(null); // ignored
		form.add(null, "Name"); // ignored
		assertEquals(2, form.size());
		assertSame(field, form.get(1));
		assertSame(field, form.get("name"));
		assertNull(form.get("x"));
		assertNull(form.get(null));
		try
		{
			// cannot add twice
			form.add(field);
			fail();
		}
		catch(IllegalStateException e)
		{
		}
		
		form.setRequired(true);
		assertTrue(field.isRequired());
		form.setReadOnly(true);
		assertTrue(field.isReadOnly());
		
		assertTrue(form.validate(true));
		
		form.remove(field);
		assertEquals(1, form.size());
	}
		
	
	@Test public void testFormRead() throws Exception
	{
		Form form = new Form(request);
		
		TextField field = new TextField("name");
		form.add(field, "Name");
		field.setRequired(true);
		
		form.clearErrorControl();
		when(request.getRequest()).thenReturn(request);
		when(request.getParameter("name")).thenReturn(null);
		assertFalse(form.read());
		assertFalse(form.isOk());
		assertSame(field, form.getErrorControl());

		when(request.getParameter("name")).thenReturn("John");
		assertTrue(form.read());
		assertTrue(form.isOk());
		assertNull(form.getErrorControl());
	}
	
	
	@Test public void testFormAttrs()
	{
		Form form = new Form(request);
		assertNull(form.getTarget());
		form.setTarget("blank");
		assertEquals("blank", form.getTarget());

		assertNull(form.getAction());
		form.setAction("test.html");
		assertEquals("test.html", form.getAction());

		assertEquals("POST", form.getMethod());
		form.setGetMethod();
		assertEquals("GET", form.getMethod());
		form.setPostMethod();
		assertEquals("POST", form.getMethod());
	}
	
	
	private void assertOut(Control<?> field, String expected)
	{
		field.print(out);
		out.assertOutNormed(expected);
	}


	private void setParam(String name, String value)
	{
		when(request.getParameter(name)).thenReturn(value);
	}
	
	
	private static Request request;
	private static TestTemplateWriter out = TestTemplateWriter.create();
	private static KeyList<String> KEYS = KeyLists.forContent(new String[]{ "a", "b"}, new String[]{ "alpha", "beta"});
}
