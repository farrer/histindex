package org.dnteam.histindex.database;

/**  A Database long-identifier Entity abstraction.
 * @author farrer  */
public abstract class Entity {

	/** The entity identifier */
	private long id;
	
	/** @return {@link #if} */
	public long getId() { 
		return id;
	};
	
	/** Set {@link #id}.
	 * @param id new identifier. */
	public void setId(long id) {
		this.id = id;
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if(obj != null) {
			if(obj.getClass().equals(getClass())) {
				Entity e2 = (Entity) obj;
				return id == e2.getId();
			}
		}
		return false;
	}
	
	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return (int) id;
	}
	
}
