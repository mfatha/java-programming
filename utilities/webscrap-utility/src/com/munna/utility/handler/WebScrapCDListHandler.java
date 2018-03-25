package com.munna.utility.handler;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.common.properties.PropertiesProvider;
import com.munna.utility.cache.WebSurfConstants;
import com.munna.utility.impl.JsoupServices;

public class WebScrapCDListHandler extends WebScrapHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebScrapCDListHandler.class);

	private String baseURL = WebSurfConstants.CollegeDuniyaConstants.BASE_URL;

	@Override
	public void startProcess() {
		LOGGER.info("Started Web Scraping Process for CollegeDunia..");
		int numberOfRecordsPerCsv = Integer
				.parseInt(PropertiesProvider.getInstance().getProperties(WebSurfConstants.CONFIGURATION_FILE)
						.getProperty(WebSurfConstants.ConfigurationProperty.NUMBER_OF_RECORDS_PER_CSV));
		int numberOfPagesToCrowl = WebSurfConstants.CollegeDuniyaConstants.TOTAL_PAGES_TO_CROWL;
		ArrayList<String> collegeLinks = new ArrayList<String>();
		boolean hasNext = true;
		int pageNumber = 0;
		Document soupDoc = null;
		while (hasNext) {
			try {
				soupDoc = (Document) JsoupServices.getInstance().getConnection(baseURL + pageNumber);
				if (soupDoc != null) {
					Elements content = (Elements) soupDoc.getElementsByClass("listing-block-cont");
					for (Element c : content) {
						Elements sub_sontent = c.getElementsByClass("automate_clientimg_snippet");
						for (Element sc : sub_sontent) {
							Element review_block = sc.children().first().getElementsByClass("bottom-block").first()
									.getElementsByClass("clg-fee-review").first();
							Element reviewLink = review_block.children().last().select("a[href]").first();
							// System.out.println(reviewLink.attr("href"));
							String link = reviewLink.attr("href");
							String[] s = link.split("/");
							if (s[s.length - 1].equalsIgnoreCase("reviews")) {
								collegeLinks.add(link);
							}
						}
					}
					if (collegeLinks.size() >= numberOfRecordsPerCsv) {
						buildCSVFile(collegeLinks, 2);
						collegeLinks = new ArrayList<String>();
					}
					if (pageNumber >= numberOfPagesToCrowl) {
						hasNext = false;
					}
					pageNumber++;
				} else {
					JsoupServices.getInstance().closeConnection(baseURL + pageNumber);
					LOGGER.error("Retrying Connection for : " + baseURL + pageNumber);
				}
			} catch (Exception e) {
				LOGGER.error("error in getting college list.... " + e);
				hasNext = false;
			}
		}
		if (collegeLinks != null && collegeLinks.size() != 0) {
			buildCSVFile(collegeLinks, 2);
			collegeLinks = new ArrayList<String>();
		}
		JsoupServices.getInstance().clearConnections();
	}

	@Override
	public void generateCsvReport() {
		LOGGER.info("Report has been generated simulaneously in this...");
	}

}
