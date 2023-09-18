package org.civilian.util;


import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class ScannerAssert
{
	private static final Method NEXT = method("next", String.class);
	private static final Method NEXT_CHAR = method("next", char.class);
	private static final Method NEXT_KEYWORD = method("nextKeyword", String.class);
	private static final Method NEXT_IDENTIFIER = method("nextIdentifier");
	private static final Method NEXT_QUOTEDSTRING = method("nextQuotedString");
	private static final Method NEXT_QUOTEDSTRING_CHAR = method("nextQuotedString", char.class);
	private static final Method NEXT_QUOTEDSTRING_BOOLEAN = method("nextQuotedString", boolean.class);
	private static final Method NEXT_QUOTEDSTRING_CHAR_BOOLEAN = method("nextQuotedString", char.class, boolean.class);
	private static final Method NEXT_TOKEN = method("nextToken");
	private static final Method NEXT_TOKEN_DELIMS = method("nextToken", String.class);
	private static final Method NEXT_UPTOPOS = method("nextUptoPos", int.class);
	
	
	private static Method method(String name, Class<?>... parameterTypes)
	{
		try
		{
			return Scanner.class.getMethod(name, parameterTypes);
		} 
		catch (Exception e)
		{
			throw new ExceptionInInitializerError(e);
		}
	}
	
	
	public ScannerAssert(Scanner scanner)
	{
		scanner_ = scanner;	
	}
	
	
	public Scanner scanner()
	{
		return scanner_;
	}
	
	
	public ScannerAssert current(int expected)
	{
		assertEquals("current", expected, scanner_.current());
		return this;
	}
	
	
	public ScannerAssert length(int expected)
	{
		assertEquals("length", expected, scanner_.getLength());
		return this;
	}

	
	public ScannerAssert line(String expected)
	{
		assertEquals("line", expected, scanner_.getLine());
		return this;
	}

	
	public ScannerAssert inputLineCount(int expected)
	{
		assertEquals("lineCount", expected, scanner_.input.getLineCount());
		return this;
	}

	
	public ScannerAssert inputLineIndex(int expected)
	{
		assertEquals("lineIndex", expected, scanner_.input.getLineIndex());
		return this;
	}
	
	
	public ScannerAssert inputNextLine(boolean expected)
	{
		assertEquals("nextLine", expected, scanner_.input.nextLine());
		return this;
	}
	

	public ScannerAssert pos(int expected)
	{
		assertEquals("pos", expected, scanner_.getPos());
		return this;
	}
	
	
	public ScannerAssert hasMore(boolean expected)
	{
		assertEquals("hasMore", expected, scanner_.hasMoreChars());
		return this;
	}
	
	
	public ScannerAssert rest(String expected)
	{
		assertEquals("getRest", expected, scanner_.getRest());
		return this;
	}

	
	public ScannerAssert expect()
	{
		scanner_.expect();
		return this; 
	}

	
	public class NextResult
	{
		private NextResult(Method method, Object... params)
		{
			Object actual = null;
			String actualError = null;
			try
			{
				actual = method.invoke(scanner_, params);
			}
			catch (InvocationTargetException e)
			{
				actualError = e.getCause().getMessage();
			}
			catch (Exception e)
			{
				actualError = e.getMessage();
			}
			this.actual = actual;
			this.actualError = actualError;
		}
		
		
		public ScannerAssert returns(Object expected)
		{
			if (actualError != null)
				fail("was error: " + actualError);
			assertEquals(expected, actual);
			return ScannerAssert.this;
		}
		
		
		public ScannerAssert fails(String expectedError)
		{
			if (actualError == null)
				fail("was result: " + actual);
			if (!actualError.contains(expectedError))
				fail("actual error '" + actualError + "' does not contain expected '" + expectedError + "'");
			return ScannerAssert.this;
		}

		
		private final Object actual;
		private final String actualError;
	}
	
	
	public NextResult next(String s)
	{
		return new NextResult(NEXT, s);
	}
	
	
	public NextResult next(char c)
	{
		return new NextResult(NEXT_CHAR, c);
	}
	
	
	public NextResult nextKeyword(String s)
	{
		return new NextResult(NEXT_KEYWORD, s);
	}

	
	public NextResult nextIdentifier()
	{
		return new NextResult(NEXT_IDENTIFIER);
	}

	
	public NextResult nextQuotedString()
	{
		return new NextResult(NEXT_QUOTEDSTRING);
	}

	
	public NextResult nextQuotedString(boolean includeQuotes)
	{
		return new NextResult(NEXT_QUOTEDSTRING_BOOLEAN, includeQuotes);
	}

	
	public NextResult nextQuotedString(char quote, boolean includeQuotes)
	{
		return new NextResult(NEXT_QUOTEDSTRING_CHAR_BOOLEAN, quote, includeQuotes);
	}

	
	public NextResult nextQuotedString(char quote)
	{
		return new NextResult(NEXT_QUOTEDSTRING_CHAR, quote);
	}

	
	public NextResult nextToken()
	{
		return new NextResult(NEXT_TOKEN);
	}
	
	
	public NextResult nextToken(String delimiters)
	{
		return new NextResult(NEXT_TOKEN_DELIMS, delimiters);
	}

	
	public NextResult nextUptoPos(int pos)
	{
		return new NextResult(NEXT_UPTOPOS, pos);
	}

	
	private final Scanner scanner_;
}
