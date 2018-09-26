package com.munna.instagram.handler;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowersRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.common.service.api.UtilityService;
import com.munna.instagram.constants.InstaConstants;
import com.munna.instagram.factory.InstagramConnectionFactory;

/**
 * @author Mohammed Fathauddin
 * @since 2018
 */

public class InstagramHandler extends UtilityService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InstagramHandler.class);
	
	public void startProcess(){
		LOGGER.info("Processing data load to Dump");			
		//init IG connection
		File configFile = new File(InstaConstants.CONFIGURATION_FILE);
		try {
			InstagramConnectionFactory.getInstance().initializeConnection(configFile);
		} catch (Exception e) {
			LOGGER.error("Error IG connection initializtion. ",e);	
		}
		LOGGER.info("IG connection initialized");
		Instagram4j instG= InstagramConnectionFactory.getInstance().getConnection();
		try {
			InstagramSearchUsernameResult userResult = instG.sendRequest(new InstagramSearchUsernameRequest("vennu_ks"));
			System.out.println("ID for @vennu_ks is " + userResult.getUser().getPk());
			System.out.println("Number of followers: " + userResult.getUser().getFollower_count());
			InstagramGetUserFollowersResult githubFollowers = instG.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk()));
			List<InstagramUserSummary> users = githubFollowers.getUsers();
			for (InstagramUserSummary user : users) {
			    System.out.println(user.getUsername());
			}
			githubFollowers = instG.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk(), githubFollowers.next_max_id));
			users = githubFollowers.getUsers();
			for (InstagramUserSummary user : users) {
			    System.out.println(user.getUsername());
			}
		} catch (ClientProtocolException e) {
			LOGGER.error("ClientProtocol Error. ",e);	
		} catch (IOException e) {
			LOGGER.error("IOException Error. ",e);	
		}
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void process() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}
}
