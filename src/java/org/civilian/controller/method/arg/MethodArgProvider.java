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
package org.civilian.controller.method.arg;


import java.lang.annotation.Annotation;
import java.lang.reflect.Type;


/**
 * MethodArgProvider is a service which can create custom
 * MethodArgs.
 */
public interface MethodArgProvider
{
	/**
	 * Creates a custom MethodArg, for instance for the parameter
	 * of a controller action method. 
	 * @param factory an MethodArgFactory which can help to create standard MethodArgs.
	 * @param annotation the annotation on the method parameter which referenced the MethodArgProvider.
	 * @param type the type of the injected argument
	 * @param genericType the generic type of the injected argument
	 * @param annotations the annotations present on the injection target.
	 * @return an MethodArg implementation suitable to create an injectable value
	 * 		from a request
	 * @throws Exception thrown if MethodArg creation runs in an error
	 */
	public MethodArg create(Annotation annotation,
		Class<?> type, 
		Type genericType,
		Annotation[] annotations) throws Exception; 
}
