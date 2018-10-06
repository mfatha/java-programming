package com.munna.common.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohammed Fathauddin
 * @since 2018
 */
public class UtilityConstants {
	
	public static final class DataBaseConstant {

		public static final String DEFAULT_DB_CONNECTION = "DEFAULT";
		
	}
	
	public static final class UtilConstant {
		public static final Map<String,String> NOISES = new HashMap<String,String>();
		static {
			NOISES.put("[comma]", ",");
			NOISES.put("'", "");
	    }
	}
	

}
