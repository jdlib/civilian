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
package org.civilian.type;


/**
 * TypeVisitor allows to implement a Visitor pattern based on types.
 * @see Type#accept(TypeVisitor, Object)
 */
public interface TypeVisitor<RETURN, PARAM, EXC extends Exception>
{
	public RETURN visitArray(PARAM param, Type<?> elementType) throws EXC;

	
	public RETURN visitBoolean(PARAM param) throws EXC;
	
	
	public RETURN visitBigDecimal(PARAM param) throws EXC;
	
	
	public RETURN visitBigInteger(PARAM param) throws EXC;
	

	public RETURN visitByte(PARAM param) throws EXC;

	
	public RETURN visitCharacter(PARAM param) throws EXC;
	
	
	public RETURN visitDate(PARAM param, DateType<?> dateType) throws EXC;

	
	public RETURN visitDouble(PARAM param) throws EXC; 
	
	
	public RETURN visitFloat(PARAM param) throws EXC;

	
	public RETURN visitInteger(PARAM param) throws EXC;

	
	public RETURN visitLong(PARAM param) throws EXC;
	
	
	public RETURN visitShort(PARAM param) throws EXC;


	public RETURN visitString(PARAM param) throws EXC;
}
