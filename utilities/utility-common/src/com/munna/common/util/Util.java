package com.munna.common.util;

import java.util.HashMap;
import java.util.Map;

import com.munna.common.cache.UtilityConstants;

/**
 * @author Mohammed Fathauddin
 * @since 2018
 */
public class Util {

	public static boolean isEmpty(String string) {
		if (string == null || string.equals(""))
			return true;
		return false;
	}

	public static boolean isNull(String string) {
		if (string == null || string.equalsIgnoreCase("null"))
			return true;
		return false;
	}

	private static String removeNoise(String value) {
		Map<String,String> noises = UtilityConstants.UtilConstant.NOISES;
		for (Map.Entry<String, String> noise : noises.entrySet()) {
			value = value.replace(noise.getKey(), noise.getValue());//(noise.getKey(), noise.getValue());
		}
		return value;
	}

	public static Map<String, String> removeNoise(Map<String, String> data) {
		Map<String, String> resultSet = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : data.entrySet()) {
			resultSet.put(entry.getKey(), removeNoise(entry.getValue()));
		}
		return resultSet;
	}

}
