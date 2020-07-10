package org.civilian.internal.pathparam;


import org.civilian.resource.PathParam;
import org.civilian.resource.PathScanner;
import org.civilian.resource.PathScanner.Mark;
import org.civilian.response.UriEncoder;
import org.civilian.util.Check;


/**
 * PrefixedPathParam is a proxy PathParam which
 * recognizes a constant segment followed by segments recognized
 * by another PathParam.
 */
public class PrefixedPathParam<T> extends PathParam<T>
{
	public PrefixedPathParam(String segment, PathParam<T> inner)
	{
		super(Check.notNull(inner, "inner").getName());
		inner_      = inner;
		segment_ 	= Check.notNull(segment, "segment");
	}
	
	
	@Override public Class<T> getType()
	{
		return inner_.getType();
	}
	
	
	@Override public T parse(PathScanner scanner)
	{
		T value	= null;
		Mark mark	= scanner.mark();
		if (scanner.matchSegment(segment_))
		{
			scanner.next();
			value = inner_.parse(scanner);
			if (value == null)
				mark.revert();
		}
		return value;
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
