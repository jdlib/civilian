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
package org.civilian.template;


/**
 * ComponentBuilder is an object which knows
 * how to write a component. A component consists of a start block, arbitrary content,
 * and a end block. It is used by the []-operator of CSP templates.
 */
public interface ComponentBuilder
{
	/**
	 * Asks the ComponentBuilder to print the start of the component.
	 * @param multiLine defines if the content of the component spans multiple lines
	 */
	public void startComponent(boolean multiLine);


	/**
	 * Asks the ComponentBuilder to print the end of the component.
	 * @param multiLine defines if the content of the component spans multiple lines
	 */
	public void endComponent(boolean multiLine);
}
