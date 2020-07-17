package org.civilian.text;


public class TimeStyle implements Style
{
	public static final TimeStyle HM 	= new TimeStyle(false, ':');
	public static final TimeStyle HMS	= new TimeStyle(true,  ':');


	/**
	 * Returns the given style as TimeStyle or {@link #HM} if not a TimeStyle.
	 */
	public static final TimeStyle cast(Style style)
	{
		return style instanceof TimeStyle ? (TimeStyle)style : HM;
	}


	private TimeStyle(boolean showSeconds, char separator)
	{
		showSeconds_ = showSeconds;
		separator_	 = separator;
	}


	/**
	 * Returns if seconds should be shown.
	 */
	public boolean showSeconds()
	{
		return showSeconds_;
	}


	/**
	 * Returns a new TimeStyle whose showSeconds flag has the given value.
	 */
	public TimeStyle showSeconds(boolean value)
	{
		return value != showSeconds_ ? new TimeStyle(value, separator_) : this;
	}


	public char separator()
	{
		return separator_;
	}


	public TimeStyle separator(char value)
	{
		return value != separator_ ? new TimeStyle(showSeconds_, value) : this;
	}


	@Override public int hashCode()
	{
		return (showSeconds_ ? 1 << 16 : 0) + separator_;
	}


	@Override public boolean equals(Object other)
	{
		if (other instanceof TimeStyle)
		{
			TimeStyle s = (TimeStyle)other;
			return (showSeconds_ == s.showSeconds_) && (separator_ == s.separator_);
		}
		else
			return false;
	}


	@Override public String toString()
	{
		return "TimeStyle[showSeconds=" + showSeconds_ + ", separator_=" + separator_ + ']';
	}


	private final boolean showSeconds_;
	private final char separator_;
}
