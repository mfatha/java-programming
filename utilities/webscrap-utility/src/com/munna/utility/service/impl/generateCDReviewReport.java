package com.munna.utility.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.common.cache.UtilityCache;
import com.munna.common.service.api.UtilityService;
import com.munna.utility.impl.JsoupServices;

public class generateCDReviewReport extends UtilityService {

	private static final Logger LOGGER = LoggerFactory.getLogger(generateCDReviewReport.class);

	String scrapUrl = "";

	Document soupDoc = null;

	String siteTile = "";

	Map<String, String> reviewData = new HashMap<String, String>();

	public generateCDReviewReport(String url) {
		scrapUrl = url;
	}

	@Override
	public void init() {
		soupDoc = (Document) JsoupServices.getInstance().getConnection(scrapUrl);
		if (soupDoc != null) {
			siteTile = soupDoc.title();
			LOGGER.info("Getting Review Data from :" + siteTile);
		}
	}

	@Override
	public void process() {
		try {
			if (soupDoc != null) {
				reviewData.put("college_name", (soupDoc.getElementById("page_h1").text() == null ? ""
						: soupDoc.getElementById("page_h1").text().replace("- Reviews", "").trim()));
				reviewData.put("home_url", scrapUrl.replace("/reviews", ""));
				reviewData.put("review_url", scrapUrl);
				Element reviewBlock = soupDoc.getElementById("renderTabData");
				Element inner_review = reviewBlock.getElementsByClass("container-fluid").first()
						.getElementsByClass("pad-wrap").first().getElementsByClass("content_box").first();
				Element headerBlock = inner_review.getElementsByClass("content_head").first();
				// Header parsing.
				String totalReviewers = headerBlock.getElementsByClass("head_desc").first().getElementsByTag("span")
						.first().text().replace("students", "").trim();
				reviewData.put("total_reviewers", totalReviewers);
				String overAllRating = headerBlock.getElementsByClass("head_desc").first()
						.getElementsByClass("major_data").first().text();
				reviewData.put("over_all_rating", overAllRating);
				// Body parsing.
				Element bodyBlock = inner_review.getElementsByClass("content_body").first();
				Elements reviewDataBlocks = bodyBlock.getElementsByClass("rating-block");
				if (reviewDataBlocks != null) {
					for (Element dataBlock : reviewDataBlocks) {
						reviewData.put(dataBlock.getElementsByClass("rating_name").first().text(),
								dataBlock.getElementsByClass("rating_value").first().text());
					}
				}
			} else {
				LOGGER.error("cant able to [review] connect to " + scrapUrl);
			}
		} catch (Exception e) {
			LOGGER.error("error while getting review data from : " + scrapUrl + "\t " + e);
		} finally {
			LOGGER.info("Finished Webscraping For : " + siteTile);
		}
	}

	@Override
	public void finish() {
		try {
			if (siteTile != null && !siteTile.equalsIgnoreCase("")) {
				UtilityCache.getInstance().add(siteTile, reviewData);
			}
		} catch (Exception e) {
			LOGGER.error("Error while feefing data to cache.. " + e);
		} finally {
			JsoupServices.getInstance().closeConnection(scrapUrl);
		}
	}

}
