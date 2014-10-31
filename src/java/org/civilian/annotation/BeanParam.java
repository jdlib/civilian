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
package org.civilian.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * BeanParam is an annotation to inject multiple requests parameters
 * in the form of a Java bean into a controller method parameter.
 * When the method is invoked, the runtime creates a new object of 
 * the bean class and populates the properties of the bean
 * with request values.<br>
 * These properties are supported:
 * <ul>
 * <li>Bean setter methods. If not otherwise annotated, the request parameter with the name 
 * 	of the bean property will be injected into the property.
 * <li>Other public methods, with a single argument and an annotation what to inject.
 * <li>Public fields. If not otherwise annotated, the request parameter with the name 
 * 	of the field will be injected into the field..
 * </ul>   
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface BeanParam 
{
}
