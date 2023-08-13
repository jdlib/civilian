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
package org.civilian.internal.admin.app;


import java.util.ArrayList;
import org.civilian.annotation.Get;
import org.civilian.annotation.Produces;
import org.civilian.annotation.Parameter;
import org.civilian.content.ContentType;
import org.civilian.controller.ControllerMethod;
import org.civilian.controller.ControllerSignature;
import org.civilian.controller.ControllerType;
import org.civilian.template.Template;


/**
 * Controller for the resource "/&lt;app-id&gt;resources".
 */
public class ResourcesController extends AppController
{
	@Override protected Template getContentTemplate()
	{
		ResourcesTemplate t = new ResourcesTemplate(viewedApp_);
		return new AppTemplate(viewedApp_, t, 1); 
	}
	
	
	@Produces(ContentType.Strings.APPLICATION_JSON)
	@Get public void getDetails(@Parameter("controller") String sigString) throws Exception
	{
		ControllerSignature signature = ControllerSignature.parse(sigString, viewedApp_.getControllerConfig().getPathParams()) ;
		ControllerType type = viewedApp_.getControllerService().getControllerType(signature);
		if (type == null)
			getResponse().writeJson("?");
		else
		{
			ArrayList<ActionInfo> infos = new ArrayList<>();
			for (ControllerMethod action : type)
				infos.add(new ActionInfo(action));
			getResponse().writeJson(infos);
		}
	}
	
	
	@SuppressWarnings("unused")
	private static class ActionInfo
	{
		public ActionInfo(ControllerMethod action)
		{
			this.method = action.getJavaMethod().getName();
			this.info   = action.getInfo();
		}

		public final String method;
		public final String info;
	}
}
