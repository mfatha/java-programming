package com.munna.instagram.constants;

import java.util.concurrent.TimeUnit;


/**
 * @author Mohammed Fathauddin
 * @since 2018
 */
public class InstaConstants {
	
	public static final int THREAD_COUNT = 10;

	public static final boolean THREAD_SLEEP_ENABLED = true;

	public static final long THREAD_SLEEP_DELAY = TimeUnit.MINUTES.toMillis(2);

	public static final int RETRY_COUNT = 5;	

	public static final String USER_DIRECTORY = System.getProperty("user.dir").concat(java.io.File.separator);

	public static final String CONF_FOLDER = USER_DIRECTORY.concat("conf").concat(java.io.File.separator);

	public static final String OUTPUT_FOLDER = USER_DIRECTORY.concat("output").concat(java.io.File.separator);

	public static final String CONFIGURATION_FILE = CONF_FOLDER.concat("config.properties");
	
	public static final String SQL_QUERY_FILE = CONF_FOLDER.concat("sql.properties");

	public static final String DEFAULT_IG_CONNECTION = "DEFAULT";

}
