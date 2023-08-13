package org.civilian.resource;


import org.civilian.util.UriEncoder;
import org.junit.Assert;


public class PathParamAssert<T>
{
	public static <T> PathParamAssert<T> of(PathParam<T> param)
	{
		return new PathParamAssert<>(param);
	}

	
	public PathParamAssert(PathParam<T> param)
	{
		param_ = param;
	}
	
	
	public PathParamAssert<T> toString(String expected)
	{
		Assert.assertEquals(expected, param_.toString());
		return this;
	}
	
	
	public PathParamAssert<T> toDetailedString(String expected)
	{
		Assert.assertEquals(expected, param_.toDetailedString());
		return this;
	}

	
	public PathParamAssert<T> scan(String path, T value)
	{
		return scan(path, value, null, false);
	}
	
	
	public PathParamAssert<T> scan(String path, T expected, String nextSegment)
	{
		return scan(path, expected, nextSegment, true);
	}
	
	
	private PathParamAssert<T> scan(String path, T expected, String nextSegment, boolean checkNext)
	{
		PathScanner scanner = new PathScanner(path);
		T actual = param_.parse(scanner);
		if ((actual != null) && actual.getClass().isArray())
			Assert.assertArrayEquals((Object[])expected, (Object[])actual);
		else
			Assert.assertEquals(expected, actual);
		if (checkNext)
			Assert.assertEquals(nextSegment, scanner.getSegment());
		return this;
	}

	
	public PathParamAssert<T> build(T value, String expected)
	{
		Assert.assertEquals(expected, param_.buildPath(value, uriEncoder_));
		return this;
	}

	
	private final PathParam<T> param_;
	private final UriEncoder uriEncoder_ = new UriEncoder(); 
}
