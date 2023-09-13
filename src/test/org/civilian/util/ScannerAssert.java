package org.civilian.util;


import static org.junit.Assert.assertEquals;


public class ScannerAssert
{
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
	
	
	private final Scanner scanner_;
}
