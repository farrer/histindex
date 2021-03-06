package org.dnteam.histindex.database;

/** Source (ie: historical document) implementation.
 * @author farrer */
public class Source extends Entity {
	
	/** Source title */
	private String title;
	/** Source's year of production */
	private int year;
	/** Source text with some extra description information */
	private String extraInfo;
	
	/** @return {@link #title} */
	public String getTitle() {
		return title;
	}
	
	/** Set new {@link #title}.
	 * @param title new {@link #title}. */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/** @return {@link #year} */
	public int getYear()
	{
		return year;
	}

	/** Set new {@link #year}.
	 * @param title new {@link #year}. */
	public void setYear(int year) {
		this.year = year;
	}

	/** @return {@link #extraInfo}. */
	public String getExtraInfo() {
		return extraInfo;
	}
	
	/** Set new {@link #extraInfo}.
	 * @param extraInfo new {@link #extraInfo}. */
	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getTitle();
	}

}
