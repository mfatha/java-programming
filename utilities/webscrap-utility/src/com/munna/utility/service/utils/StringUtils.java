package com.munna.utility.service.utils;

/**
 * The Class StringUtils.
 * 
 * @author Janardhanan V S
 */
public final class StringUtils {

	/**
	 * Private constructor to avoid object instantiation.
	 */
	private StringUtils() {

	}

	public static boolean isEmpty(String s) {
		return (s == null || s.isEmpty());
	}
}
