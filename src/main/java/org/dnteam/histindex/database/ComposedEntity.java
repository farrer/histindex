package org.dnteam.histindex.database;

import org.dnteam.histindex.util.Tuple;

/** A composed entity implementation, wich is made of two {@link Entity}ies.
 * @author farrer
 *
 * @param <A> type of its first {@link Entity}.
 * @param <B> type of its second {@link Entity}. */
public abstract class ComposedEntity<A extends Entity, B extends Entity> {

	/** Key, composed by its two Entities keys */
	private Tuple<Long, Long> key = new Tuple<Long, Long>();
	/** Its first related Entity */
	private A first;
	/** Its second related Entity */
	private B second;
	
	/** @return {@link #key}. */
	public Tuple<Long, Long> getKey()	{
	   return key;
	}
	
	/** Set {@link #key}.
	 * @param key new key value. */
	public void setKey(Tuple<Long, Long> key) {
		this.key = key;
	}

	/** @return {@link #first}. */
	public A getFirstEntity() {
		return first;
	}

	/** Set the first entity of the composed.<br/>
	 * <b>Note:</b> It will reset the {@link #key}.<br/>
	 * @param first new first entity. */
	public void setFirstEntity(A first) {
		this.first = first;
		if(first != null) {
			key.setFirst(first.getId());
		} else {
			key.setFirst(null);
		}
	}

	/** @return {@link #second}. */
	public B getSecondEntity() {
		return second;
	}

	/** Set the second entity of the composed.<br/>
	 * <b>Note:</b> It will reset the {@link #key}.<br/>
	 * @param second new second entity. */
	public void setSecondEntity(B second) {
		this.second = second;
		if(second != null) {
			key.setSecond(second.getId());
		} else {
			key.setSecond(null);
		}
	}
	
	/** @return EntityManager for the first Entity */
	public abstract EntityManager<A> getFirstEntityManager();
	/** @return EntityManager for the second Entity */
	public abstract EntityManager<B> getSecondEntityManager();
	
}
