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


import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.civilian.util.Check;


/**
 * A Template implementation which uses a Velocity template.
 */
public class VelocityTemplate extends org.civilian.template.Template
{
	public VelocityTemplate(String templateFile, VelocityContext context)
	{
		this(Velocity.getTemplate(templateFile), context);
	}
	
	
	public VelocityTemplate(org.apache.velocity.Template template, VelocityContext context)
	{	
		template_	= Check.notNull(template, "template");
		context_ 	= context;
	}

	
	@Override protected void print() throws Exception
	{
		template_.merge(context_, out);
	}
	
	
	private final org.apache.velocity.Template template_;
	private final VelocityContext context_;
}
