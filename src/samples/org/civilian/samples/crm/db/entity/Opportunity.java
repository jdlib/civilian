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
package org.civilian.samples.crm.db.entity;


public class Opportunity
{
	public Opportunity()
	{
	}
	
	
	public Opportunity(Integer id)
	{
		id_ 		= id;
		int i		= id.intValue();
		name 		= "Name" + i;
		int n		= i + 1;
		volume		= n * 1000 + n * 100;
		probability	= (n * 17) % 100;
	}
	
	
	public Integer getId()
	{
		return id_;
	}


	public String getName()
	{
		return name;
	}	
	
	
	public void setName(String name)
	{
		this.name = name;
	}	

	
	public int getVolume()
	{
		return volume;
	}


	public void setVolume(int volume)
	{
		this.volume = volume;
	}


	public int getProbability()
	{
		return probability;
	}


	public void setProbability(int probability)
	{
		this.probability = probability;
	}


	private Integer id_; 
	private String name; 
	private int volume;
	private int probability;
}
