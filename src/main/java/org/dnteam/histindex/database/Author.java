package org.dnteam.histindex.database;

public class Author extends Entity {

	/** Author's name */
	private String name;
	/** Author's surname */
	private String lastName;
	/** lastName, name */
	private String fullName;
	
	/** @return {@link #name} */
	public String getName() {
		return name;
	}
	
	/** Set author's name
	 * @param name new name */
	public void setName(String name) {
		this.name = name;
		updateFullName();
	}
	
	/** @return {@link #lastName} */
	public String getLastName() {
		return lastName;
	}
	
	/** Set author's surname.
	 * @param lastName new last name. */
	public void setLastName(String lastName) {
		this.lastName = lastName;
		updateFullName();
	}
	
	/** Update current fullName. */
	protected void updateFullName() {
		this.fullName = this.lastName + ", " + this.name;
	}
	
	/** @return {@link #fullName}. */
	public String getFullName() {
		return this.fullName;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getFullName();
	}
	
}
