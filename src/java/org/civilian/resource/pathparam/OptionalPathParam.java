package org.civilian.resource.pathparam;


import java.util.Optional;
import org.civilian.resource.PathParam;
import org.civilian.util.PathScanner;
import org.civilian.util.UriEncoder;


/**
 * OptionalPathParam is a PathParam which returns an Optional based on the value of another PathParam.
 * If that PathParam matches then an Optional with the matched value is parsed.
 * If that PathParam does not match then an empty Optional is parsed.
 */
public class OptionalPathParam<T> extends PathParam<Optional<T>>
{
	public OptionalPathParam(String name, PathParam<T> inner)
	{
		super(buildName(name, inner));
		inner_ = inner;
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override public Class<Optional<T>> getType()
	{
		return (Class)Optional.class;
	}
	
	
	@Override public Optional<T> parse(PathScanner scanner)
	{
		return Optional.ofNullable(inner_.parse(scanner)); 
	}
	
	
	@Override public void buildPath(Optional<T> value, UriEncoder encoder, StringBuilder path)
	{
		if (value.isPresent())
			inner_.buildPath(value.get(), encoder, path);
	}
	

	@Override protected String getPatternString()
	{
		return getPatternString(inner_);
	}
	
	
	private final PathParam<T> inner_;
}
