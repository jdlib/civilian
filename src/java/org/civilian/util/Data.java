package org.civilian.util;


/**
 * Data represents a list of values which can be retrieved by their class.
 */
public class Data 
{
	public static final Data EMPTY = new SealedData();
	private static final Object[] EMPTY_VALUES = new Object[0];
	
	
	public Data()
	{
	}
	
	
	protected Data(Object... values)
	{
		values_ = values;
	}
	
	
	public int size()
	{
		return values_.length;
	}

	
	/**
	 * Adds the value.
	 * @param value a non-null value
	 */
	public void add(Object value)
	{
		Check.notNull(value, "value");
		if (find(value) < 0)
			values_ = ArrayUtil.addLast(values_, value);
	}
	
	
	/**
	 * Adds the value.
	 * @param value a non-null value
	 */
	public void addAll(Object... values)
	{
		if (values != null)
		{
			for (Object value : values)
				add(value);
		}
	}

	
	/**
	 * Returns the first value that has the given class.
	 * @param cls a class
	 * @return the value or null if not found.
	 */
	public <T> T get(Class<? extends T> cls)
	{
		for (Object value : values_)
		{
			T t = ClassUtil.unwrap(value, cls);
			if (t != null)
				return t;
		}
		return null;
	}
	
	
	/**
	 * Returns the first attribute object of the TemplateWriter that has
	 * the given class.  
	 * @throws IllegalStateException if there is no such object,
	 */
	public <T> T getRequired(Class<? extends T> cls)
	{
		T value = getRequired(cls);
		if (value != null)
			return value;
		throw new IllegalStateException("no value for " + cls.getName());
	}
	
	
	private int find(Object value)
	{
		for (int i=0; i<values_.length; i++)
		{
			if (values_[i] == value)
				return i;
		}
		return -1;
	}
	

	public void remove(Object value)
	{
		int p = find(value);
		if (p >= 0)
			values_ = ArrayUtil.removeAt(values_, p);
	}
	
	
	public void clear()
	{
		values_ = EMPTY_VALUES;
	}
	
	
	public Data seal()
	{
		return new SealedData(values_);
	}
	
	
	private Object[] values_ = EMPTY_VALUES;
}


class SealedData extends Data
{
	public SealedData(Object... values)
	{
		super(values);
	}
	

	@Override public void add(Object value)
	{
		sealed();
	}
	
	
	@Override public void remove(Object value)
	{
		sealed();
	}
	
	
	@Override public void clear()
	{
		sealed();
	}
	
	
	private void sealed()
	{
		throw new UnsupportedOperationException("this Data object is sealed"); 
	}
}
