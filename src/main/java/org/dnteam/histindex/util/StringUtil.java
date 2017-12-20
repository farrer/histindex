package org.dnteam.histindex.util;

public class StringUtil {

	/** Check if two Strings are equal, accepting <code>null</code> values.
	 * @param s1 first string to check.
	 * @param s2 second string to check.
	 * @return if are equal or not. */
	public static boolean isEqual(String s1, String s2) {
		if((s1 == null) && (s2 == null)) {
			return true;
		}
		else if(s1 != null) {
			return s1.equals(s2);
		}
		
		return false;
	}
	
	/** Check if the {@link String} is empty (or <code>null</code>).
	 * @param s {@link String} to check.
	 * @return <code>true</code> if it's empty or <code>null</code>. <code>false</code> otherwise. */
	public static boolean isEmpty(String s) {
		if(s == null) {
			return true;
		}
		
		return s.isEmpty();
	}

}
