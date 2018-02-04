package com.munna.utility.handler;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.munna.common.cache.UtilityCache;
import com.munna.utility.cache.WebSurfConstants;
import com.munna.utility.impl.CSVParserServices;
import com.munna.utility.impl.JsoupServices;

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
		log.info("generating csv report process started...");
		String outputDirectory = WebSurfConstants.OUTPUT_FOLDER.concat("CollegesReview_shiksha");
		CSVParserServices csvHandler = new CSVParserServices();
		csvHandler.createDirectory(outputDirectory);
		String outputFileName = outputDirectory.concat(java.io.File.separator)
				.concat("college_review_" + batchNumber + ".csv");
		List<String> columnNames = WebSurfConstants.CollegeDuniyaConstants.COLUMN_NAMES;
		List<Map<String, Object>> columnData = new ArrayList<Map<String, Object>>();
		try {
			Map<String, Object> reviewData = UtilityCache.getInstance().getEntireCache();
			for (Entry<String, Object> entry : reviewData.entrySet()) {
				columnData.add((Map<String, Object>) entry.getValue());
			}
		} catch (Exception e) {
			log.error("error while parsing cache data :" + e);
		}
		try {
			FileWriter fileWriter = new FileWriter(outputFileName, true);
			StringBuffer document = new StringBuffer();
			for (String column : columnNames) {
				document.append("\"" + column + "\"").append(",");
			}
			String outputDocument = "";
			if (document != null && document.length() > 0 && document.charAt(document.length() - 1) == ',') {
				outputDocument = document.toString();
				outputDocument = outputDocument.substring(0, outputDocument.length() - 1);
			}
			fileWriter.append(outputDocument);
			fileWriter.append("\n");
			document = new StringBuffer();
			for (Map<String, Object> result : columnData) {
				System.out.println(result.toString());
				for (String column : columnNames) {
					String data = (String) result.get(column);
					if (data == null || data.length() == 0) {
						data = "null";
					}
					if (data.indexOf(",") >= 0) {
						data = data.replaceAll(",", "[comma]");
					}
					document.append("\"" + data.replaceAll("\n", " ") + "\"").append(",");
				}
				outputDocument = "";
				if (document != null && document.length() > 0 && document.charAt(document.length() - 1) == ',') {
					outputDocument = document.toString();
					outputDocument = outputDocument.substring(0, outputDocument.length() - 1);
				}
				fileWriter.append(outputDocument);
				document = new StringBuffer();
				fileWriter.append("\n");
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (Exception e) {
			log.error(e);
		} finally {
			batchNumber++;
			UtilityCache.getInstance().clearCache();
			JsoupServices.getInstance().clearConnections();
			log.info("finished csv report generation process...");
		}
	}

}
