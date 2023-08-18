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
package org.civilian.util;


import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;


/**
 * PercentEncoder is a utility class to convert a character to the string "%xy"
 * where xy is the hex value of the utf-8 encoded character.
 */
public class PercentEncoder
{
	public PercentEncoder()
	{
		encoder_ 	= StandardCharsets.UTF_8.newEncoder();
		charBuffer_	= CharBuffer.allocate(1);
		byteBuffer_	= ByteBuffer.allocate(4);
	}
	
	
	public int convert(char c)
	{
		charBuffer_.clear();
		charBuffer_.put(c);
		charBuffer_.rewind();
		byteBuffer_.clear();
		
		encoder_.reset();
		encoder_.encode(charBuffer_, byteBuffer_, true /*end of input*/);
		encoder_.flush(byteBuffer_);
		
		return Math.min(4,  byteBuffer_.position());
	}
	
	
	public int getResult(int i)
	{
		return byteBuffer_.get(i);
	}
	

	public void escape(char c, StringBuilder out)
	{
		int len = convert(c);
		
		for (int i=0; i<len; i++)
		{
			int b = getResult(i);
			if (b < 0)
				b += 256;
			out.append('%');
			out.append(HEX_DIGITS[b / 16]);
			out.append(HEX_DIGITS[b % 16]);
		}
	}
	
	
	public String escape(char c)
	{
		StringBuilder out = new StringBuilder();
		escape(c, out);
		return out.toString();
	}
	
	
	private ByteBuffer byteBuffer_; 
	private CharBuffer charBuffer_; 
	private CharsetEncoder encoder_;
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
}
