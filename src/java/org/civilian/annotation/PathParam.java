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
import org.civilian.resource.PathParamMap;
import org.civilian.resource.ResourceConfig;


/**
 * PathParam is an annotation to either define a path-parameter
 * mapping or inject a path parameter.
 * It can be used on
 * <ol>
 * <li>packages containing controller classes
 * <li>controller classes
 * <li>parameters of controller action methods
 * </ol>
 * Its value is the name of a PathParam defined by the application.
 * (see {@link PathParamMap} and {@link ResourceConfig#getPathParams()}).
 * <h1>Used on packages</h1>
 * To qualify a package, create a package-info.java file and place
 * the annotation on its package statement. This means that
 * the package does not map to a constant URL segment, but to
 * URLs that contain the path parameter.
 * <h1>Used on controller classes</h1>
 * To annotate a controller class serves a similar purpose
 * than annotating a controller packages: It maps the controller
 * class to a resource which extends the package resource by the path param. 
 * <h1>Used on parameters of controller action methods</h1>
 * When the action method is invoked then the corresponding
 * path parameter of the request will be injected into the parameter.
 * {@link DefaultValue} can be used to specify a default value if the request contains
 * no such path parameter.
 */
@Target({ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PathParam 
{
	/**
	 * Returns the path parameter name.
	 */
	public String value();
}
