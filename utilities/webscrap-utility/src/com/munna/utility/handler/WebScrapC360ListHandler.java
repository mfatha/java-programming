package com.munna.utility.handler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.munna.common.properties.PropertiesProvider;
import com.munna.utility.cache.WebSurfConstants;
import com.munna.utility.impl.CSVParserServices;
import com.munna.utility.impl.JsoupServices;

public class WebScrapC360ListHandler extends WebScrapHandler {

	private static Log log = LogFactory.getLog(WebScrapC360ListHandler.class);

	private long recordCount = 1L;

	@Override
	public void startProcess() {
		String siteOneListUrlFormat = WebSurfConstants.SoupUrls.SITE_ONE_LIST_URL_FORMAT;
		int numberOfPagesToCrowl = 210;
		int numberOfRecordsPerCsv = Integer
				.parseInt(PropertiesProvider.getInstance().getProperties(WebSurfConstants.CONFIGURATION_FILE)
						.getProperty(WebSurfConstants.ConfigurationProperty.NUMBER_OF_RECORDS_PER_CSV));
		boolean hasNext = true;
		int pageNumber = 0;
		ArrayList<String> collegeLinks = new ArrayList<String>();
		while (hasNext) {
			try {
				Document soupDoc = (Document) JsoupServices.getInstance()
						.getConnection(siteOneListUrlFormat + pageNumber);
				Elements content = (Elements) soupDoc.getElementsByClass("combine-block");
				for (Element e : content) {
					Elements title = e.getElementsByClass("title");
					for (Element t : title) {
						Elements reviewlink = t.select("a[href]");
						collegeLinks.add(reviewlink.attr("href"));
					}
				}
				if (collegeLinks.size() >= numberOfRecordsPerCsv) {
					buildCSVFile(collegeLinks, 1);
					collegeLinks = new ArrayList<String>();
				}
				pageNumber++;
				if (pageNumber >= numberOfPagesToCrowl) {
					hasNext = false;
				}
			} catch (Exception e) {
				log.error("Error in parsing the Links " + e);
				hasNext = false;
			}
		}
		if (collegeLinks != null && collegeLinks.size() != 0) {
			buildCSVFile(collegeLinks, 1);
			collegeLinks = new ArrayList<String>();
		}
		JsoupServices.getInstance().clearConnections();
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
		}
		try {
			FileWriter fileWriter = new FileWriter(fileName, true);
			if (typeCode == 1) {
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

	@Override
	public void generateCsvReport() {
		log.info("Report has Been generated simulaneously in this...");
	}



}
