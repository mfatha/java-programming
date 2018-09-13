package com.munna.utility.drivers;

import java.io.File;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.common.db.connection.factory.DatabaseConnectionFactory;
import com.munna.common.executor.factory.ExecutorServiceFactory;
import com.munna.utility.cache.WebSurfConstants;
import com.munna.utility.handler.LoadDataToDumpHandler;
import com.munna.utility.handler.WebScrapC360ListHandler;
import com.munna.utility.handler.WebScrapC360ReviewHandler;
import com.munna.utility.handler.WebScrapCDListHandler;
import com.munna.utility.handler.WebScrapCDReviewHandler;
import com.munna.utility.handler.WebScrapHandler;
import com.munna.utility.handler.WebScrapSSListHandler;
import com.munna.utility.handler.WebScrapSSReviewHandler;

/**
 * @author Mohammed Fathauddin
 * @since 2018
 */
public class WebSurfDriver {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebSurfDriver.class);

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		try {
			System.out.println("Welcome: WebScrap Utility");
			System.out.println("Choose the Website to Scrap");
			System.out.println("1) Car33r36O.c0m ->Getting Colleges_list");
			System.out.println("2) Car33r36O.c0m ->Getting Colleges Reviews");
			System.out.println("3) C0llegeDuniya.c0m ->Getting Colleges_list");
			System.out.println("4) C0llegeDuniya.c0m ->Getting Colleges Reviews");
			System.out.println("5) Shiksha.c0m ->Getting Colleges_list");
			System.out.println("6) Shiksha.c0m ->Getting Colleges Reviews");
			System.out.println("7) Load Collected Data to Data_Dump ");
			System.out.println("Enter your option:");
			String proceed = sc.nextLine();
			WebScrapHandler webScrap = null;
			if(proceed != null) {
				switch(proceed.trim()) {
					case "1": 	webScrap = new WebScrapC360ListHandler();
								break;
					case "2": 	webScrap = new WebScrapC360ReviewHandler();
								break;
					case "3": 	webScrap = new WebScrapCDListHandler();
								break;
					case "4": 	webScrap = new WebScrapCDReviewHandler();
								break;
					case "5": 	webScrap = new WebScrapSSListHandler();
								break;
					case "6": 	webScrap = new WebScrapSSReviewHandler();
								break;
					case "7": 	webScrap = loadDataToDump();
								break;
					default : 	LOGGER.error("Unknown option selection, Exiting Utility");
					
				}
			}else {
				LOGGER.error("Unknown option selection, Exiting Utility");
			}
			
			if (webScrap != null) {
				ExecutorServiceFactory.initialize("Executor", WebSurfConstants.THREAD_COUNT);
				webScrap.startProcess();
				ExecutorServiceFactory.getInstance("Executor").shutdown();
				LOGGER.info("Executor ShutDowned...");
			}
		} catch (Exception e) {
			LOGGER.error("Error in main process", e);
		} finally {
			IOUtils.closeQuietly(sc);
		}
	}

	private static WebScrapHandler loadDataToDump() {
		try {
			LOGGER.info("Processing data load to Dump");			
			//init database connection
			File configFile = new File(WebSurfConstants.DataBaseConstant.DB_CONFIGURATION_FILE);
			DatabaseConnectionFactory.getInstance().initializeConnection(configFile);
			LOGGER.info("DB connection initialized");			
			//load data to the database
			LoadDataToDumpHandler loadData = new LoadDataToDumpHandler();
			return loadData;		
		}catch(Exception e) {
			LOGGER.error("Process initialization failed, Unable to continue the process", e);
		}
		return null;
	}

}
