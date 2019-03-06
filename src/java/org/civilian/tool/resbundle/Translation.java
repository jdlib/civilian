package org.civilian.tool.resbundle;


import org.civilian.util.Check;


class Translation implements Comparable<Translation>
{
	public Translation(String id, int count)
	{
		this.id = Check.notEmpty(id, "id");
		this.lang = new String[count];
	}
	

	@Override public int compareTo(Translation o)
	{
		return id.compareTo(o.id);
	}
	
	
	public final String id;
	public final String[] lang;
}
