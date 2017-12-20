package org.dnteam.histindex.util;

/**
 * A simple Tuple (pair) implementation.
 * @author farrer
 * 
 * @param <F> type of the First element.
 * @param <S> type of the Second element.
 */
public class Tuple <F, S> {
	
	/** First value */
	private F first;
	/** Second value */
	private S second;
	
	/** @return the first */
	public F getFirst() {
		return first;
	}

	/** @param first value of the first */
	public void setFirst(F first) {
		this.first = first;
	}

	/** @return the second */
	public S getSecond() {
		return second;
	}

	/** @param second value of the second */
	public void setSecond(S second) {
		this.second = second;
	}

	/** Constructor with default (null) values */
	public Tuple() {
	}
	
	/** Constructor with defined values.
	 * @param f first value
	 * @param s second value */
	public Tuple(F f, S s) {
		this.first = f;
		this.second = s;
	}
	
}
