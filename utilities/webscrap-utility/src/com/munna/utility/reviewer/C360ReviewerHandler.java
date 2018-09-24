package com.munna.utility.reviewer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.common.db.connection.factory.DataSchemaManager;
import com.munna.common.util.Util;
import com.munna.utility.cache.WebSurfConstants;

public class C360ReviewerHandler extends ReviewerHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(C360ReviewerHandler.class);
	
	List<String> columnNames = WebSurfConstants.C360Constants.COLUMN_NAMES;
		
	@Override
	public Map<String, String> checkDataExist(Map<String, String> dataMap) {
		checkCollegeListTable(dataMap);
		return super.checkDataExist(dataMap);
	}
	
	@Override
	public boolean checkCollegeListTable(Map<String, String> dataMap) {
		DataSchemaManager dataManager = new DataSchemaManager();
		if(dataManager.isTableExist(WebSurfConstants.SQLConstant.COLLEGE_LIST)) {
			Map<String,String> collegeListDataSchemaMap = new HashMap<String,String>();
			collegeListDataSchemaMap.put("COLLEGE_NAME", (dataMap.containsKey("College Name") && !Util.isNull(dataMap.get("College Name")) )? dataMap.get("College Name"): null);
			collegeListDataSchemaMap.put("REVIEW_IN_SOURCE_ID", "1");
			collegeListDataSchemaMap.put("SITE_URL", (dataMap.containsKey("Review Url") && !Util.isNull(dataMap.get("Review Url")) )? dataMap.get("Review Url"): null);
			//JSONObject resultSet = dataManager.getDataFromCollegeList(dataMap);
		}else {
			LOGGER.error(WebSurfConstants.SQLConstant.COLLEGE_LIST + "Table does not exist..");
		}
		return false;
	}
}
