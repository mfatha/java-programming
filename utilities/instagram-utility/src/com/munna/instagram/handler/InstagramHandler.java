package com.munna.instagram.handler;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.common.service.api.UtilityService;
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
		File configFile = new File("");
		try {
			InstagramConnectionFactory.getInstance().initializeConnection(configFile);
		} catch (Exception e) {
			LOGGER.error("Error IG connection initializtion. ",e);	
		}
		LOGGER.info("DB connection initialized");			
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
