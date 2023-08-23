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
package org.civilian.text.keys;


import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;
import org.civilian.CivTest;
import org.civilian.text.keys.serialize.KeySerializers;
import org.civilian.text.type.StandardSerializer;
import org.junit.Test;


public class KeyListTest extends CivTest
{
	private enum Abc
	{
		alpha,
		beta,
		gamma
	}
	
	
	@Test public void testEmptyKeyList()
	{
		assertEquals(0, KeyLists.empty().size());
	}


	@Test public void testKeyValueKeyList()
	{
		KeyList<String> list = new KeyListBuilder<String>().add("a", "alpha").end();   
		assertEquals(1, list.size());
		assertItem(list, 0, "a", "alpha");
		assertEquals("KeyList[1]",	list.toString());
		assertEquals("alpha",	list.getText("a"));
		assertEquals(null, 		list.getText("b"));
		assertEquals(-1, 		list.indexOf(null));
		assertEquals(0, 		list.indexOf("a"));
		assertEquals(0, 		list.getTextIndex("alpha"));
		assertEquals("a", 		list.getValue("alpha"));
		assertEquals(null, 		list.getValue("beta"));
	}
	
	
	@Test public void testPureTextKeyList() throws Exception
	{
		KeyList<String> keys = KeyLists.forTexts("a", "b", "c");   
		assertEquals(3, keys.size());
		assertItem(keys, 1, "b", "b");
		assertFormat(keys, 2, "c");
		assertParse(keys, "a", "a");
		assertParseReject(keys, "d");
	}

	
	@Test public void testEnumKeyList() throws Exception
	{
		KeyList<Abc> keys = new EnumKeyList<>(Abc.class);
		assertEquals(3, keys.size());
		assertItem(keys, 1, Abc.beta, "beta");
		
		assertFormat(keys, 2, "gamma");
		assertParse(keys, Abc.beta, "beta");
		assertParseFailure(keys, "delta", IllegalArgumentException.class);
	}
	
	
	@Test public void testTypedSerializer() throws Exception
	{
		Integer one = Integer.valueOf(1); 
		Integer two = Integer.valueOf(2); 
		KeyList<Integer> keys = new KeyListBuilder<Integer>().
			add(one, "one").
			add(two, "two").
			end();   

		assertEquals(2, keys.size());
		assertItem(keys, 0, one, "one");

		assertFormat(keys, 0, "1");
		assertParse(keys, two, "2");
		assertParseFailure(keys, "a", NumberFormatException.class);
		assertParseReject(keys, "3");
	}
	
	
	@Test public void testIndexedSerializer() throws Exception
	{
		KeyList<Locale> keys = new KeyListBuilder<Locale>().
			setSerializer(KeySerializers.TO_INDEX).
			add(Locale.GERMAN, "de").
			add(Locale.ITALIAN, "it").
			end();   
		
		assertItem(keys, 0, Locale.GERMAN, "de");
		assertFormat(keys, 0, "0");
		assertFormat(keys, 1, "1");
		assertParse(keys, Locale.ITALIAN, "1");
		assertParseFailure(keys, "a", NumberFormatException.class);
		assertParseReject(keys, "2");
	}
	
	
	@Test public void testToStringSerializer() throws Exception
	{
		LocalDate date = LocalDate.of(2014, 1, 31);
		ValueKeyList<LocalDate> keys = new ValueKeyList<>(KeySerializers.TO_STRING, date);
		
		assertItem(keys, 0, date, "2014-01-31");
		assertFormat(keys, 0, "2014-01-31");
		assertParse(keys, date, "2014-01-31");
		assertParseFailure(keys, "a", IllegalArgumentException.class);
		assertParseReject(keys, "2");
	}

	
	@Test public void testBuilder() throws Exception
	{
		KeyList<String> empty = new KeyListBuilder<String>().end();
		assertEquals(0, empty.size());
		
		try
		{
			new KeyListBuilder<String>().add(null, "a").end();
			fail();
		}
		catch(IllegalStateException e)
		{
		}
	}
	
	
	@Test public void testMutableKeyList() throws Exception
	{
		Integer one = Integer.valueOf(1); 
		Integer two = Integer.valueOf(2); 
		Integer three = Integer.valueOf(3); 

		MutableKeyList<Integer> keys = new MutableKeyList<>();
		keys.add(one, "eins");
		assertItem(keys, 0, one, "eins");
		keys.setText(0, "uno");
		assertItem(keys, 0, one, "uno");
		keys.setText(one, "une");
		assertItem(keys, 0, one, "une");
		
		assertFormat(keys, 0, "0"); 
		assertParse(keys, one, "0"); 
		assertParseFailure(keys, "a", NumberFormatException.class); 
		assertParseReject(keys, "2"); 

		keys.add(two, "zwei");
		keys.add(three, "three");
		assertEquals(3, keys.size());
		assertEquals(1, keys.indexOf(two));
		assertEquals(2, keys.indexOf(three));
		keys.remove(1);
		assertEquals(-1, keys.indexOf(two));
		keys.remove(three);
		assertEquals(-1, keys.indexOf(three));
		assertEquals(1, keys.size());
		keys.clear();
		assertEquals(0, keys.size());
		
		keys.add(one, "eins");
		assertFormat(keys, 0, "3"); 
	}
	
	
	@Test public void testFailures()
	{
		KeyList<?> emptyList = KeyLists.empty();

		try
		{
			emptyList.getText(0);
			fail();
		}
		catch(IllegalArgumentException e)
		{
		}

		try
		{
			emptyList.getValue(0);
			fail();
		}
		catch(IllegalArgumentException e)
		{
		}
		
		try
		{
			new SimpleKeyList<>(new Integer[0], new String[]{ "text" });
			fail();
		}
		catch(IllegalArgumentException e)
		{
		}
	}


	private <T> void assertItem(KeyList<T> keyList, int index, T value, String text)
	{
		assertEquals(value, keyList.getValue(index));
		assertEquals(text, keyList.getText(index));
	}
	
	
	private <T> void assertFormat(KeyList<T> keyList, int index, String formattedValue)
	{
		T value = keyList.getValue(index);
		assertFormat(keyList, value, formattedValue);
		
		String s = keyList.getType().format(value, index);
		assertEquals(formattedValue, s);
	}


	private <T> void assertFormat(KeyList<T> keyList, T value, String formattedValue)
	{
		String s = StandardSerializer.INSTANCE.format(keyList.getType(), value);
		assertEquals(formattedValue, s);
	}


	private <T> void assertParse(KeyList<T> keyList, T expected, String s) throws Exception
	{
		T actual = StandardSerializer.INSTANCE.parse(keyList.getType(), s);
		assertEquals(expected, actual);
	}


	private <T> void assertParseFailure(KeyList<T> keyList, String s, Class<? extends Exception> ex) throws Exception
	{
		try
		{
			StandardSerializer.INSTANCE.parse(keyList.getType(), s);
			fail();
		}
		catch(ParseException e)
		{
			assertEquals(ex, e.getCause().getClass());
		}
	}


	private <T> void assertParseReject(KeyList<T> keyList, String s) throws Exception
	{
		try
		{
			StandardSerializer.INSTANCE.parse(keyList.getType(), s);
			fail();
		}
		catch(ParseException e)
		{
			assert(e.getCause() instanceof IllegalArgumentException);
			assertEquals("not a valid value '" + s + "'", e.getCause().getMessage());
		}
	}
}
