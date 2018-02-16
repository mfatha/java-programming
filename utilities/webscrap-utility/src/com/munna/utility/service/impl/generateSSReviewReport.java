/*
 * 
 */
package com.munna.utility.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.munna.common.cache.UtilityCache;
import com.munna.common.service.api.UtilityService;
import com.munna.utility.bean.Rating;
import com.munna.utility.bean.Review;
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

	private Log log = LogFactory.getLog(this.getClass());

	String scrapUrl = "";

	Document soupDoc = null;

	String siteTitle = "";

	Map<String, List<Review>> reviewData = new HashMap<>();

	public generateSSReviewReport(String url) {
		scrapUrl = url;
	}

	@Override
	public void init() {
		soupDoc = (Document) JsoupServices.getInstance().getConnection(scrapUrl);
		if (soupDoc != null) {
			siteTitle = soupDoc.title();
			log.info("Getting Review Data from :" + siteTitle);
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
						log.error("Error while feeding data to cache.. " + e);
					} finally {
						if (!StringUtils.isEmpty(url)) {
							JsoupServices.getInstance().closeConnection(url);
						}
					}
				}
				if (hasNextReview(soupDoc)) {
					url = soupDoc.getElementsByClass("next").first().select("a[href]").attr("href");
					soupDoc = (Document) JsoupServices.getInstance().getConnection(url);
					siteTitle = soupDoc.title();
				} else {
					hasNext = false;
					reviewData.put(college, reviewList);
					UtilityCache.getInstance().add(college, reviewList);
				}
			}
		} catch (Exception e) {
			log.error("error while getting review data from : " + scrapUrl + "\t " + e);
		} finally {
			log.info("Finished Webscraping For : " + siteTitle);
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
						log.debug("Invalid review category !! : " + category);
						review.setGenericReview(reviewDetail.text().trim());
						continue;
					}

				} else {
					log.debug("Review category not mentioned. Hence generic review !!");
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
				log.debug("Invalid rating !!");
				break;
			}
		}

		return rating;
	}

	private boolean hasNextReview(Document Doc) {
		Element content = (Element) soupDoc.getElementsByClass("next").first();
		if (content != null) {
			String reviewLink = content.select("a[href]").attr("href");
			log.debug("Next Url link : " + reviewLink);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void finish() {
	}
}
