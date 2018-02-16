package com.munna.utility.impl;

import java.util.Random;

/**
 * The Class UAProvider.
 * 
 * @author Janardhanan V S
 */
public final class UAProvider {

	private static final Random RANDOM = new Random();

	private static final UAConstants[] UA_VALUES = UAConstants.values();

	public static String randomUA() {
		return UA_VALUES[RANDOM.nextInt(UA_VALUES.length)].getUa();
	}

	private enum UAConstants {

		CHROME_WIN_63("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36"), 
		FIREFOX_WIN_58("Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0"), 
		OPERA_WIN_15("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.52 Safari/537.36 OPR/15.0.1147.100"), 
		FIREFOX_LIN_58("Mozilla/5.0 (X11; Linux x86_64; rv:58.0) Gecko/20100101 Firefox/58.0"), 
		DEFAULT("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21");

		private String ua;

		private UAConstants(String ua) {
			this.ua = ua;
		}

		public String getUa() {
			return ua;
		}

	}

}
