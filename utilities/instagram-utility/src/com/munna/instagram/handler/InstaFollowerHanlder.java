package com.munna.instagram.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.instagram.constants.InstaConstants;

public class InstaFollowerHanlder extends InstagramHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(InstaFollowerHanlder.class);
	
	String IgUsername = InstaConstants.AuthenticationConstant.IG_USERNAME;
	
	@Override
	public void init() {		
		Map<String,List<String>> followers = new HashMap<String,List<String>>();
		followers = getFollowers(IgUsername);
	}

}
