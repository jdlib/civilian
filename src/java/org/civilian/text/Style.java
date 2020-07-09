package org.civilian.text;


/**
 * Style represents a formatting variant.
 */
public interface Style
{
	public static <S extends Style> S cast(Class<S> type, Style style)
	{
		return cast(type, style, null);
	}


	@SuppressWarnings("unchecked")
	public static <S extends Style> S cast(Class<S> type, Style style, S defaultStyle)
	{
		return (style != null) && type.isInstance(style) ? (S)style : defaultStyle;
	}
}
