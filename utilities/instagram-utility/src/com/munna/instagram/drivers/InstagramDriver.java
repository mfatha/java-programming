package com.munna.instagram.drivers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.common.executor.factory.ExecutorServiceFactory;
import com.munna.instagram.constants.InstaConstants;
import com.munna.instagram.handler.InstaFollowerHanlder;
import com.munna.instagram.handler.InstaFollowingHanlder;
import com.munna.instagram.handler.InstaSocialHanlder;
import com.munna.instagram.handler.InstagramHandler;

/**
 * @author Mohammed Fathauddin
 * @since 2018
 * @credit https://github.com/brunocvcunha/instagram4j
 */
public class InstagramDriver {

	private static final Logger LOGGER = LoggerFactory.getLogger(InstagramDriver.class);

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		ExecutorServiceFactory.initialize("IG_Executor", InstaConstants.THREAD_COUNT);
		try {
			System.out.println("Welcome: Instagram Utility");
			System.out.println("Choose the Instagram working Type");
			System.out.println("1) IG Following Handler");
			System.out.println("2) IG Followers Handler");
			System.out.println("3) IG Socialise Handler");
			System.out.println("4) IG Take Over Handler");
			String proceed = sc.nextLine();
			InstagramHandler IG = null;
			if (proceed != null) {
				List<Future<?>> futures = new ArrayList<Future<?>>();
				switch (proceed.trim()) {
					case "1": 
						IG = new InstaFollowingHanlder();
						break;
					case "2":
						IG = new InstaFollowerHanlder();
						break;
					case "3":
						IG = new InstaSocialHanlder();
						break;
					case "4":
						//TODO make it do all process in parallel
						break;
				}
				if(IG != null){
					IG.startProcess();
					futures.add(ExecutorServiceFactory.getInstance("IG_Executor").addService(IG));
				}	
				for (Future<?> future : futures) {
					future.get();
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error in main process", e);
		} finally {
			ExecutorServiceFactory.getInstance("IG_Executor").shutdown();
			LOGGER.info("Executor ShutDowned...");
			//TODO proper finish
		}
	}

}
