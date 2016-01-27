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
package org.civilian.type.lib;


import java.math.BigInteger;
import org.civilian.type.Type;
import org.civilian.type.TypeLib;
import org.civilian.type.TypeSerializer;
import org.civilian.type.TypeVisitor;


/**
 * A type implementation for java.math.BigInteger.
 * @see TypeLib#BIGINTEGER
 */
public class BigIntegerType extends Type<BigInteger>
{
	@Override public Class<BigInteger> getJavaType()
	{
		return BigInteger.class;
	}
	
	
	@Override public String format(TypeSerializer serializer, BigInteger value, Object style)
	{
		return serializer.formatBigInteger(value, style);
	}

	
	@Override public BigInteger parse(TypeSerializer serializer, String s) throws Exception
	{
		return serializer.parseBigInteger(s);
	}
	
	
	@Override public <R,P,E extends Exception> R accept(TypeVisitor<R,P,E> visitor, P param) throws E
	{
		return visitor.visitBigInteger(param);
	}
}
