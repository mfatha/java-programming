package com.munna.utility.reviewer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CDuniaReviewerHandler extends ReviewerHandler{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CDuniaReviewerHandler.class);
	
	@Override
	protected Map<String, String> checkDataExist(Map<String, String> dataMap) {
		return super.checkDataExist(dataMap);
	}
	
}
