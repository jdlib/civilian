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
package org.civilian.samples.jpa.reslocal.model;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import org.civilian.util.Check;


public class JpaContext
{
	private static final ThreadLocal<EntityManager> EM_THREADLOCAL = new ThreadLocal<>();

	
	public static void setEntityManagerFactory(EntityManagerFactory emf)
	{
		emf_ = Check.notNull(emf, "emf");
	}
	
	
	public static EntityManagerFactory getEntityManagerFactory()
	{
		if (emf_ == null)
			throw new IllegalStateException("EntityManagerFactory not initialized");
		return emf_;
	}
	
	
	/**
	 * Returns the window associated with the current thread.
	 * If the current thread is not processing a request, an exception is thrown.
	 */
	public static EntityManager em()
	{
		EntityManager em = EM_THREADLOCAL.get();
		if (em == null)
		{
			em = getEntityManagerFactory().createEntityManager();
			EM_THREADLOCAL.set(em);
		}
		return em;
	}
	
	
	public static EntityManager setEm(EntityManager em)
	{
		return setEm(em, false);
	}
	
	
	public static EntityManager setEm(EntityManager em, boolean commit)
	{
		closeEm(false);
		EM_THREADLOCAL.set(em);
		return em;
	}

	
	public static void closeEm(boolean commit)
	{
		EntityManager em = EM_THREADLOCAL.get();
		if (em != null)
		{
			EM_THREADLOCAL.remove();
			try
			{
				EntityTransaction txn = em.getTransaction();
				if (txn.isActive())
				{
					if (commit)
						txn.commit();
					else
						txn.rollback();
				}
			}
			finally
			{
				em.close();
			}
		}
	}
	
	
	private static EntityManagerFactory emf_;
}
