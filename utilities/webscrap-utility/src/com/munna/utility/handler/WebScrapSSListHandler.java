package com.munna.utility.handler;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.munna.common.properties.PropertiesProvider;
import com.munna.utility.cache.WebSurfConstants;
import com.munna.utility.impl.JsoupServices;

public class WebScrapSSListHandler extends WebScrapHandler{

	private Log log = LogFactory.getLog(this.getClass());
	
	private String baseURL = WebSurfConstants.ShikshaConstants.BASE_URL;

	@Override
	public void startProcess() {
		log.info("Started Web Scraping Process for Shikasha..");
		int numberOfRecordsPerCsv = Integer
				.parseInt(PropertiesProvider.getInstance().getProperties(WebSurfConstants.CONFIGURATION_FILE)
						.getProperty(WebSurfConstants.ConfigurationProperty.NUMBER_OF_RECORDS_PER_CSV));
		int numberOfPagesToCrowl = WebSurfConstants.ShikshaConstants.TOTAL_PAGES_TO_CROWL;
		ArrayList<String> collegeLinks = new ArrayList<String>();
		boolean hasNext = true;
		int pageNumber = 0;	
		Document soupDoc = null;
		while (hasNext) {
			try {
				soupDoc = (Document) JsoupServices.getInstance().getConnection(baseURL + pageNumber);
				if(soupDoc != null) {
					Elements content = (Elements) soupDoc.getElementsByClass("rv_title").select("a[href]");
					for(Element c : content) {
						//System.out.println(reviewLink.attr("href"));
						String link = c.attr("href");
						String[] s = link.split("/");
						if(s[s.length-1].equalsIgnoreCase("reviews")) {
							collegeLinks.add(link);	
						}							
					}
					if (collegeLinks.size() >= numberOfRecordsPerCsv) {
						buildCSVFile(collegeLinks, 3);
						collegeLinks = new ArrayList<String>();
					}
					if (pageNumber >= numberOfPagesToCrowl) {
						hasNext = false;
					}
					pageNumber++;
				}else {
					JsoupServices.getInstance().closeConnection(baseURL + pageNumber);
					log.error("Retrying Connection for : "+ baseURL + pageNumber);
				}
			} catch (Exception e) {
				log.error("error in getting college list.... " + e);
				hasNext = false;
			}
		}
		if (collegeLinks != null && collegeLinks.size() != 0) {
			buildCSVFile(collegeLinks, 3);
			collegeLinks = new ArrayList<String>();
		}
		JsoupServices.getInstance().clearConnections();
	}

	@Override
	public void generateCsvReport() {
		log.info("Report has been generated simulaneously in this...");
	}
}
