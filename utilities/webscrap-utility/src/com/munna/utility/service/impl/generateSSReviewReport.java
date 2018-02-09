package com.munna.utility.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;

import com.munna.common.cache.UtilityCache;
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
		try {
			boolean hasNext = true;
			int pageNumber = 1;
			String nextUrl = "";
			while(hasNext) {
				if (soupDoc != null) {
					reviewData = getUserReview(soupDoc);
					try {
						if (siteTile != null && !siteTile.equalsIgnoreCase("")) {
							UtilityCache.getInstance().add(siteTile+pageNumber, reviewData);
						}
					} catch (Exception e) {
						log.error("Error while feefing data to cache.. " + e);
					}finally {
						if(nextUrl != null && nextUrl !="") {
							JsoupServices.getInstance().closeConnection(nextUrl);
						}
					}
				}
				//reviews-9?sort_by=year+of+gradution
				nextUrl = scrapUrl+"-"+pageNumber+"?sort_by=year+of+gradution";
				soupDoc = (Document) JsoupServices.getInstance().getConnection(nextUrl);
				if(!hasReviewData(soupDoc)) {
					hasNext=false;
				}else {
					pageNumber++;
				}
			}
		} catch (Exception e) {
			log.error("error while getting review data from : " + scrapUrl + "\t " + e);
		} finally {
			log.info("Finished Webscraping For : " + siteTile);
		}
	}

	private boolean hasReviewData(Document nextSoupDoc) {
		
		return false;
	}

	private Map<String, String> getUserReview(Document docs) {
		
		return null;
	}

	@Override
	public void finish() {
		
	}

}
