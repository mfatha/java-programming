package com.munna.utility.handler;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.common.db.connection.factory.DataManager;
import com.munna.common.service.csvparser.CSVParser;
import com.munna.utility.cache.WebSurfConstants;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

public class LoadDataToDumpHandler extends WebScrapHandler{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoadDataToDumpHandler.class);
	
	DataManager dataManager = new DataManager();

	public void startProcess() {
		LOGGER.info("Create Table for data Insert");
		createTables();
		LOGGER.info("Reading CSV files from College_list Folder..");
		LOGGER.info("Reading CSV files from C360 Folder..");
		fetchCSVFilesFromFolder2(new File(WebSurfConstants.OUTPUT_FOLDER.concat("CollegesReview_c360")), 1);		
	}

	private void createTables() {
		if(!dataManager.isTableExist(WebSurfConstants.SQLConstant.COLLEGE_LIST)) {
			dataManager.createTable(WebSurfConstants.SQLConstant.COLLEGE_LIST_TABLE_QUERY);
		}
		if(!dataManager.isTableExist(WebSurfConstants.SQLConstant.REVIEW_LIST)) {
			dataManager.createTable(WebSurfConstants.SQLConstant.REVIEW_LIST_QUERY);
		}
		if(!dataManager.isTableExist(WebSurfConstants.SQLConstant.REVIEW_DATA)) {
			dataManager.createTable(WebSurfConstants.SQLConstant.REVIEW_DATA_QUERY);
		}
	}

	private void fetchCSVFilesFromFolder2(File folder, int handleType) {
		long start = System.currentTimeMillis();
		long toalNumberOfFiles = 0L;
		try {
			for (final File fileEntry : folder.listFiles()) {
				if (fileEntry.isDirectory()) {
					fetchCSVFilesFromFolder2(fileEntry,handleType);
				} else {
					if (getFileExtension(fileEntry).equalsIgnoreCase("csv")) {
						synchronized (LoadDataToDumpHandler.class) {
							readDataFromCSV2(fileEntry,handleType);
							toalNumberOfFiles++;
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error occured", e);
		} finally {
			LOGGER.info("TOTAL NUMBER OF FILES CRAWLED FOR UTILITY : " + toalNumberOfFiles);
			long mins = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start);
			LOGGER.info("Final... TIME TAKEN FOR Folder Crawl : " + folder.getName() + " is :" + mins + " minutes.");
		}
	}

	private void readDataFromCSV2(File fileEntry, int handleType) {
		LOGGER.info("fetching Data for : " + fileEntry.getName());
		long lineNumber = 1L;
		CSVParser csvParser = new CSVParser();
		ArrayList<String[]> rowData = new ArrayList<String[]>();
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
						LOGGER.info("removing column Names from CSV File...[Header (1st Row)]");
					} else {
						rowData.add(row);
					}
					lineNumber++;
				}
				if (rowData.size() > 0) {
					try {
						insertDataIntoTable(rowData,handleType);
					} catch (Exception e) {
						LOGGER.error("Error occured", e);
					} finally {
						
					}

				}
			}

		} catch (Exception e) {
			LOGGER.error("Error throwed in readDataFromCSV method due to  : " + e);
		} finally {
			LOGGER.info("Utility Completed.....");
			LOGGER.info("TIME TAKEN FOR FILE : " + fileEntry.getName() + " is : "
					+ (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start) + " minutes."));
		}
	}

	private void insertDataIntoTable(ArrayList<String[]> rowData, int handleType) {
		
	}

	@Override
	public void generateCsvReport() {
		
	}
}
