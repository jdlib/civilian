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
package org.civilian.internal.controller.arg.reqcontent;


import java.io.InputStream;
import java.io.Reader;

import org.civilian.controller.method.arg.MethodArg;


/**
 * Factory class for @RequestContent MethodArgs.
 */
public class ReqContentArgs
{
	public static <T> MethodArg create(Class<?> type, java.lang.reflect.Type genericType)
	{
		if (type == String.class)
			return new ReqContentStringArg();
		else if (type == Reader.class)
			return new ReqContentReaderArg();
		else if (InputStream.class == type)
			return new ReqContentInputStreamArg();
		else if (type.isArray() && (type.getComponentType() == Byte.TYPE))
			return new ReqContenBytesArg();
		else
			return new ReqContentGenericArg(type, genericType);
	}
}
