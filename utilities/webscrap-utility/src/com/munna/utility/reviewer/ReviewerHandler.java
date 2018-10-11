package com.munna.utility.reviewer;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.common.db.connection.factory.DataSchemaManager;
import com.munna.common.util.Util;

/**
 * @author Mohammed Fathauddin
 * @since 2018
 */
public abstract class ReviewerHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReviewerHandler.class);
	
	public Map<String,String> process(Map<String,String> dataMap){
		return dataMap;		
	}	
		
	public JSONObject getCollegeDetails(Map<String, String> dataMap){
		DataSchemaManager dataManager = new DataSchemaManager();
		JSONObject jsonResult = null;
		String query =  "SELECT CL.ID, CL.COLLEGE_NAME , RL.ID AS REVIEW_ID, RL.REVIEW_IN_SOURCE_ID, RL.SITE_URL FROM college_list AS CL , review_list AS RL WHERE RL.COLLEGE_ID = CL.ID" + " AND CL.COLLEGE_NAME LIKE ('"+dataMap.get("COLLEGE_NAME")+"')";
		try {
			ResultSet resultSet = dataManager.executeCommand(query);
			jsonResult = new JSONObject();
			JSONArray jsonReviewers = new JSONArray();
			while(resultSet.next()) {
				JSONObject reviewer = null;
				if(Util.jsonHasElement(jsonResult, "COLLEGE_ID") && jsonResult.getString("COLLEGE_ID").equalsIgnoreCase(resultSet.getString("ID"))) {
					if(Util.jsonHasElement(jsonResult, "REVIEWERS")) {
						jsonReviewers = jsonResult.getJSONArray("REVIEWERS");
						for(int i =0 ; i< jsonReviewers.length() ;i++) {
							JSONObject json = jsonReviewers.getJSONObject(i);
							if(Util.jsonHasElement(json, "REVIEW_IN_SOURCE_ID") && !json.getString("REVIEW_IN_SOURCE_ID").equalsIgnoreCase(resultSet.getString("REVIEW_IN_SOURCE_ID"))) {
								reviewer = new JSONObject();
								reviewer.put("REVIEW_IN_SOURCE_ID", resultSet.getString("REVIEW_IN_SOURCE_ID"));
								reviewer.put("SITE_URL", resultSet.getString("SITE_URL"));
								reviewer.put("REVIEWS",getReviews(resultSet.getString("REVIEW_ID")));
							}
						}
					}else {
						reviewer = new JSONObject();
						reviewer.put("REVIEW_IN_SOURCE_ID", resultSet.getString("REVIEW_IN_SOURCE_ID"));
						reviewer.put("SITE_URL", resultSet.getString("SITE_URL"));
					}
				}else {
					jsonResult.put("COLLEGE_ID", resultSet.getString("ID"));
					jsonResult.put("COLLEGE_NAME", resultSet.getString("COLLEGE_NAME"));
					reviewer = new JSONObject();
					reviewer.put("REVIEW_IN_SOURCE_ID", resultSet.getString("REVIEW_IN_SOURCE_ID"));
					reviewer.put("SITE_URL", resultSet.getString("SITE_URL"));		
					reviewer.put("REVIEWS",getReviews(resultSet.getString("REVIEW_ID")));
				}
				if(reviewer != null)
					jsonReviewers.put(reviewer);				
				jsonResult.put("REVIEWERS", jsonReviewers);
			}
		} catch (Exception e) {
			LOGGER.error("Error in execting query : " + e);
		}
		return jsonResult;
	}

	protected JSONObject getReviews(String reviewSourceId) {
		DataSchemaManager dataManager = new DataSchemaManager();
		JSONObject jsonResult = null;
		String query =  "SELECT REVIEW.ID, REVIEW.REVIEW_ID , REVIEW.REVIEW_NAME, REVIEW.VALUE FROM review_data AS REVIEW WHERE REVIEW.REVIEW_ID = "+reviewSourceId;
		try {
			ResultSet resultSet = dataManager.executeCommand(query);
			jsonResult = new JSONObject();
			while(resultSet.next()) {				
				if(resultSet.getString("VALUE") != null)
					jsonResult.put(resultSet.getString("REVIEW_NAME"), resultSet.getString("VALUE"));				
			}
		} catch (Exception e) {
			LOGGER.error("Error in execting query : " + e);
		}
		return jsonResult;
	}
	
}
