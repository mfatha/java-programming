/*
 * 
 */
package com.munna.utility.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.common.cache.UtilityCache;
import com.munna.common.service.api.UtilityService;
import com.munna.utility.bean.Rating;
import com.munna.utility.bean.Review;
import com.munna.utility.cache.WebSurfConstants;
import com.munna.utility.impl.CSVParserServices;
import com.munna.utility.impl.JsoupServices;
import com.munna.utility.service.utils.StringUtils;

/**
 * The Class generateSSReviewReport.
 * 
 * 
 * @author Mohammed Fathauddin
 * @author Janardhanan V S
 */
public class generateSSReviewReport extends UtilityService {

	private static final Logger LOGGER = LoggerFactory.getLogger(generateSSReviewReport.class);

	String scrapUrl = "";

	Document soupDoc = null;

	String siteTitle = "";

	public generateSSReviewReport(String url) {
		scrapUrl = url;
	}

	@Override
	public void init() {
		soupDoc = (Document) JsoupServices.getInstance().getConnection(scrapUrl);
		if (soupDoc != null) {
			siteTitle = soupDoc.title();
			LOGGER.info("Getting Review Data from :" + siteTitle);
		}
	}

	@Override
	public void process() {
		try {
			String url = "";
			boolean hasNext = true;
			final List<Review> reviewList = new ArrayList<>(50);
			final String college = scrapUrl.split("/")[4];

			while (hasNext) {
				if (soupDoc != null) {
					try {
						parseReviewData(soupDoc, reviewList);
					} catch (Exception e) {
						LOGGER.error("Error while feeding data to cache.. " + e);
					} finally {
						if (!StringUtils.isEmpty(url)) {
							JsoupServices.getInstance().closeConnection(url);
						}
					}
				}else {
					LOGGER.error("Soup Object is empty for: ("+ url +")" );
					UtilityCache.getInstance().add(college, reviewList);
					reviewList.clear();
					synchronized (generateSSReviewReport.class) {
						final Map<String, Object> cacheData = UtilityCache.getInstance().getEntireCache();
						if(cacheData != null && cacheData.size() !=0) {
							LOGGER.info("writing the fetched data into a json file...");
							writeReviewJson();
						}else {
							LOGGER.info("no data to be written in json...");
						}
					}
				}
				if (hasNextReview(soupDoc)) {
					url = soupDoc.getElementsByClass("next").first().select("a[href]").attr("href");
					soupDoc = (Document) JsoupServices.getInstance().getConnection(url);
					siteTitle = soupDoc.title();
				} else {
					hasNext = false;
					UtilityCache.getInstance().add(college, reviewList);
				}
			}
		} catch (Exception e) {
			LOGGER.error("error while getting review data from : " + scrapUrl, e);
		} finally {
			LOGGER.info("Finished Webscraping For : " + siteTitle);
		}
	}

	private List<Review> parseReviewData(Document doc, List<Review> reviewList) {
		final Elements reviewElements = doc.getElementsByAttributeValueStarting("id", "reviewCard_");

		for (Element reviewElm : reviewElements) {
			final Review review = new Review();

			// Reviewer name
			final Element batchElm = reviewElm.getElementsByClass("btch-dtl").first();
			final String name = batchElm.getElementsByClass("name").first().text();
			review.setReviewer(name);

			// Batch
			final String blockText = batchElm.getElementsMatchingText("Batch of ").first().text();
			final String year = blockText.substring(blockText.indexOf("Batch of"), blockText.indexOf("Rating"))
					.replace("Batch of", "").trim();
			review.setBatch(Integer.valueOf(year));

			// Rating
			final Rating rating = parseRating(batchElm);
			review.setRating(rating);

			// Recommended
			final boolean recommends = reviewElm.getElementsByClass("rc-sec").first().text().trim()
					.equals("Recommends this course");
			review.setRecommended(recommends);

			// Course
			final String course = reviewElm.getElementsByAttributeValueMatching("class", "(rvw-h)$").first()
					.getElementsByClass("rvw-titl").first().text().replace("Course reviewed : ", "").trim();
			review.setCourseReviewed(course);

			// Detailed review
			final Elements reviewDetailElms = reviewElm.getElementsByAttributeValueMatching("class", "(rvw-h)$").first()
					.getElementsByAttributeValueStarting("id", "fullDescSection_").select(".rvw-titl");

			for (Element reviewDetail : reviewDetailElms) {
				if (reviewDetail.text().trim().isEmpty()) {
					continue;
				}
				final String[] reviewArray = reviewDetail.text().trim().split(":", 2);
				if (reviewArray.length > 1) {
					final String category = reviewArray[0].trim();
					final String reviewData = reviewArray[1].trim();

					switch (category) {
					case "Placements":
						review.setPlacement(reviewData);
						break;

					case "Infrastructure":
						review.setInfrastructure(reviewData);
						break;

					case "Faculty":
						review.setFaculty(reviewData);
						break;

					case "Other":
						review.setOther(reviewData);
						break;

					default:
						LOGGER.debug("Invalid review category !! : " + category);
						review.setGenericReview(reviewDetail.text().trim());
						continue;
					}

				} else {
					LOGGER.debug("Review category not mentioned. Hence generic review !!");
					review.setGenericReview(reviewDetail.text().trim());
				}
			}

			reviewList.add(review);
		}

		return reviewList;
	}

