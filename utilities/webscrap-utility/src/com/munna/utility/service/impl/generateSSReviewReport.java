package com.munna.utility.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;

import com.munna.common.service.api.UtilityService;
import com.munna.utility.impl.JsoupServices;

public class generateSSReviewReport extends UtilityService{

	private Log log = LogFactory.getLog(this.getClass());

	String scrapUrl = "";

	Document soupDoc = null;

	String siteTile = "";

	Map<String, String> reviewData = new HashMap<String, String>();
	
	public generateSSReviewReport(String url) {
		scrapUrl = url;
	}

	@Override
	public void init() {
		soupDoc = (Document) JsoupServices.getInstance().getConnection(scrapUrl);
		if(soupDoc != null) {
			siteTile = soupDoc.title();
			log.info("Getting Review Data from :" + siteTile);
		}
	}

	@Override
	public void process() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}

}
