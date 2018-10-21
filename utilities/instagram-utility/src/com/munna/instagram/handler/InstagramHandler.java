package com.munna.instagram.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;
import org.brunocvcunha.instagram4j.requests.InstagramDirectShareRequest;
import org.brunocvcunha.instagram4j.requests.InstagramDirectShareRequest.ShareType;
import org.brunocvcunha.instagram4j.requests.InstagramFollowRequest;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowersRequest;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowingRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.InstagramUnfollowRequest;
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
	
	@Override
	public void sleep() {
		if (InstaConstants.THREAD_SLEEP_ENABLED) {
			try {
				LOGGER.info("Thead sleeping for Undeduction. About ["+ InstaConstants.THREAD_SLEEP_DELAY +"] milliseconds");
				Thread.sleep(InstaConstants.THREAD_SLEEP_DELAY);
			} catch (InterruptedException e) {
				LOGGER.error("Error occured while sleep");
			}
		}
	}

	
	@Override
	public void run() {
		init();
		process();
		finish();
	}
}
