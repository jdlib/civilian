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
 package org.civilian.samples.crm.db;


import java.util.ArrayList;
import org.civilian.util.Check;


public class SearchResult
{
	public SearchResult(String... cols)
	{
		this.cols = Check.notEmpty(cols, "cols");
	}

	
	public void addRow(Integer id, Object... data)
	{
		Check.notEmpty(data, "data");
		if (data.length != cols.length)
			throw new IllegalArgumentException();
		rows.add(new Row(id, data));
	}

	
	private static class Row
	{
		public Row(Integer id, Object[] data)
		{
			this.id = id;
			this.data = data;
		}
		
		// only used to be sent as json to the client 
		@SuppressWarnings("unused")
		public Integer id;
		@SuppressWarnings("unused")
		public Object[] data;
	}
	
	
	private String cols[];
	private ArrayList<Row> rows = new ArrayList<>();
}
