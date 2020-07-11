package org.civilian.internal.pathparam;


import org.civilian.resource.PathParam;
import org.civilian.resource.PathScanner;
import org.civilian.response.UriEncoder;
import org.civilian.type.Type;
import org.civilian.util.Check;


public class ConvertingPathParam<T> extends TypeBasedPathParam<T>
{
	public ConvertingPathParam(PathParam<String> inner, Type<T> type)
	{
		super(Check.notNull(inner, "inner").getName(), type);
		inner_ = inner;
	}
	

	@Override public T parse(PathScanner scanner)
	{
		return parse(inner_.parse(scanner));
	}
	

	@Override public void buildPath(T value, UriEncoder encoder, StringBuilder path)
	{
		inner_.buildPath(format(value), encoder, path);
	}
	

	@Override protected String getPatternString()
	{
		return getPatternString(inner_);
	}
	
	
	private PathParam<String> inner_;
}
