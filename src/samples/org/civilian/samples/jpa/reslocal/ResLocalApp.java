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
 package org.civilian.samples.jpa.reslocal;


import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.civilian.Application;
import org.civilian.application.AppConfig;
import org.civilian.samples.jpa.reslocal.model.JpaContext;


public class ResLocalApp extends Application
{
	@Override protected void init(AppConfig config) throws Exception
	{
		// get the entity-manager-factory defined by the
		// persisten-unit "reslocal" in META-INF/services/persistence.xml
		emFactory_ = Persistence.createEntityManagerFactory("reslocal");
		JpaContext.setEntityManagerFactory(emFactory_);
	}

	
	public EntityManagerFactory getEmFactory()
	{
		return emFactory_;
	}
	

	@Override protected void close() throws Exception
	{
		if (emFactory_ != null)
			emFactory_.close();
	}
	
	
	private EntityManagerFactory emFactory_;
}
