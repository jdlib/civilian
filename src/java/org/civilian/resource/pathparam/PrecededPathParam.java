package org.civilian.resource.pathparam;


import org.civilian.util.Check;
import org.civilian.util.PathScanner;
import org.civilian.util.http.UriEncoder;


/**
 * PrecededPathParam is a proxy PathParam which
 * recognizes a constant segment followed by segments recognized
 * by another PathParam.
 */
public class PrecededPathParam<T> extends PathParam<T>
{
	public PrecededPathParam(String name, String segment, PathParam<T> inner)
	{
		super(buildName(name, inner));
		inner_      = inner;
		segment_ 	= Check.notNull(segment, "segment");
	}
	
	
	@Override public Class<T> getType()
	{
		return inner_.getType();
	}
	
	
	@Override public T parse(PathScanner scanner)
	{
		return scanner.consumeSegment(segment_) ? inner_.parse(scanner) : null; 
	}
	
	
	@Override public void buildPath(T value, UriEncoder encoder, StringBuilder path)
	{
		buildPathSegment(segment_, encoder, path);
		inner_.buildPath(value, encoder, path);
	}
	
	
	@Override protected String getPatternString()
	{
		return '/' + segment_ + getPatternString(inner_);
	}
	
	
	private final PathParam<T> inner_;
	private final String segment_;
}
