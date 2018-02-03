package com.munna.utility.handler;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.munna.common.cache.UtilityCache;
import com.munna.common.executor.factory.ExecutorServiceFactory;
import com.munna.common.service.api.UtilityService;
import com.munna.common.service.csvparser.CSVParser;
import com.munna.utility.cache.WebSurfConstants;
import com.munna.utility.impl.CSVParserServices;
import com.munna.utility.impl.JsoupServices;
import com.munna.utility.service.impl.generateC360ReviewReport;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

/**
 * @author Mohammed Fathauddin
 * @since 2018
 */
public class WebScrapC360ReviewHandler extends WebScrapHandler {

	private Log log = LogFactory.getLog(this.getClass());
	
	private long batchNumber = 1L;

	@Override
	public void startProcess() {
		log.info("Reading CSV files from College_list Folder..");
		fetchCSVFilesFromFolder(new File(WebSurfConstants.OUTPUT_FOLDER.concat("CollegesList_c360")));
	}

	private void fetchCSVFilesFromFolder(File folder) {
		long start = System.currentTimeMillis();
		long toalNumberOfFiles = 0L;
		try {
			for (final File fileEntry : folder.listFiles()) {
				if (fileEntry.isDirectory()) {
					fetchCSVFilesFromFolder(fileEntry);
				} else {
					if (getFileExtension(fileEntry).equalsIgnoreCase("csv")) {
						synchronized (WebScrapC360ReviewHandler.class) {
							readDataFromCSV(fileEntry);
							toalNumberOfFiles++;
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e);
		} finally {
			log.info("TOTAL NUMBER OF FILES CRAWLED FOR UTILITY : " + toalNumberOfFiles);
			long end = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis());
			log.info("Final... TIME TAKEN FOR Folder Crawl : " + folder.getName() + " is :" + (end - start));
		}
	}

	private void readDataFromCSV(File fileEntry) {
		log.info("fetching Data for : " + fileEntry.getName());
		long lineNumber = 1L;
		CSVParser csvParser = new CSVParser();
		ArrayList<String> urlLinks = new ArrayList<String>();
		CsvFormat csvFormat = new CsvFormat();
		InputStream inputStream = null;
		long start = System.currentTimeMillis();
		try {
			// set recordtoRead as -1 to read all records in CSV file
			CsvParserSettings parserSettings = csvParser.getCurrentCsvParserSettings(-1, csvFormat, false);
			CsvParser parser = csvParser.getCsvParser(parserSettings);
			inputStream = FileUtils.openInputStream(fileEntry);
			if (parser != null && inputStream != null) {
				parser.beginParsing(inputStream);
				String[] row;
				while ((row = parser.parseNext()) != null) {
					if (lineNumber == 1L) {
						log.info("removing column Names from CSV File...[Header (1st Row)]");
					} else {
						urlLinks.add(row[0]);
					}
					lineNumber++;
				}
				if (urlLinks.size() > 0) {
					try {
						List<Future<?>> futures = new ArrayList<Future<?>>();
						UtilityService utilityService = null;						
						for (String url : urlLinks) {
							utilityService = new generateC360ReviewReport(url);
							futures.add(ExecutorServiceFactory.getInstance("Executor").addService(utilityService));
						}
						for (Future<?> future : futures) {
							future.get();
						}
					} catch (Exception e) {
						log.error(e);
					} finally {
						synchronized (WebScrapC360ReviewHandler.class) {
							generateCsvReport();
						}						
					}

				}
			}

		} catch (Exception e) {
			log.error("Error throwed in readDataFromCSV method due to  : " + e);
		} finally {
			log.info("Utility Completed.....");
			log.info("TIME TAKEN FOR FILE : " + fileEntry.getName() + " is : " + (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()) - start ));
		}
	}

	@Override
	public void generateCsvReport() {
		log.info("generating csv report process started...");
		String outputDirectory = WebSurfConstants.OUTPUT_FOLDER.concat("CollegesReview_c360");
		CSVParserServices csvHandler = new CSVParserServices();
		csvHandler.createDirectory(outputDirectory);
		String outputFileName =outputDirectory.concat(java.io.File.separator).concat("college_review_" + batchNumber+".csv");
		List<String> columnNames = WebSurfConstants.C360Constants.COLUMN_NAMES;
		List<Map<String,Object>> columnData = new ArrayList<Map<String,Object>>();
		try {
			Map<String, Object> reviewData = UtilityCache.getInstance().getEntireCache();
			 for (Entry<String, Object> entry : reviewData.entrySet()) {
				 columnData.add((Map<String, Object>) entry.getValue());
			 }
		}catch(Exception e) {
			log.error("error while parsing cache data :"+e);
		}
		try {
			FileWriter fileWriter = new FileWriter(outputFileName, true);
			StringBuffer document = new StringBuffer();
			for(String column : columnNames){
				document.append("\"" + column + "\"").append(","); 
			}
			String outputDocument = "";
			if (document != null && document.length() > 0 && document.charAt(document.length()-1)==',') {
				outputDocument = document.toString();
				outputDocument = outputDocument.substring(0, outputDocument.length()-1);
		    }
			fileWriter.append(outputDocument);
			fileWriter.append("\n");
			document = new StringBuffer();
			for(Map<String, Object> result : columnData){
				System.out.println(result.toString());
				for(String column : columnNames){				
					String data = (String) result.get(column);
					if(data == null || data.length() == 0){
						data = "null";
					}
					if(data.indexOf(",") >=0){
						data = data.replaceAll("," , "[comma]");
					}
					document.append("\"" + data.replaceAll("\n", " ") + "\"").append(","); 
				}
				outputDocument = "";
				if (document != null && document.length() > 0 && document.charAt(document.length()-1)==',') {
					outputDocument = document.toString();
					outputDocument = outputDocument.substring(0, outputDocument.length()-1);
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
