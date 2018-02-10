package com.munna.utility.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
			String nextUrl = "";
			while(hasNext) {
				if (soupDoc != null) {
					reviewData = getUserReview(soupDoc);
					try {
						if (siteTile != null && !siteTile.equalsIgnoreCase("")) {
							UtilityCache.getInstance().add(siteTile, reviewData);
						}
					} catch (Exception e) {
						log.error("Error while feefing data to cache.. " + e);
					}finally {
						if(nextUrl != null && nextUrl !="") {
							JsoupServices.getInstance().closeConnection(nextUrl);
						}
					}
				}
				if(hasNextReview(soupDoc)) {
					nextUrl = soupDoc.getElementsByClass("next").first().select("a[href]").attr("href");
					soupDoc = (Document) JsoupServices.getInstance().getConnection(nextUrl);
					siteTile = soupDoc.title();
				}else {
					hasNext=false;
				}
			}
		} catch (Exception e) {
			log.error("error while getting review data from : " + scrapUrl + "\t " + e);
		} finally {
			log.info("Finished Webscraping For : " + siteTile);
		}
	}

	private Map<String, String> getUserReview(Document Doc) {
		
		return null;
	}
	
	private boolean hasNextReview(Document Doc) {
		Element content = (Element) soupDoc.getElementsByClass("next").first();
		if(content != null) {
			String reviewLink = content.select("a[href]").attr("href");
			log.debug("Next Url link : " + reviewLink);
			return true;
		}else {
			return false;
		}		
	}


	@Override
	public void finish() {
		
	}

}
