package com.munna.utility.handler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.munna.utility.cache.WebSurfConstants;
import com.munna.utility.impl.CSVParserServices;

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
		}else if(typeCode == 2) {
			// CSV CODE TO WRITE COLLEGE LIST FOR Collgeduniya.c0m
			outputFolder = WebSurfConstants.OUTPUT_FOLDER.concat("CollegesList_collegeDuniya");
			csvparser.createDirectory(outputFolder);
			fileName = outputFolder.concat(java.io.File.separator) + "review_File" + recordCount + ".csv";
		}
		try {
			FileWriter fileWriter = new FileWriter(fileName, true);
			//COLUMN NAME
			if (typeCode == 1) {
				fileWriter.append("URL");
			}else if (typeCode == 2) {
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
}
