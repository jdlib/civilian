package org.civilian.text;


import java.util.Locale;
import org.civilian.CivTest;
import org.junit.BeforeClass;
import org.junit.Test;


public class NumberFormatTest extends CivTest
{
	@BeforeClass public static void beforeClass()
	{
		formatDe_ = new NumberFormat(Locale.GERMAN);
		formatUk_ = new NumberFormat(Locale.UK);
	}
	
	
	@Test public void testAccessors()
	{
		assertEquals(',', formatDe_.getDecimalSeparator());
		assertEquals('.', formatUk_.getDecimalSeparator());
		
		assertEquals('.', formatDe_.getGroupingSeparator());
		assertEquals(',', formatUk_.getGroupingSeparator());

		assertEquals(".", formatDe_.getGroupingSepString());
		assertEquals(",", formatUk_.getGroupingSepString());
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
		assertEquals(      "1",	formatDe_.formatNatural(1, style));
		assertEquals(     "12",	formatUk_.formatNatural(12, style));
		assertEquals(    "123",	formatDe_.formatNatural(123, style));
		assertEquals(  "1,234",	formatUk_.formatNatural(1234, style));
		assertEquals( "12.345",	formatDe_.formatNatural(12345, style));
		assertEquals("123,456",	formatUk_.formatNatural(123456, style));
		
		style = NumberStyle.RAW;
		assertEquals(      "1",	formatDe_.formatNatural(1, style));
		assertEquals(     "12",	formatUk_.formatNatural(12, style));
		assertEquals(    "123",	formatDe_.formatNatural(123, style));
		assertEquals(   "1234",	formatUk_.formatNatural(1234, style));
		assertEquals(  "12345",	formatDe_.formatNatural(12345, style));
		assertEquals( "123456",	formatUk_.formatNatural(123456, style));
	}

	
	@Test public void testStyleDecimals()
	{
		NumberStyle style = NumberStyle.DEFAULT;
		assertEquals("1,00",	formatDe_.formatDecimal(1, style));
		assertEquals("1.20",	formatUk_.formatDecimal(1.2, style));
		assertEquals("1,23",	formatDe_.formatDecimal(1.23, style));
		assertEquals("1,23",	formatDe_.formatDecimal(1.239, style));
		assertEquals("1.234,56",formatDe_.formatDecimal(1234.56, style));
		assertEquals("1234.56",	formatUk_.formatDecimal(1234.56, NumberStyle.RAW));
		
		style = NumberStyle.DEFAULT.maxDecimals(3);
		assertEquals("1,00",	formatDe_.formatDecimal(1, style));
		assertEquals("1.20",	formatUk_.formatDecimal(1.2, style));
		assertEquals("1,23",	formatDe_.formatDecimal(1.23, style));
		assertEquals("1,239",	formatDe_.formatDecimal(1.239, style));
		assertEquals("1,239",	formatDe_.formatDecimal(1.2398, style));

		style = NumberStyle.DEFAULT.decimals(0);
	}
	
	
	private static NumberFormat formatDe_;
	private static NumberFormat formatUk_;
}
