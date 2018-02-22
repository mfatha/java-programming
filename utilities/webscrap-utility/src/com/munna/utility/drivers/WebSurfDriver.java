package com.munna.utility.drivers;

import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.munna.common.executor.factory.ExecutorServiceFactory;
import com.munna.utility.cache.WebSurfConstants;
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

	private static Log log = LogFactory.getLog(WebSurfDriver.class);

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
			System.out.println("Enter your option:");
			String proceed = sc.nextLine();
			WebScrapHandler webScrap = null;
			if (proceed != null && "1".equalsIgnoreCase(proceed.trim())) {
				webScrap = new WebScrapC360ListHandler();
			} else if (proceed != null && "2".equalsIgnoreCase(proceed.trim())) {
				webScrap = new WebScrapC360ReviewHandler();
			} else if (proceed != null && "3".equalsIgnoreCase(proceed.trim())) {
				webScrap = new WebScrapCDListHandler();
			} else if (proceed != null && "4".equalsIgnoreCase(proceed.trim())) {
				webScrap = new WebScrapCDReviewHandler();
			} else if (proceed != null && "5".equalsIgnoreCase(proceed.trim())) {
				webScrap = new WebScrapSSListHandler();
			} else if (proceed != null && "6".equalsIgnoreCase(proceed.trim())) {
				webScrap = new WebScrapSSReviewHandler();
			} else {
				log.error("Unknown option selection, Exiting Utility");
			}
			if (webScrap != null) {
				ExecutorServiceFactory.initialize("Executor", WebSurfConstants.THREAD_COUNT);
				webScrap.startProcess();
				ExecutorServiceFactory.getInstance("Executor").shutdown();
				log.info("Executor ShutDowned.....");
			}
		} catch (Exception e) {
			log.error("Error in main process", e);
		} finally {
			IOUtils.closeQuietly(sc);
		}
	}

}
