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
package org.civilian.samples.jpa.jta.model;


import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import org.civilian.samples.jpa.shared.model.Person;


@Stateful
public class EjbPersonService implements PersonService
{
	@Override public boolean persist(Person person)
	{
		try
		{
			em_.persist(person);
			return true;
		}
		catch(RollbackException e)
		{
			return false;
		}
	}
	

	@Override public Person query(String code)
	{
        Query query = em_.createQuery("select p from Person p where p.code = :code");
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
 

	@PersistenceContext(unitName="primary")
	private EntityManager em_;
}
