package com.munna.utility.reviewer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mohammed Fathauddin
 * @since 2018
 */
public abstract class ReviewerHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReviewerHandler.class);
	
	public Map<String,String> checkDataExist(Map<String,String> dataMap){
		return dataMap;		
	}	
	
	public boolean checkCollegeListTable(Map<String, String> dataMap) {
		return false;
	}
	
}
