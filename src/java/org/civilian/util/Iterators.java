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
package org.civilian.util;


import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * Provides iterators and collection utility methods.
 */
public abstract class Iterators
{
	/**
	 * @return an empty Iterator. 
	 */
	public static <T> Iterator<T> empty()
	{
		return List.<T>of().iterator(); 
	}

	
	/**
	 * @return an Iterator for an array. 
	 */
	@SafeVarargs
	public static <T,S extends T> Iterator<T> forValues(S... array)
	{
		return forValues(array, 0, array.length);
	}
	

	/**
	 * @return an Iterator for an array part. 
	 */
	public static <T,S extends T> Iterator<T> forValues(S[] array, int start, int end)
	{
		return new ArrayIt<>(array, start, end);
	}

	
	/**
	 * @return an Iterator which wraps a enumeration. 
	 */
	public static <T> Iterator<T> asIterator(Enumeration<T> enumeration)
	{
		return new EnumerationIt<>(enumeration);
	}

	
	/**
	 * @return an enumeration which wraps an iterator. 
	 */
	public static <T> Enumeration<T> asEnumeration(Iterator<T> iterator)
	{
		return new ItEnumeration<>(iterator);
	}

	
	/**
	 * @return an Iterator which returns zero or one values.
	 * @param value the value. If it is not null, the iterator wiull returns
	 * 		this values, else it returns no value. 
	 */
	public static <T> Iterator<T> forValue(T value)
	{
		return forValue(value, value != null);
	}

	
	/**
	 * @return an Iterator which returns zero or one values.
	 * @param value the value
	 * @param hasNext if true, the iterator will return the value, else
	 * 		it will return nothing  
	 */
	public static <T> Iterator<T> forValue(T value, boolean hasNext)
	{
		return new ValueIt<>(value, hasNext);
	}

	
	/**
	 * @return an Iterator which joins the given iterators.
	 */
	@SafeVarargs
	public static <T> Iterator<T> join(Iterator<T>... iterators)
	{
		return new JoinIt<>(iterators);
	}

	
	/**
	 * Returns an Iterator which returns every element just once.
	 */
	public static <T> Iterator<T> unique(Iterator<T> iterator)
	{
		return new UniqueIt<>(iterator);
	}

	
	/**
	 * @return an unmodifiable iterator. 
	 */
	public static <T> Iterator<T> unmodifiable(Iterator<T> it)
	{
		return new UnmodifiableIt<>(it);
	}

	
	/**
	 * Returns an unmodifiable iterator for the collection. 
	 */
	public static <T> Iterator<T> unmodifiable(Collection<T> list)
	{
		return unmodifiable(list.iterator());
	}

	
	private static class ArrayIt<T,S extends T> implements Iterator<T>
	{
		public ArrayIt(S[] elements, int start, int end)
		{
			elements_ 	= elements;
			index_ 		= start;
			end_ 		= end;
		}


		@Override public boolean hasNext()
		{
			return ((elements_ != null) && (index_ < end_));
		}


		@Override public T next() throws NoSuchElementException
		{
			if (!hasNext())
				throw new NoSuchElementException();
			return elements_[index_++];
		}


		@Override public void remove()
		{
			throw new UnsupportedOperationException();
		}


		private final S[] elements_;
		private int index_;
		private final int end_;
	}


	private static class EnumerationIt<T> implements Iterator<T>
	{
		public EnumerationIt(Enumeration<T> enumeration)
		{
			enumeration_ = enumeration;
		}
		
		
		@Override public boolean hasNext()
		{
			return enumeration_.hasMoreElements();
		}
		

		@Override public T next()
		{
			return enumeration_.nextElement();
		}

		
		@Override public void remove()
		{
			throw new UnsupportedOperationException();
		}
		
		
		private final Enumeration<T> enumeration_;
	}


	private static class ItEnumeration<T> implements Enumeration<T>
	{
		public ItEnumeration(Iterator<T> iterator)
		{
			iterator_ = iterator;
		}
		
		
		@Override public boolean hasMoreElements()
		{
			return iterator_.hasNext();
		}
		

		@Override public T nextElement()
		{
			return iterator_.next();
		}

		
		private final Iterator<T> iterator_;
	}


	private static class ValueIt<T> implements Iterator<T>
	{
		public ValueIt(T value, boolean hasNext)
		{
			value_ 		= value;
			hasNext_	= hasNext;
		}
		
		
		@Override public boolean hasNext()
		{
			return hasNext_;
		}
		

		@Override public T next()
		{
			if (!hasNext())
				throw new NoSuchElementException();
			hasNext_ = false;
			return value_;
		}

		
		@Override public void remove()
		{
			throw new UnsupportedOperationException();
		}
		
		
		private boolean hasNext_;
		private final T value_;
	}


	private static class UnmodifiableIt<T> implements Iterator<T>
	{
		public UnmodifiableIt(Iterator<T> it)
		{
			it_	= Check.notNull(it, "iterator");
		}
		
		
		@Override public boolean hasNext()
		{
			return it_.hasNext();
		}
		

		@Override public T next()
		{
			return it_.next();
		}

		
		@Override public void remove()
		{
			throw new UnsupportedOperationException();
		}
		
		
		private final Iterator<T> it_;
	}


	private static class JoinIt<T> implements Iterator<T>
	{
		public JoinIt(Iterator<T>[] iterators)
		{
			iterators_	= Check.notNull(iterators, "iterators");
			initNext();
		}
		
		
		private boolean initNext()
		{
			if (next_ < iterators_.length)
			{
				current_ = iterators_[next_++];
				return true;
			}
			else
			{
				current_ = null;
				return false;
			}
		}
		
		
		@Override public boolean hasNext()
		{
			while(current_ != null)
			{
				if (current_.hasNext())
					return true;
				initNext();
			}
			return false;
		}
		

		@Override public T next()
		{
			while(current_ != null)
			{
				if (current_.hasNext())
					return current_.next();
				initNext();
			}
			throw new NoSuchElementException();
		}

		
		@Override public void remove()
		{
			while(current_ != null)
			{
				if (current_.hasNext())
					current_.remove();
				initNext();
			}
			throw new NoSuchElementException();
		}
		
		
		private Iterator<T>[] iterators_;
		private Iterator<T> current_;
		private int next_;
	}


	private static class UniqueIt<T> implements Iterator<T>
	{
		public UniqueIt(Iterator<T> iterator)
		{
			iterator_ = Check.notNull(iterator, "iterator");
			initNext();
		}
		
		
		private void initNext()
		{
			while (iterator_.hasNext())
			{
				T nextCandidate = iterator_.next();
				if (!seen_.contains(nextCandidate))
				{
					seen_.add(nextCandidate);
					next_ = nextCandidate;
					return;
				}
			}
			iterator_ = null;
		}
		
		
		@Override public boolean hasNext()
		{
			return iterator_ != null;
		}
		

		@Override public T next()
		{
			if (!hasNext())
				throw new NoSuchElementException();
			T next = next_;
			initNext();
			return next;
		}
		

		@Override public void remove()
		{
			throw new UnsupportedOperationException();
		}
		
		
		private T next_;
		private Iterator<T> iterator_;
		private HashSet<T> seen_ = new HashSet<>(); 
	}
}
