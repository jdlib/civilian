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
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import org.civilian.samples.jpa.shared.model.Person;


public abstract class PersonService
{
	public static boolean persist(Person person)
	{
		try
		{
			EntityManager em = JpaContext.em();
			em.getTransaction().begin();
			em.persist(person);
			em.getTransaction().commit();
			return true;
		}
		catch(RollbackException e)
		{
			return false;
		}
	}
	
	
	public static Person query(String code)
	{
        Query query = JpaContext.em().createQuery("select p from Person p where p.code = :code");
        query.setParameter("code", code);
        try 
        {
            return (Person)query.getSingleResult();
        } 
        catch (NoResultException e) 
        {
            return null;
        } 
	}
}
