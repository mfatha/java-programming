package com.munna.utility.handler;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.common.db.connection.factory.DataSchemaManager;
import com.munna.common.service.csvparser.CSVParser;
import com.munna.common.util.Util;
import com.munna.utility.cache.WebSurfConstants;
import com.munna.utility.reviewer.C360ReviewerHandler;
import com.munna.utility.reviewer.CDuniaReviewerHandler;
import com.munna.utility.reviewer.ReviewerHandler;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

public class LoadDataToDumpHandler extends WebScrapHandler{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoadDataToDumpHandler.class);
	
	DataSchemaManager dataManager = new DataSchemaManager();

	public void startProcess() {
		LOGGER.info("Create Table for data Insert");
		createTables();
		LOGGER.info("Reading CSV files from College_list Folder..");
		LOGGER.info("Reading CSV files from C360 Folder..");
		fetchCSVFilesFromFolder2(new File(WebSurfConstants.OUTPUT_FOLDER.concat("CollegesReview_c360")), 1);		
	}

	private void createTables() {
		try {
			if(!dataManager.isTableExist(WebSurfConstants.SQLConstant.COLLEGE_LIST)) {
					dataManager.createTable(WebSurfConstants.SQLConstant.COLLEGE_LIST_TABLE_QUERY);
			}
			if(!dataManager.isTableExist(WebSurfConstants.SQLConstant.REVIEW_LIST)) {
				dataManager.createTable(WebSurfConstants.SQLConstant.REVIEW_LIST_QUERY);
			}
			if(!dataManager.isTableExist(WebSurfConstants.SQLConstant.REVIEW_DATA)) {
				dataManager.createTable(WebSurfConstants.SQLConstant.REVIEW_DATA_QUERY);
			}
		} catch (Exception e) {
			LOGGER.error("Error occured :\n", e);
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
						if(handleType != 3){
							insertDataIntoTable(rowData,handleType);
						}						
					} catch (Exception e) {
						LOGGER.error("Error occured", e);
					} finally {
						//TODO finish the code;
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

	@SuppressWarnings("unchecked")
	private void insertDataIntoTable(ArrayList<String[]> rowsData, int handleType) {
		List<String> columnNames = getColumnNames(handleType);
		Map<String,String> data = new HashMap<String,String>();
		if(!rowsData.isEmpty() && !columnNames.isEmpty()){
			for(String[] rowData : rowsData ){
				for(int i = 0;i<columnNames.size();i++) {
					data.put(columnNames.get(i), rowData[i]);
				}
				LOGGER.debug(data.toString());
				//check data present in table and organize the data..
				data = (Map<String, String>) checkDataExist(data,handleType);
				if(!data.isEmpty()) {
					if(data.containsValue("COLLEGE_REVIEWER_PRESENT_IN_LIST") && Boolean.parseBoolean(data.get("COLLEGE_REVIEWER_PRESENT_IN_LIST"))) {
						//TODO Update in review_data table
					}else if(data.containsValue("COLLEGE_PRESENT_IN_LIST") && Boolean.parseBoolean(data.get("COLLEGE_PRESENT_IN_LIST"))) {
						//TODO Insert into reviewer list table and add review_data table
					}else {
						//create new record in college_list table
					}
				}
			}
		}
	}

	private Object checkDataExist(Map<String, String> data, int handleType) {
		data = Util.removeNoise(data); 
		ReviewerHandler reviewer = null;
		switch(handleType) {
			case 1: reviewer = new C360ReviewerHandler();
					break;
			case 2: reviewer = new CDuniaReviewerHandler();
					break;
			/*case 3: reviewer = new SkishaReviewerHandler();
					break;*/
		}
		return reviewer.process(data);
	}

	private List<String> getColumnNames(int handleType) {
		List<String> columnNames = new ArrayList<String>();
		switch(handleType){
			case 1: columnNames = WebSurfConstants.C360Constants.COLUMN_NAMES;
					break;
			case 2: columnNames = WebSurfConstants.CollegeDuniyaConstants.COLUMN_NAMES;
					break;
		}
		return columnNames;
	}

	@Override
	public void generateCsvReport() {
		
	}
}
