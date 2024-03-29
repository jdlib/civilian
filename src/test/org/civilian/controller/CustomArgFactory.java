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
package org.civilian.controller;


import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.civilian.controller.method.arg.MethodArg;
import org.civilian.controller.method.arg.MethodArgProvider;


public class CustomArgFactory implements MethodArgProvider
{
	@Override public MethodArg create(
		Annotation annotation, Class<?> paramType, Type genericParamType,
		Annotation[] paramAnnotations) throws Exception
	{
		return new CustomArg();
	}
}
