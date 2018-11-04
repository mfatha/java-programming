package com.munna.utility.cache;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.munna.common.properties.PropertiesProvider;
import com.munna.common.util.Util;

/**
 * @author Mohammed Fathauddin
 * @since 2018
 */
public class WebSurfConstants {

	public static final int THREAD_COUNT = 10;

	public static final boolean THREAD_SLEEP_ENABLED = true;

	public static final long THREAD_SLEEP_DELAY = TimeUnit.MINUTES.toMillis(1);

	public static final int RETRY_COUNT = 5;

	public static final String USER_DIRECTORY = System.getProperty("user.dir").concat(java.io.File.separator);

	public static final String CONF_FOLDER = USER_DIRECTORY.concat("conf").concat(java.io.File.separator);

	public static final String OUTPUT_FOLDER = USER_DIRECTORY.concat("output").concat(java.io.File.separator);

	public static final String CONFIGURATION_FILE = CONF_FOLDER.concat("config.properties");
	
	public static final String SQL_QUERY_FILE = CONF_FOLDER.concat("sql.properties");

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
		
		public static final Map<String, Integer> REVIEW_WEIGHT = createMap();
		    private static Map<String, Integer> createMap()
		    {
		        Map<String,Integer> REVIEW_WEIGHT = new HashMap<String,Integer>();
		        REVIEW_WEIGHT.put("Excellent", 5);
		        REVIEW_WEIGHT.put("Very Good", 4);
		        REVIEW_WEIGHT.put("Good", 3);
		        REVIEW_WEIGHT.put("Average", 2);
		        REVIEW_WEIGHT.put("Poor", 1);
		        return REVIEW_WEIGHT;
		    }

		public static final int TOTAL_PAGES_TO_CROWL = 210;

	}

	public final static class CollegeDuniyaConstants {

		public static final String BASE_URL = "https://collegedunia.com/india-colleges?ajax=1&college_type=0&page=";

		public static final List<String> COLUMN_NAMES = Arrays
				.asList(new String[] { "college_name", "home_url", "review_url", "total_reviewers", "over_all_rating",
						"infrastructure", "academic", "placements", "faculty", "accommodation", "" });

		public static final int TOTAL_PAGES_TO_CROWL = 1487;

	}

	public final static class ShikshaConstants {

		public static final String BASE_URL = "https://www.shiksha.com/btech/resources/college-reviews/";

		public static final int TOTAL_PAGES_TO_CROWL = 814;

		public static final int RECORD_LIMIT = 250;
	}

	public final static class DataBaseConstant {

		public static final String DEFAULT_DB_CONNECTION = "DEFAULT";
		
		public static final String DB_CONFIGURATION_FILE = CONF_FOLDER.concat("db-config.properties");
		
	}
	
	public final static class SQLConstant {
		
		public static final String COLLEGE_LIST = "COLLEGE_LIST";

		public static final String REVIEW_LIST = "REVIEW_LIST";
		
		public static final String REVIEW_DATA = "REVIEW_DATA";
		
		public static final String  COLLEGE_LIST_TABLE_QUERY = PropertiesProvider.getInstance().getProperties(WebSurfConstants.SQL_QUERY_FILE).getProperty(COLLEGE_LIST);
		
		public static final String  REVIEW_LIST_QUERY = PropertiesProvider.getInstance().getProperties(WebSurfConstants.SQL_QUERY_FILE).getProperty(REVIEW_LIST);
		
		public static final String  REVIEW_DATA_QUERY = PropertiesProvider.getInstance().getProperties(WebSurfConstants.SQL_QUERY_FILE).getProperty(REVIEW_DATA);
		
		public static final String[] REVIEW_DATA_NAMES = {"REVIEWER_RATING","TOTAL_REVIEWER","INFRASTRUCTURE","PLACEMENT","INDUSTRY_EXPOSURE","FACULTY","COLLEGE_LIFE","HOSTEL_LIFE","SOCIAL","RECOMMENDATIONS","ACCOMMODATION","ACADEMIC"};
	}
}
