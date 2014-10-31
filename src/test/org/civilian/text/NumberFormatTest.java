package org.civilian.text;


import java.util.Locale;
import org.civilian.CivTest;
import org.junit.BeforeClass;
import org.junit.Test;


public class NumberFormatTest extends CivTest
{
	@BeforeClass public static void beforeClass()
	{
		formatDe_ = NumberFormat.getInstance(Locale.GERMAN, false);
		formatUk_ = NumberFormat.getInstance(Locale.UK, true);
	}
	
	
	@Test public void testAccessors()
	{
		assertEquals(',', formatDe_.getDecimalSeparator());
		assertEquals('.', formatUk_.getDecimalSeparator());
		
		assertEquals('.', formatDe_.getGroupingSeparator());
		assertEquals(',', formatUk_.getGroupingSeparator());

		assertEquals(".", formatDe_.getGroupingSeparatorString());
		assertEquals(",", formatUk_.getGroupingSeparatorString());
	}

	
	@Test public void testFormatSimple()
	{
		assertEquals(       "4", formatDe_.formatNatural(      4));
		assertEquals(     "234", formatDe_.formatNatural(    234));
		assertEquals(   "1.234", formatDe_.formatNatural(   1234));
		assertEquals(   "1,234", formatUk_.formatNatural(   1234));
		assertEquals(  "12.345", formatDe_.formatNatural(  12345));
		assertEquals( "234,567", formatUk_.formatNatural( 234567));
		assertEquals(      "-4", formatDe_.formatNatural(     -4));
		assertEquals(    "-234", formatDe_.formatNatural(   -234));
		assertEquals(  "-1.234", formatDe_.formatNatural(  -1234));
		assertEquals(  "-1,234", formatUk_.formatNatural(  -1234));
		assertEquals( "-12.345", formatDe_.formatNatural( -12345));
		assertEquals("-234,567", formatUk_.formatNatural(-234567));
	}
	

	@Test public void testPredefinedStyles()
	{
		assertEquals(2, NumberStyle.DEFAULT.minDecimals());
		assertEquals(2, NumberStyle.DEFAULT.maxDecimals());
		assertEquals(true, NumberStyle.DEFAULT.useGrouping());

		assertEquals( 0, NumberStyle.RAW.minDecimals());
		assertEquals(Integer.MAX_VALUE, NumberStyle.RAW.maxDecimals());
		assertEquals(false, NumberStyle.RAW.useGrouping());
	}

	
	@Test public void testStyleCreation()
	{
		assertEquals( 0, NumberStyle.DEFAULT.minDecimals(0).minDecimals());
		assertEquals( 7, NumberStyle.DEFAULT.maxDecimals(7).maxDecimals());
		assertEquals(false, NumberStyle.DEFAULT.useGrouping(false).useGrouping());
	}

	
	@Test public void testStyleWholeNumbers()
	{
		NumberStyle style = NumberStyle.DEFAULT;
		assertEquals(      "1",	style.formatNatural(formatDe_, 1));
		assertEquals(     "12",	style.formatNatural(formatUk_, 12));
		assertEquals(    "123",	style.formatNatural(formatDe_, 123));
		assertEquals(  "1,234",	style.formatNatural(formatUk_, 1234));
		assertEquals( "12.345",	style.formatNatural(formatDe_, 12345));
		assertEquals("123,456",	style.formatNatural(formatUk_, 123456));
		
		style = NumberStyle.RAW;
		assertEquals(      "1",	style.formatNatural(formatDe_, 1));
		assertEquals(     "12",	style.formatNatural(formatUk_, 12));
		assertEquals(    "123",	style.formatNatural(formatDe_, 123));
		assertEquals(   "1234",	style.formatNatural(formatUk_, 1234));
		assertEquals(  "12345",	style.formatNatural(formatDe_, 12345));
		assertEquals( "123456",	style.formatNatural(formatUk_, 123456));
	}

	
	@Test public void testStyleDecimals()
	{
		assertEquals("1,00",	NumberStyle.DEFAULT.formatDecimal(formatDe_, 1));
		assertEquals("1.20",	NumberStyle.DEFAULT.formatDecimal(formatUk_, 1.2));
		assertEquals("1,23",	NumberStyle.DEFAULT.formatDecimal(formatDe_, 1.23));
		assertEquals("1,23",	NumberStyle.DEFAULT.formatDecimal(formatDe_, 1.239));
		assertEquals("1.234,56",NumberStyle.DEFAULT.formatDecimal(formatDe_, 1234.56));
		assertEquals("1234.56",	NumberStyle.RAW.formatDecimal(formatUk_, 1234.56));
		
		NumberStyle style = NumberStyle.DEFAULT.maxDecimals(3);
		assertEquals("1,00",	style.formatDecimal(formatDe_, 1));
		assertEquals("1.20",	style.formatDecimal(formatUk_, 1.2));
		assertEquals("1,23",	style.formatDecimal(formatDe_, 1.23));
		assertEquals("1,239",	style.formatDecimal(formatDe_, 1.239));
		assertEquals("1,239",	style.formatDecimal(formatDe_, 1.2398));

		style = NumberStyle.DEFAULT.decimals(0);
	}
	
	
	private static NumberFormat formatDe_;
	private static NumberFormat formatUk_;
}
