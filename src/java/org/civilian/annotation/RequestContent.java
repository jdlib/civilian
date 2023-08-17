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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.civilian.application.Application;
import org.civilian.content.ContentSerializer;


/**
 * An annotation to inject the request content
 * into the parameter of a controller action method.
 * The content of the request will be injected into
 * the parameter variable. Conversion to the variable type
 * is automatically done using a {@link ContentSerializer} provided
 * by the {@link Application#getContentSerializer(org.civilian.content.ContentType) application}.<br>
 * Example: If the content has content type application/json, then the
 * parsed JSON data will be injected into the action parameter
 * annotated with this annotation.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestContent 
{
}