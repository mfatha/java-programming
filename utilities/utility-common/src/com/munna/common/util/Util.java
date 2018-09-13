package com.munna.common.util;

/**
 * @author Mohammed Fathauddin
 * @since 2018
 */
public class Util {
	
	public static boolean isEmpty(String string){
		if(string == null || string.equals("")) 
			return true;
		return false;
	}
	
	
	public static boolean isNull(String string){
		if(string == null || string.equalsIgnoreCase("null"))
			return true;
		return false;
	}
}
