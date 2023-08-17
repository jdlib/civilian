package org.civilian.resource.pathparam;


import org.civilian.type.Type;
import org.civilian.util.PathScanner;
import org.civilian.util.UriEncoder;


public class ConvertingPathParam<T> extends TypeBasedPathParam<T>
{
	public ConvertingPathParam(String name, PathParam<String> inner, Type<T> type)
	{
		super(buildName(name, inner), type);
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
	
	
	private final PathParam<String> inner_;
}