	private Rating parseRating(Element batchElm) {
		final Element ratingElm = batchElm.getElementsByClass("rating-scr").first();
		final String overallrating = ratingElm.text().substring(0, ratingElm.text().indexOf("/")).trim();

		final Rating rating = new Rating();
		rating.setOverall(Float.valueOf(overallrating));

		final Elements splitRatingElms = ratingElm.getElementsByClass("rating-ol").select("li");

		for (Element splitRating : splitRatingElms) {
			int starCount = splitRating.select("span:not(.sprite-str.star)").size();
			switch (splitRating.text().trim()) {
			case "Worth the Money":
				rating.setWorth(starCount);
				break;

			case "Crowd & Campus Life":
				rating.setCampus(starCount);
				break;

			case "Placements & Internships":
				rating.setPlacements(starCount);
				break;

			case "Infrastructure":
				rating.setInfra(starCount);
				break;

			case "Faculty & Course Curriculum":
				rating.setFaculty(starCount);
				break;

			default:
				LOGGER.debug("Invalid rating !!");
				break;
			}
		}

		return rating;
	}

	private boolean hasNextReview(Document Doc) {
		Element content = (Element) soupDoc.getElementsByClass("next").first();
		if (content != null) {
			String reviewLink = content.select("a[href]").attr("href");
			LOGGER.debug("Next Url link : " + reviewLink);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void finish() {
	}

	/**
	 * TODO
	 * 
	 * Move this to default interface method, if wait needed for all. Also
	 * change reading thread params from config file.
	 * 
	 */
	@Override
	public void sleep() {
		if (WebSurfConstants.THREAD_SLEEP_ENABLED) {
			try {
				LOGGER.info("Thead sleeping for Undeduction. About ["+ WebSurfConstants.THREAD_SLEEP_DELAY +"] milliseconds");
				Thread.sleep(WebSurfConstants.THREAD_SLEEP_DELAY);
			} catch (InterruptedException e) {
				LOGGER.error("Error occured while sleep");
			}
		}
	}
	
	private void writeReviewJson() {
		final String outputDirectory = WebSurfConstants.OUTPUT_FOLDER.concat("CollegesReview_shiksha_2");
		CSVParserServices csvparser = new CSVParserServices();
		csvparser.createDirectory(outputDirectory);
		final String outputFileTemplate = outputDirectory.concat(java.io.File.separator).concat("college_review_");
		final int limit = WebSurfConstants.ShikshaConstants.RECORD_LIMIT;
		final long start = System.currentTimeMillis();
		String outputFileName = "";
		int colleges = 0;
		int count = 0;
		try {
			final Map<String, Object> cacheData = UtilityCache.getInstance().getEntireCache();
			final Map<String, Object> reviewData = new HashMap<>();
			colleges = cacheData.size();
			for (Entry<String, Object> review : cacheData.entrySet()) {
				final List<Review> reviewList = (List<Review>) review.getValue();
				if (count == limit) {
					outputFileName = getUpdatedFileName(outputFileTemplate, System.currentTimeMillis());
					writeJson(outputFileName, reviewData);
					reviewData.clear();
					count = 0;
				} else if (reviewList.size() + count <= limit) {
					reviewData.put(review.getKey(), review.getValue());
					count += reviewList.size();
				} else {
					int index = limit - count;
					reviewData.put(review.getKey(), reviewList.subList(0, index));
					outputFileName = getUpdatedFileName(outputFileTemplate, System.currentTimeMillis());
					writeJson(outputFileName, reviewData);
					reviewData.clear();
					reviewData.put(review.getKey(), reviewList.subList(index, reviewList.size()));
					count = index;
				}
			}
			if (!reviewData.isEmpty()) {
				outputFileName = getUpdatedFileName(outputFileTemplate, System.currentTimeMillis());
				writeJson(outputFileName, reviewData);
			}
		} catch (Exception e) {
			LOGGER.error("error while writing review as json", e);
		} finally {
			UtilityCache.getInstance().clearCache();
			JsoupServices.getInstance().clearConnections();
			long mins = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start);
			LOGGER.info("Total time taken for file writing : " + mins + " minutes for " + colleges + " colleges");
		}
	}
	
	private String getUpdatedFileName(String outputFileTemplate, long batchNum) {
		return outputFileTemplate.concat(batchNum + ".json");
	}
	
	private void writeJson(String fileName, Map<String, Object> reviewData) {
		writeAsFile(fileName, getMinifiedJson(reviewData));
	}

	private void writeAsFile(String filePath, String fileContent) {
		try {
			Files.write(Paths.get(filePath), fileContent.getBytes(), StandardOpenOption.CREATE);
			LOGGER.debug("Finished writing the file : " + filePath);
		} catch (IOException e) {
			LOGGER.error("error while writing to file : " + filePath, e);
		}
	}
	
	private String getMinifiedJson(Map<String, Object> reviewData) {
		return new JSONObject(reviewData).toString();
	}
	

}
