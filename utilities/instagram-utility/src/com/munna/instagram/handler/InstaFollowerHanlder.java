package com.munna.instagram.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.instagram.constants.InstaConstants;
import com.munna.instagram.factory.InstagramManager;

public class InstaFollowerHanlder extends InstagramHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(InstaFollowerHanlder.class);
	
	String IgUsername = InstaConstants.AuthenticationConstant.IG_USERNAME;
	
	Map<String,List<String>> followers = new HashMap<String,List<String>>();
	
	@Override
	public void init() {
		LOGGER.info("Starting Follower Manager Handler..");
		followers = InstagramManager.getFollowers(IgUsername);
		//TODO For every 15 mins get users list again and if new user send welcome message. 
		Boolean saveFollowersList = false;
		do{
			saveFollowersList = false;
			List<String> newFollowers = new ArrayList<String>();
			sleep();
			Map<String,List<String>> tempFollowers = new HashMap<String,List<String>>();
			tempFollowers = InstagramManager.getFollowers(IgUsername);
			if(tempFollowers != null && tempFollowers.size() != 0){
				for (Entry<String, List<String>> hashKey : tempFollowers.entrySet()) {
					 //System.out.println("Key = " + hashKey.getKey() + ", Value = " + hashKey.getValue()); 
					 if(hashKey.getValue() != null && hashKey.getValue().size() != 0){
						 //checking with the default followers list
						 List<String> followerList = new ArrayList<String>();
						 List<String> tempFollowerList = new ArrayList<String>();
						 if(followers.containsKey(hashKey.getKey())){
							 followerList = followers.get(hashKey.getKey());
							 tempFollowerList = hashKey.getValue();
							 for(String fol : tempFollowerList){
								 if(!followerList.contains(fol)){
									 saveFollowersList = true;
									 newFollowers.add(String.valueOf(InstagramManager.getUserDetails(fol).getUser().getPk()));
								 }
							 }
						 }else{
							 tempFollowerList = hashKey.getValue();
							 for(String fol : tempFollowerList){
								 saveFollowersList = true;
								 newFollowers.add(String.valueOf(InstagramManager.getUserDetails(fol).getUser().getPk()));
							 }
							 saveFollowersList = true;
						 }
					 }
				}
			}
			if(saveFollowersList){
				LOGGER.info("Sending Welcome message to new users...");
				for(String newFollow : newFollowers){
					List<String> temp = new ArrayList<String>();
					temp.add(newFollow);
					InstagramManager.message(temp);
				}
			}
			//Loading new map to global variable
			followers = tempFollowers;
			LOGGER.info("Global follower map has been changed..");
		}while(!InstaConstants.AuthenticationConstant.IG_STOP_PROCESS);		
	}


	@Override
	public void sleep() {
		if (InstaConstants.THREAD_SLEEP_ENABLED) {
			try {
				long waitingMins = 15;
				LOGGER.info("Thead sleeping for Undeduction. About ["+ TimeUnit.MINUTES.toMillis(waitingMins) +"] milliseconds");
				Thread.sleep(TimeUnit.MINUTES.toMillis(waitingMins));
			} catch (InterruptedException e) {
				LOGGER.error("Error occured while sleep");
			}
		}
	}

}
