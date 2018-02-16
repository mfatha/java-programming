package com.munna.utility.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.munna.common.cache.UtilityCache;
import com.munna.utility.bean.Review;
import com.munna.utility.cache.WebSurfConstants;
import com.munna.utility.impl.JsoupServices;

/**
 * The Class WebScrapSSReviewHandler.
 * 
 * TODO
 * 
 * Bypass shiksha.com bot check. Have to introduce wait between successive
 * requests.
 * 
 * @author Mohammed Fathauddin
 * @author Janardhanan V S
 */
public class WebScrapSSReviewHandler extends WebScrapHandler {

	private Log log = LogFactory.getLog(this.getClass());

	private long batchNumber = 1L;

	@Override
	public void startProcess() {
		log.info("Reading CSV files from College_list Folder..");
		fetchCSVFilesFromFolder(new File(WebSurfConstants.OUTPUT_FOLDER.concat("CollegesList_shiksha")), 3);
	}

	@Override
	public void generateCsvReport() {
		log.info("Writing review data as json...");
		writeReviewJson();
		log.info("finished writing review data as json...");
	}

	@SuppressWarnings("unchecked")
	private void writeReviewJson() {
		final String outputDirectory = WebSurfConstants.OUTPUT_FOLDER.concat("CollegesReview_shiksha");
		final String outputFileTemplate = outputDirectory.concat(java.io.File.separator).concat("college_review_");
		String outputFileName = getUpdatedFileName(outputFileTemplate, batchNumber);
		final int limit = WebSurfConstants.ShikshaConstants.RECORD_LIMIT;
		int count = 0;
		try {
			Map<String, Object> cacheData = UtilityCache.getInstance().getEntireCache();
			Map<String, Object> reviewData = new HashMap<>();
			for (Entry<String, Object> review : cacheData.entrySet()) {
				final List<Review> reviewList = (List<Review>) review.getValue();
				if (count == limit) {
					outputFileName = getUpdatedFileName(outputFileTemplate, batchNumber++);
					writeJson(outputFileName, reviewData);
					reviewData.clear();
					count = 0;
				} else if (reviewList.size() + count <= limit) {
					reviewData.put(review.getKey(), review.getValue());
				} else {
					int index = limit - count;
					reviewData.put(review.getKey(), reviewList.subList(0, index));
					outputFileName = getUpdatedFileName(outputFileTemplate, batchNumber++);
					writeJson(outputFileName, reviewData);
					reviewData.clear();
					reviewData.put(review.getKey(), reviewList.subList(index, reviewList.size()));
					count = index;
				}
			}
		} catch (Exception e) {
			log.error("error while writing review as json", e);
		} finally {
			UtilityCache.getInstance().clearCache();
			JsoupServices.getInstance().clearConnections();
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
		} catch (IOException e) {
			log.error("error while writing to file : " + filePath, e);
		}
	}

	private String getMinifiedJson(Map<String, Object> reviewData) {
		return new JSONObject(reviewData).toString();
	}

}
