package com.munna.common.db.connection.factory;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mohammed Fathauddin
 * @since 2018
 */

public class DBUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(DBUtil.class);

	public static void releaseResources(AutoCloseable... closable) {
		if (closable != null) {
			try {
				for (AutoCloseable autoCloseable : closable) {
					if (autoCloseable != null) {
						autoCloseable.close();
					}
				}
			} catch (Exception e) {
				LOGGER.error("Error occurred while closing closeable instance", e);
			}
		}
	}

	public static String getTime(long startTime) {
		startTime = System.currentTimeMillis() - startTime;
		long calc = TimeUnit.MILLISECONDS.toMinutes(startTime);
		if (calc == 0) {
			calc = TimeUnit.MILLISECONDS.toSeconds(startTime);
			return calc + " Sec";
		}
		return calc + " Mins";
	}

}
