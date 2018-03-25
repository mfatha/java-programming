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
import com.munna.utility.cache.WebSurfConstants;
import com.munna.utility.impl.JsoupServices;

/**
 * @author Mohammed Fathauddin
 * @since 2018
 */
public class generateC360ReviewReport extends UtilityService {

	private static final Logger LOGGER = LoggerFactory.getLogger(generateC360ReviewReport.class);

	private String BaseUrl = WebSurfConstants.C360Constants.BASE_URL;

	String scrapUrl = "";

	String reviewLink = "";

	Document soupDoc = null;

	String siteTile = "";

	Map<String, String> reviewData = new HashMap<String, String>();

	public generateC360ReviewReport(String url) {
		scrapUrl = url;
	}

	@Override
	public void init() {
		// Getting Review Link from College Details Page [Homme to Review Tab]
		soupDoc = (Document) JsoupServices.getInstance().getConnection(scrapUrl);
		if (soupDoc != null) {
			siteTile = soupDoc.title();
			LOGGER.info("Started Webscraping For : " + siteTile);
			Element content = (Element) soupDoc.getElementsByClass("inner-tab").first();
			String reviewLink = "";
			Elements rlink = content.select("li");
			for (Element r : rlink) {
				if (r.text().equalsIgnoreCase("Reviews")) {
					reviewLink = r.select("a[href]").attr("href");
				}
			}
			if (reviewLink != null && reviewLink != "") {
				soupDoc = (Document) JsoupServices.getInstance().getConnection(BaseUrl + reviewLink);
				this.reviewLink = BaseUrl + reviewLink;
			} else {
				LOGGER.error("Can't Find Review Link for the URL : " + scrapUrl);
				soupDoc = null;
			}
		} else {
			LOGGER.error("cant able to [home] connect to " + scrapUrl);
		}
	}

	@Override
	public void process() {
		// Getting the Review data from the College Review Page
		LOGGER.info("Getting Review Data from :" + siteTile);
		try {
			if (soupDoc != null) {
				reviewData.put("College Name", (soupDoc.getElementsByClass("titleNameCol").first().text() == null ? ""
						: soupDoc.getElementsByClass("titleNameCol").first().text()));
				reviewData.put("Home Url", scrapUrl);
				reviewData.put("Review Url", reviewLink);
				Elements review_block = soupDoc.getElementsByClass("review_blk");
				if (review_block != null && !review_block.isEmpty()) {
					for (Element rBlock : review_block) {
						if (rBlock.getElementsByTag("h4").text().equalsIgnoreCase("College Recommendations")) {
							reviewData.put("Postive Recommendations",
									rBlock.getElementsByClass("thumb-up").first().text());
							reviewData.put("Negative Recommendations",
									rBlock.getElementsByClass("thumb-down").first().text());
						} else {
							Elements inner_rBlock = rBlock.getElementsByClass("meter_progress");
							if (inner_rBlock == null || inner_rBlock.isEmpty()) {
								inner_rBlock = rBlock.getElementsByClass("star_blk");
							}
							if (!inner_rBlock.isEmpty()) {
								for (Element innerRate : inner_rBlock) {
									String[] content = innerRate.text().split(" ");
									String value = content[content.length - 1];
									reviewData.put(innerRate.text().replace(value, "").trim(), value);
								}
							}
						}
					}
				}
			} else {
				LOGGER.error("cant able to [review] connect to" + scrapUrl);
			}
		} catch (Exception e) {
			LOGGER.error("error while getting review data from : " + reviewLink + "\t " + e);
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
			JsoupServices.getInstance().closeConnection(reviewLink);
			JsoupServices.getInstance().closeConnection(scrapUrl);
		}
	}

}
