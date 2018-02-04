package com.munna.utility.handler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.munna.common.executor.factory.ExecutorServiceFactory;
import com.munna.common.service.api.UtilityService;
import com.munna.common.service.csvparser.CSVParser;
import com.munna.utility.cache.WebSurfConstants;
import com.munna.utility.impl.CSVParserServices;
import com.munna.utility.service.impl.generateC360ReviewReport;
import com.munna.utility.service.impl.generateCDReviewReport;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

/**
 * @author Mohammed Fathauddin
 * @since 2018
 */
public abstract class WebScrapHandler {

	private Log log = LogFactory.getLog(this.getClass());

	private long recordCount = 1L;

	public abstract void startProcess();

	public abstract void generateCsvReport();

	protected String getFileExtension(File file) {
		String fileName = file.getName();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		} else {
			return "";
		}
	}

	protected void buildCSVFile(ArrayList<String> datas, int typeCode) {
		CSVParserServices csvparser = new CSVParserServices();
		csvparser.createDirectory(WebSurfConstants.OUTPUT_FOLDER);
		String outputFolder = "";
		String fileName = "";
		if (typeCode == 1) {
			// CSV CODE TO WRITE COLLEGE LIST FOR C36o.c0m
			outputFolder = WebSurfConstants.OUTPUT_FOLDER.concat("CollegesList_c360");
			csvparser.createDirectory(outputFolder);
			fileName = outputFolder.concat(java.io.File.separator) + "review_File" + recordCount + ".csv";
		} else if (typeCode == 2) {
			// CSV CODE TO WRITE COLLEGE LIST FOR Collgeduniya.c0m
			outputFolder = WebSurfConstants.OUTPUT_FOLDER.concat("CollegesList_collegeDuniya");
			csvparser.createDirectory(outputFolder);
			fileName = outputFolder.concat(java.io.File.separator) + "review_File" + recordCount + ".csv";
		}
		try {
			FileWriter fileWriter = new FileWriter(fileName, true);
			// COLUMN NAME
			if (typeCode == 1) {
				fileWriter.append("URL");
			} else if (typeCode == 2) {
				fileWriter.append("URL");
			}
			fileWriter.append("\n");
			for (String links : datas) {
				fileWriter.append(links + "\n");
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			log.error(e);
		} finally {
			log.info("Data id Witten to file : " + "review_File" + recordCount + ".csv");
			recordCount++;
		}
	}

	protected void fetchCSVFilesFromFolder(File folder, int handlerType) {
		long start = System.currentTimeMillis();
		long toalNumberOfFiles = 0L;
		try {
			for (final File fileEntry : folder.listFiles()) {
				if (fileEntry.isDirectory()) {
					fetchCSVFilesFromFolder(fileEntry, handlerType);
				} else {
					if (getFileExtension(fileEntry).equalsIgnoreCase("csv")) {
						synchronized (WebScrapHandler.class) {
							readDataFromCSV(fileEntry, handlerType);
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

	private void readDataFromCSV(File fileEntry, int handlerType) {
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
							if (handlerType == 1) {
								utilityService = new generateC360ReviewReport(url);
							} else {
								utilityService = new generateCDReviewReport(url);
							}
							if (utilityService != null) {
								futures.add(ExecutorServiceFactory.getInstance("Executor").addService(utilityService));
							}
						}
						for (Future<?> future : futures) {
							future.get();
						}
					} catch (Exception e) {
						log.error(e);
					} finally {
						synchronized (WebScrapHandler.class) {
							generateCsvReport();
						}
					}

				}
			}

		} catch (Exception e) {
			log.error("Error throwed in readDataFromCSV method due to  : " + e);
		} finally {
			log.info("Utility Completed.....");
			log.info("TIME TAKEN FOR FILE : " + fileEntry.getName() + " is : "
					+ (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()) - start));
		}
	}

}
