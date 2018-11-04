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

public class CDuniaReviewerHandler extends ReviewerHandler{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CDuniaReviewerHandler.class);

	List<String> columnNames = WebSurfConstants.CollegeDuniyaConstants.COLUMN_NAMES;

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
							LOGGER.info("CDunia review is present for College ( " + collegeListDataSchemaMap.get("COLLEGE_NAME")
									+ " ) and data exist in table...");
						}
					}
				} else {
					LOGGER.error("No CDunia review is present for College ( " + collegeListDataSchemaMap.get("COLLEGE_NAME")
							+ " )  and does not exist in table...");
				}
				collegeListDataSchemaMap.put("COLLEGE_PRESENT_IN_LIST","true");
			} else {
				LOGGER.error("College ( " + dataMap.containsKey("college_name") + " ) does not exist in table...");
			}
		}
		collegeListDataSchemaMap.put("RESULT_JSON", collegeDetails.toString());
		return collegeListDataSchemaMap;
	}
	
	@Override
	public JSONObject getCollegeDetails(Map<String, String> dataMap) {
		DataSchemaManager dataManager = new DataSchemaManager();
		if (dataManager.isTableExist(WebSurfConstants.SQLConstant.COLLEGE_LIST)) {
			if(dataMap.containsKey("college_name") && !Util.isNull(dataMap.get("college_name"))) {
				String collegeName = dataMap.get("college_name").replaceAll("[\\[\\]]", "<>").replaceAll("\\]", ">").replaceAll("<>.*?<>", "").replace("-", "").trim().replaceAll("\\s+","");
				collegeListDataSchemaMap.put("COLLEGE_NAME",(!Util.isEmpty(collegeName))? collegeName : null);
			}			
			//System.out.println("Hi<friends>and<family>".replaceAll("<.*?>", ""));
			collegeListDataSchemaMap.put("REVIEW_IN_SOURCE_ID", "2");
			collegeListDataSchemaMap.put("SITE_URL",(dataMap.containsKey("review_url") && !Util.isNull(dataMap.get("review_url")))? dataMap.get("review_url") : null);
			//Review DataMapping
			collegeListDataSchemaMap.put("REVIEWER_RATING",(dataMap.containsKey("over_all_rating") && !Util.isNull(dataMap.get("over_all_rating")))? dataMap.get("over_all_rating").split("/")[0] : null);
			collegeListDataSchemaMap.put("TOTAL_REVIEWER",(dataMap.containsKey("total_reviewers") && !Util.isNull(dataMap.get("total_reviewers")))? dataMap.get("total_reviewers") : null);
			collegeListDataSchemaMap.put("INFRASTRUCTURE",(dataMap.containsKey("infrastructure") && !Util.isNull(dataMap.get("infrastructure")))? dataMap.get("infrastructure") : null);
			collegeListDataSchemaMap.put("PLACEMENT",(dataMap.containsKey("placements") && !Util.isNull(dataMap.get("placements")))? dataMap.get("placements") : null);
			collegeListDataSchemaMap.put("FACULTY",(dataMap.containsKey("faculty") && !Util.isNull(dataMap.get("faculty")))? dataMap.get("faculty") : null);
			collegeListDataSchemaMap.put("ACCOMMODATION",(dataMap.containsKey("accommodation") && !Util.isNull(dataMap.get("accommodation")))? dataMap.get("accommodation") : null);
			collegeListDataSchemaMap.put("ACADEMIC",(dataMap.containsKey("academic") && !Util.isNull(dataMap.get("academic")))? dataMap.get("academic") : null);
			return super.getCollegeDetails(collegeListDataSchemaMap);
		} else {
			LOGGER.error(WebSurfConstants.SQLConstant.COLLEGE_LIST + "Table does not exist..");
		}
		return null;
	}
	
}
