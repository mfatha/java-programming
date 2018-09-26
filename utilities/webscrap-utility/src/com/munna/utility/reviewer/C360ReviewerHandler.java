package com.munna.utility.reviewer;

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
		Boolean reviewerPresent = false;
		if(collegeDetails != null){
			if (Util.jsonHasElement(collegeDetails, "COLLEGE_NAME")) {
				JSONArray reviewers = new JSONArray();
				if (Util.jsonHasElement(collegeDetails, "REVIEWERS")) {
					reviewers = collegeDetails.getJSONArray("REVIEWERS");
					for (int i = 0; i < reviewers.length(); i++) {
						if (reviewers.getJSONObject(i).getString("REVIEW_IN_SOURCE_ID").equalsIgnoreCase("1")) {
							LOGGER.info("C360 review is present for College ( " + collegeListDataSchemaMap.get("COLLEGE_NAME")
									+ " ) and data exist in table...");
							reviewerPresent = true;
						}
					}
				} else {
					LOGGER.error("No C360 review is present for College ( " + collegeListDataSchemaMap.get("COLLEGE_NAME")
							+ " )  and does not exist in table...");
				}
			} else {
				LOGGER.error("College ( " + dataMap.containsKey("College Name") + " ) does not exist in table...");
			}
		}
		if(reviewerPresent){
			
		}
		return null;
	}

	@Override
	public JSONObject getCollegeDetails(Map<String, String> dataMap) {
		DataSchemaManager dataManager = new DataSchemaManager();
		if (dataManager.isTableExist(WebSurfConstants.SQLConstant.COLLEGE_LIST)) {
			collegeListDataSchemaMap.put("COLLEGE_NAME",
					(dataMap.containsKey("College Name") && !Util.isNull(dataMap.get("College Name")))
							? dataMap.get("College Name") : null);
			collegeListDataSchemaMap.put("REVIEW_IN_SOURCE_ID", "1");
			collegeListDataSchemaMap.put("SITE_URL",
					(dataMap.containsKey("Review Url") && !Util.isNull(dataMap.get("Review Url")))
							? dataMap.get("Review Url") : null);
			return super.getCollegeDetails(collegeListDataSchemaMap);
		} else {
			LOGGER.error(WebSurfConstants.SQLConstant.COLLEGE_LIST + "Table does not exist..");
		}
		return null;
	}
}
