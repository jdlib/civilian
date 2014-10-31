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
 * Path is an annotation to define a resource mapping.
 * It can be used on
 * <ol>
 * <li><a href="#p">packages</a> containing controller classes
 * <li><a href="#c">controller classes</a>
 * <li><a href="#m">controller action methods</a>
 * </ol>
 * It allows to override the mapping of a package or controller class
 * to a resource, or to introduce a mapping of a controller method
 * to a resource.
 * You can annotate a package in package-info.java placed in the package
 * directory.
 * The {@link #value() value} of the annotation is a relative path string.
 * It is interpreted as follows:
 * <a name="p"></a>
 * <h1>Used on controller packages</h1>
 * The path string overrides the default mapping of the controller
 * package to a resource, relative to its parent package.
 * The path string must not be empty.<br>
 * Example:<br>
 * Suppose the parent controller package "com.app.contacts" is mapped to resource "/contacts".
 * Package "com.app.contacts.detail" would by default be mapped to 
 * resource "/contacts/detail".<br>
 * But annotated with @Path("showdetails") it would be mapped to resource 
 * "/contacts/showdetails".
 * <a name="c"></a>
 * <h1>Used on controller classes</h1>
 * The path string overrides the default mapping of the controller
 * class to a resource, relative to its package.
 * The path string may be empty.
 * Example:<br>
 * Suppose the controller package "com.app.contacts" is mapped to resource "/contacts".
 * The controller class "com.app.contacts.ViewController" would by default be mapped to 
 * resource "/contacts/view".<br>
 * But annotated with @Path("show") it would be mapped to resource 
 * "/contacts/show". If annotated with @Path("") it would be mapped to resource 
 * "/contacts".
 * <a name="m"></a>
 * <h1>Used on controller action methods</h1>
 */
@Target({ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Path
{
	/**
	 * Returns the path value.
	 */
	public String value();
}
