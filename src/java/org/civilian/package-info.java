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
/**
 * Contains the main classes of the framework.
 * <ul>
 * <li>{@link org.civilian.Context} represents the context in which Civilian applications are running.
 * <li>{@link org.civilian.Application} is the base class for Civilian applications.
 * <li>{@link org.civilian.Processor} is the entry point for request processing.
 * <li>{@link org.civilian.Controller} handles requests to application resources. The main task in writing 
 * 		a Civilian application is to implement controller classes.
 * <li>{@link org.civilian.Request} represents a request for a resource.  
 * <li>{@link org.civilian.Resource} models a resource of the application, addressable by a URL.
 * <li>{@link org.civilian.Response} represents the response to a request.
 * <li>{@link org.civilian.template.Template} is the main tool to build complex textual response content.
 * </ul>
 */
package org.civilian;