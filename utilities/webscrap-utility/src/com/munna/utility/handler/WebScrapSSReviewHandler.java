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
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.common.cache.UtilityCache;
import com.munna.utility.bean.Review;
import com.munna.utility.cache.WebSurfConstants;
import com.munna.utility.impl.CSVParserServices;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(WebScrapSSReviewHandler.class);

	private long batchNumber = 1L;

	@Override
	public void startProcess() {
		LOGGER.info("Reading CSV files from College_list Folder..");
		fetchCSVFilesFromFolder(new File(WebSurfConstants.OUTPUT_FOLDER.concat("CollegesList_shiksha")), 3);
	}

	@Override
	public void generateCsvReport() {
		LOGGER.info("Writing review data as json...");
		writeReviewJson();
		LOGGER.info("Finished writing review data as json...");
	}

	@SuppressWarnings("unchecked")
	private void writeReviewJson() {
		final String outputDirectory = WebSurfConstants.OUTPUT_FOLDER.concat("CollegesReview_shiksha");
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
			final Map<String, Object> reviewData = new HashMap<String, Object>();
			colleges = cacheData.size();
			for (Entry<String, Object> review : cacheData.entrySet()) {
				final List<Review> reviewList = (List<Review>) review.getValue();
				if (count == limit) {
					outputFileName = getUpdatedFileName(outputFileTemplate, batchNumber++);
					writeJson(outputFileName, reviewData);
					reviewData.clear();
					count = 0;
				} else if (reviewList.size() + count <= limit) {
					reviewData.put(review.getKey(), review.getValue());
					count += reviewList.size();
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
			if (!reviewData.isEmpty()) {
				outputFileName = getUpdatedFileName(outputFileTemplate, batchNumber);
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
