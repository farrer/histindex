package org.dnteam.histindex.database;

/** A Keyword for indexing Quotes.
 * @author farrer */
public class Keyword extends Entity {
	
	/** Keyword name */
	private String name;

	/** @return {@link #name} */
	public String getName() {
		return name;
	}

	/** Set {@link #name} 
	 * @param name new value for {@link #name}. */
	public void setName(String name) {
		this.name = name;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return name;
	}

}
