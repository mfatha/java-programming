package com.munna.utility.handler;

import java.io.File;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
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
		fetchCSVFilesFromFolder(new File(WebSurfConstants.OUTPUT_FOLDER.concat("CollegesReview_c360")), 1);		
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
	
	@Override
	protected void fetchCSVFilesFromFolder(File folder, int handleType) {
		long start = System.currentTimeMillis();
		long toalNumberOfFiles = 0L;
		try {
			for (final File fileEntry : folder.listFiles()) {
				if (fileEntry.isDirectory()) {
					fetchCSVFilesFromFolder(fileEntry,handleType);
				} else {
					if (getFileExtension(fileEntry).equalsIgnoreCase("csv")) {
						synchronized (LoadDataToDumpHandler.class) {
							readDataFromCSV(fileEntry,handleType);
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

	private void readDataFromCSV(File fileEntry, int handleType) {
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
		Map<String,String> collegeListDataSchemaMap = new HashMap<String,String>();
		if(!rowsData.isEmpty() && !columnNames.isEmpty()){
			for(String[] rowData : rowsData ){
				for(int i = 0;i<columnNames.size();i++) {
					collegeListDataSchemaMap.put(columnNames.get(i), rowData[i]);
				}
				LOGGER.debug(collegeListDataSchemaMap.toString());
				//check data present in table and organize the data..
				collegeListDataSchemaMap = (Map<String, String>) checkDataExist(collegeListDataSchemaMap,handleType);
				if(!collegeListDataSchemaMap.isEmpty()) {
					if(isReviewDataPresent(collegeListDataSchemaMap,handleType)) {
						//TODO Update in review_data table
						if(!collegeListDataSchemaMap.containsKey("COLLEGE_ID")) {
							collegeListDataSchemaMap.put("COLLEGE_ID", getCollegeIdFromName(collegeListDataSchemaMap.get("COLLEGE_NAME")));
						}
						if(!collegeListDataSchemaMap.containsKey("REVIEW_TO_ID")) {
							collegeListDataSchemaMap.put("REVIEW_TO_ID", getReviewId(collegeListDataSchemaMap.get("COLLEGE_ID"), collegeListDataSchemaMap.get("REVIEW_IN_SOURCE_ID")));
						}
						updateReviewData(collegeListDataSchemaMap);
						LOGGER.info("INSERTED DATA FOR "+ collegeListDataSchemaMap);
					}else if(collegeListDataSchemaMap.containsKey("COLLEGE_PRESENT_IN_LIST") && Boolean.parseBoolean(collegeListDataSchemaMap.get("COLLEGE_PRESENT_IN_LIST"))) {
						if(!collegeListDataSchemaMap.containsKey("COLLEGE_ID")) {
							collegeListDataSchemaMap.put("COLLEGE_ID", getCollegeIdFromName(collegeListDataSchemaMap.get("COLLEGE_NAME")));
						}
						insertIntoReviewList(collegeListDataSchemaMap);
						if(!collegeListDataSchemaMap.containsKey("REVIEW_TO_ID")) {
							collegeListDataSchemaMap.put("REVIEW_TO_ID", getReviewId(collegeListDataSchemaMap.get("COLLEGE_ID"), collegeListDataSchemaMap.get("REVIEW_IN_SOURCE_ID")));
						}
						insertIntoReviewData(collegeListDataSchemaMap);
						LOGGER.info("INSERTED DATA FOR "+ collegeListDataSchemaMap);
					}else {
						insertIntoCollegeList(collegeListDataSchemaMap);
						if(!collegeListDataSchemaMap.containsKey("COLLEGE_ID")) {
							collegeListDataSchemaMap.put("COLLEGE_ID", getCollegeIdFromName(collegeListDataSchemaMap.get("COLLEGE_NAME")));
						}
						insertIntoReviewList(collegeListDataSchemaMap);
						if(!collegeListDataSchemaMap.containsKey("REVIEW_TO_ID")) {
							collegeListDataSchemaMap.put("REVIEW_TO_ID", getReviewId(collegeListDataSchemaMap.get("COLLEGE_ID"), collegeListDataSchemaMap.get("REVIEW_IN_SOURCE_ID")));
						}
						insertIntoReviewData(collegeListDataSchemaMap);
						LOGGER.info("INSERTED DATA FOR "+ collegeListDataSchemaMap);
					}
				}
			}
		}
	}

	private boolean isReviewDataPresent(Map<String, String> collegeListDataSchemaMap, int handleType) {
		JSONObject collegeDetails = new JSONObject(collegeListDataSchemaMap.get("RESULT_JSON"));
		if(collegeDetails != null) {
			if(collegeDetails.has("REVIEWERS")) {
				JSONArray array = collegeDetails.getJSONArray("REVIEWERS");
				if(array != null) {
					for(int i = 0 ; i<array.length(); i++) {
						if( array.getJSONObject(i).has("REVIEW_IN_SOURCE_ID") && array.getJSONObject(i).has("REVIEWS") && array.getJSONObject(i).getInt("REVIEW_IN_SOURCE_ID") == handleType) {
							JSONObject review = array.getJSONObject(i).getJSONObject("REVIEWS");
							if(review != null && review.length() != 0) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private void insertIntoCollegeList(Map<String, String> collegeListDataSchemaMap) {
		DataSchemaManager dataManager = new DataSchemaManager();
		//statement.executeUpdate("INSERT INTO Customers " + "VALUES (1001, 'Simpson', 'Mr.', 'Springfield', 2001)");
		String query =  "INSERT INTO college_list (COLLEGE_NAME) VALUES ('"+collegeListDataSchemaMap.get("COLLEGE_NAME")+"')";
		try {
			dataManager.executeUpdate(query);
		} catch (Exception e) {
			LOGGER.error("Error in execting query : " + e);
		}
	}
	
	private void insertIntoReviewList(Map<String, String> collegeListDataSchemaMap) {
		DataSchemaManager dataManager = new DataSchemaManager();
		//INSERT newtable (user,age,os) SELECT table1.user,table1.age,table2.os FROM table1,table2 WHERE table1.user=table2.user;
		if(collegeListDataSchemaMap.containsKey("COLLEGE_ID") && collegeListDataSchemaMap.get("COLLEGE_ID") != null) {
			String query =  "INSERT INTO review_list (COLLEGE_ID, REVIEW_IN_SOURCE_ID, SITE_URL ) VALUES ("+collegeListDataSchemaMap.get("COLLEGE_ID")+", "+collegeListDataSchemaMap.get("REVIEW_IN_SOURCE_ID")+", '"+collegeListDataSchemaMap.get("SITE_URL")+"')";
			try {
				dataManager.executeUpdate(query);
			} catch (Exception e) {
				LOGGER.error("Error in execting query : " + e);
			}
		}else {
			LOGGER.error("NO COLLEGE DETAILS FOUND...");
		}
	}
	
	private void insertIntoReviewData(Map<String, String> collegeListDataSchemaMap) {
		DataSchemaManager dataManager = new DataSchemaManager();
		if(collegeListDataSchemaMap.containsKey("REVIEW_TO_ID") && collegeListDataSchemaMap.get("REVIEW_TO_ID") != null) {
			for(String review : WebSurfConstants.SQLConstant.REVIEW_DATA_NAMES) {
				if(collegeListDataSchemaMap.containsKey(review) && collegeListDataSchemaMap.get(review)!= null) {
					String query =  "INSERT INTO review_data (REVIEW_ID, REVIEW_NAME, VALUE ) VALUES ( "+collegeListDataSchemaMap.get("REVIEW_TO_ID")+", '"+review+"', '"+collegeListDataSchemaMap.get(review)+"')";
					try {
						dataManager.executeUpdate(query);
					} catch (Exception e) {
						LOGGER.error("Error in execting query : " + e);
					}
				}			
			}
		}else {
			LOGGER.error("NO REVIEWER DETAILS FOUND...");
		}
	}
	
	private void updateReviewData(Map<String, String> collegeListDataSchemaMap) {
		DataSchemaManager dataManager = new DataSchemaManager();
		if(collegeListDataSchemaMap.containsKey("REVIEW_TO_ID") && collegeListDataSchemaMap.get("REVIEW_TO_ID") != null) {
			for(String review : WebSurfConstants.SQLConstant.REVIEW_DATA_NAMES) {
				if(collegeListDataSchemaMap.containsKey(review) && collegeListDataSchemaMap.get(review)!= null) {
					String query =  "UPDATE review_data SET VALUE = '"+collegeListDataSchemaMap.get(review)+"' WHERE REVIEW_ID = "+collegeListDataSchemaMap.get("REVIEW_TO_ID")+" AND REVIEW_NAME = '"+review+"'"; 
					try {
						dataManager.executeUpdate(query);
					} catch (Exception e) {
						LOGGER.error("Error in execting query : " + e);
					}
				}			
			}
		}else {
			LOGGER.error("NO REVIEWER DETAILS FOUND...");
		}
	}
	
	private String getCollegeIdFromName(String collegeName) {
		DataSchemaManager dataManager = new DataSchemaManager();
		String query =  "SELECT ID FROM college_list WHERE COLLEGE_NAME LIKE ('"+collegeName+"')";
		String id = null;
		try {
			ResultSet resultSet = dataManager.executeCommand(query);
			while(resultSet.next()) {
				id = resultSet.getString("ID");
			}
		} catch (Exception e) {
			LOGGER.error("Error in execting query : " + e);
		}
		return id;
	}
	
	private String getReviewId(String collegeId, String reviewerId) {
		DataSchemaManager dataManager = new DataSchemaManager();
		String query =  "SELECT ID FROM review_list WHERE COLLEGE_ID = "+collegeId+" AND REVIEW_IN_SOURCE_ID = "+reviewerId;
		String id = null;
		try {
			ResultSet resultSet = dataManager.executeCommand(query);
			while(resultSet.next()) {
				id = resultSet.getString("ID");
			}
		} catch (Exception e) {
			LOGGER.error("Error in execting query : " + e);
		}
		return id;
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
