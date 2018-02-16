package com.munna.utility.cache;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Mohammed Fathauddin
 * @since 2018
 */
public class WebSurfConstants {

	public static final int THREAD_COUNT = 5;

	public static final boolean THREAD_SLEEP_ENABLED = true;

	public static final long THREAD_SLEEP_DELAY = TimeUnit.MINUTES.toMillis(5);

	public static final int RETRY_COUNT = 5;

	public static final String USER_DIRECTORY = System.getProperty("user.dir").concat(java.io.File.separator);

	public static final String CONF_FOLDER = USER_DIRECTORY.concat("conf").concat(java.io.File.separator);

	public static final String OUTPUT_FOLDER = USER_DIRECTORY.concat("output").concat(java.io.File.separator);

	public static final String CONFIGURATION_FILE = CONF_FOLDER.concat("config.properties");

	public static final class SoupUrls {

		public static final String SITE_ONE_LIST_URL_FORMAT = "https://engineering.careers360.com/colleges/list-of-engineering-colleges-in-india?page=";

	}

	public static final class ConfigurationProperty {

		public static final String NUMBER_OF_RECORDS_PER_CSV = "NUMBER_OF_RECORDS_PER_CSV";

	}

	public static final class C360Constants {

		public static final String BASE_URL = "https://engineering.careers360.com";

		public static final List<String> COLUMN_NAMES = Arrays.asList(new String[] { "College Name", "Home Url",
				"Review Url", "All", "Current Student", "Alumni", "Other", "Excellent", "Very Good", "Good", "Average",
				"Poor", "College Infrastructure", "Campus placement", "Industry Exposure", "Faculty", "College Life",
				"Hostel", "Student Crowd", "Affordability", "Postive Recommendations", "Negative Recommendations" });

		public static final int TOTAL_PAGES_TO_CROWL = 210;

	}

	public final static class CollegeDuniyaConstants {

		public static final String BASE_URL = "https://collegedunia.com/india-colleges?ajax=1&college_type=0&page=";

		public static final List<String> COLUMN_NAMES = Arrays
				.asList(new String[] { "college_name", "home_url", "review_url", "total_reviewers", "over_all_rating",
						"infrastructure", "academic", "placements", "faculty", "accommodation", "social" });

		public static final int TOTAL_PAGES_TO_CROWL = 1487;

	}

	public final static class ShikshaConstants {

		public static final String BASE_URL = "https://www.shiksha.com/btech/resources/college-reviews/";

		public static final int TOTAL_PAGES_TO_CROWL = 814;

		public static final int RECORD_LIMIT = 1000;
	}

}
