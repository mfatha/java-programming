package com.munna.instagram.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowingRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.InstagramUnfollowRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
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

	protected InstagramSearchUsernameResult getUserDetails(String username) {
		try {
			return InstagramConnectionFactory.getInstance().getConnection().sendRequest(new InstagramSearchUsernameRequest(username));
		} catch (ClientProtocolException e) {
			LOGGER.error("ClientProtocol Error. ",e);	
		} catch (IOException e) {
			LOGGER.error("IOException Error. ",e);	
		}
		return null;
	}
	
	protected InstagramGetUserFollowersResult getFollowingUser(long igId) {
		try {
			return InstagramConnectionFactory.getInstance().getConnection().sendRequest(new InstagramGetUserFollowingRequest(igId));
		} catch (ClientProtocolException e) {
			LOGGER.error("ClientProtocol Error. ",e);	
		} catch (IOException e) {
			LOGGER.error("IOException Error. ",e);	
		}
		return null;
	} 
	
	protected void unFollowUser(long igId) throws ClientProtocolException, IOException {
		InstagramConnectionFactory.getInstance().getConnection().sendRequest(new InstagramUnfollowRequest(igId));
	}
	
	
	protected boolean stopProcess() {
		File configFile = new File(InstaConstants.CONFIGURATION_FILE);
		Properties properties = new Properties();
		Boolean stopProcess = false;
		InputStream iStream;
		try {
			iStream = new FileInputStream(configFile);
			properties.load(iStream);
		} catch (Exception e) {
			LOGGER.error("Error occured while initializinng the property file connection for ".concat(InstaConstants.CONFIGURATION_FILE), e);
		}
		stopProcess = Boolean.parseBoolean(properties.getProperty(InstaConstants.AuthenticationConstant.STOP_PROCESS));
		return stopProcess;
	}
	
	@Override
	public void run() {
		init();
		process();
		finish();
	}
}
