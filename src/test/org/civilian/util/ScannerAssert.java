package org.civilian.util;


import static org.junit.Assert.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class ScannerAssert
{
	private static final Method NEXT = method("consume", String.class);
	private static final Method NEXT_CHAR = method("consume", Character.class);
	
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
	
	
	public ScannerAssert expectFail(String s)
	{
		expectedFail_ = s;
		return this; 
	}
	
	
	public ScannerAssert next(boolean expected, String s)
	{
		return nextResult(expected, NEXT, s);
	}
	
	
	public ScannerAssert next(boolean expected, char c)
	{
		return nextResult(expected, NEXT_CHAR, c);
	}

	
	private ScannerAssert nextResult(Object expected, Method method, Object... params)
	{
		Object actual = null;
		String actualFail = null;
		String expectedFail = expectedFail_;
		expectedFail_ = null;
		
		try
		{
			actual = method.invoke(scanner_, params);
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new IllegalStateException("unexpected", e);
		}
		catch (Exception e)
		{
			actualFail = e.getMessage();
		}
		
		assertEquals("fail", expectedFail, actualFail);
		if (actualFail != null) 
			assertEquals("result", expected, actual);
		return this;
	}
	
	
	private final Scanner scanner_;
	private String expectedFail_;
}
