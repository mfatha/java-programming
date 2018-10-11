package com.munna.utility.reviewer;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.common.db.connection.factory.DataSchemaManager;
import com.munna.common.util.Util;
import com.munna.utility.cache.WebSurfConstants;

public class C360ReviewerHandler extends ReviewerHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(C360ReviewerHandler.class);

	List<String> columnNames = WebSurfConstants.C360Constants.COLUMN_NAMES;

	private Map<String, String> collegeListDataSchemaMap = new HashMap<String, String>();

	@Override
	public Map<String, String> process(Map<String, String> dataMap) {
		JSONObject collegeDetails = getCollegeDetails(dataMap);
		if(collegeDetails != null){
			if (Util.jsonHasElement(collegeDetails, "COLLEGE_NAME")) {
				JSONArray reviewers = new JSONArray();
				if (Util.jsonHasElement(collegeDetails, "REVIEWERS")) {
					reviewers = collegeDetails.getJSONArray("REVIEWERS");
					for (int i = 0; i < reviewers.length(); i++) {
						if (reviewers.getJSONObject(i).getString("REVIEW_IN_SOURCE_ID").equalsIgnoreCase("1")) {
							LOGGER.info("C360 review is present for College ( " + collegeListDataSchemaMap.get("COLLEGE_NAME")
									+ " ) and data exist in table...");
						}
					}
				} else {
					LOGGER.error("No C360 review is present for College ( " + collegeListDataSchemaMap.get("COLLEGE_NAME")
							+ " )  and does not exist in table...");
				}
				collegeListDataSchemaMap.put("COLLEGE_PRESENT_IN_LIST","true");
			} else {
				LOGGER.error("College ( " + dataMap.containsKey("College Name") + " ) does not exist in table...");
			}
		}
		collegeListDataSchemaMap.put("RESULT_JSON", collegeDetails.toString());
		return collegeListDataSchemaMap;
	}

	@Override
	public JSONObject getCollegeDetails(Map<String, String> dataMap) {
		DataSchemaManager dataManager = new DataSchemaManager();
		if (dataManager.isTableExist(WebSurfConstants.SQLConstant.COLLEGE_LIST)) {
			if(dataMap.containsKey("College Name") && !Util.isNull(dataMap.get("College Name"))) {
				String collegeName = dataMap.get("College Name").split(",")[0];
				collegeListDataSchemaMap.put("COLLEGE_NAME",(!Util.isEmpty(collegeName))? collegeName : null);
			}			
			collegeListDataSchemaMap.put("REVIEW_IN_SOURCE_ID", "1");
			collegeListDataSchemaMap.put("SITE_URL",
					(dataMap.containsKey("Review Url") && !Util.isNull(dataMap.get("Review Url")))
							? dataMap.get("Review Url") : null);
			//Review DataMapping
			if(dataMap.containsKey("All") && !Util.isNull(dataMap.get("All"))) {
				collegeListDataSchemaMap.put("TOTAL_REVIEWER",(dataMap.containsKey("All") && !Util.isNull(dataMap.get("All")))? dataMap.get("All") : null);
				String[] ReviewWeight = {"Excellent", "Very Good", "Good", "Average","Poor"};
				double rWeight = 0L;
				for(String weight : ReviewWeight){
					if(dataMap.containsKey(weight) && !Util.isNull(dataMap.get(weight))) {
						rWeight += Long.parseLong(dataMap.get(weight))*WebSurfConstants.C360Constants.REVIEW_WEIGHT.get(weight);
					}
				}
				rWeight = rWeight/Long.parseLong(dataMap.get("All"));
				DecimalFormat f = new DecimalFormat("##.00");
				rWeight = Double.parseDouble(f.format(rWeight));
				collegeListDataSchemaMap.put("REVIEWER_RATING",(!Util.isNull(rWeight))? Double.toString(rWeight) : null);
			}
			collegeListDataSchemaMap.put("INFRASTRUCTURE",(dataMap.containsKey("College Infrastructure") && !Util.isNull(dataMap.get("College Infrastructure")))? dataMap.get("College Infrastructure") : null);
			collegeListDataSchemaMap.put("PLACEMENT",(dataMap.containsKey("Campus placement") && !Util.isNull(dataMap.get("Campus placement")))? dataMap.get("Campus placement") : null);
			collegeListDataSchemaMap.put("INDUSTRY_EXPOSURE",(dataMap.containsKey("Industry Exposure") && !Util.isNull(dataMap.get("Industry Exposure")))? dataMap.get("Industry Exposure") : null);
			collegeListDataSchemaMap.put("FACULTY",(dataMap.containsKey("Faculty") && !Util.isNull(dataMap.get("Faculty")))? dataMap.get("Faculty") : null);
			collegeListDataSchemaMap.put("COLLEGE_LIFE",(dataMap.containsKey("College Life") && !Util.isNull(dataMap.get("College Life")))? dataMap.get("College Life") : null);
			collegeListDataSchemaMap.put("HOSTEL_LIFE",(dataMap.containsKey("Hostel") && !Util.isNull(dataMap.get("Hostel")))? dataMap.get("Hostel") : null);
			collegeListDataSchemaMap.put("SOCIAL",(dataMap.containsKey("Student Crowd") && !Util.isNull(dataMap.get("Student Crowd")))? dataMap.get("Student Crowd") : null);
			collegeListDataSchemaMap.put("RECOMMENDATIONS",(dataMap.containsKey("Postive Recommendations") && !Util.isNull(dataMap.get("Postive Recommendations")))? dataMap.get("Postive Recommendations") : null);
			return super.getCollegeDetails(collegeListDataSchemaMap);
		} else {
			LOGGER.error(WebSurfConstants.SQLConstant.COLLEGE_LIST + "Table does not exist..");
		}
		return null;
	}
}
