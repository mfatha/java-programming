package com.munna.instagram.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.instagram.constants.InstaConstants;

/**
 * @author Mohammed Fathauddin
 * @since 2018
 */

public class InstaFollowingHanlder extends InstagramHandler{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InstaFollowingHanlder.class);
	
	String IgUsername = InstaConstants.AuthenticationConstant.IG_USERNAME;
	
	@Override
	public void init() {
		if(!InstaConstants.AuthenticationConstant.IG_STOP_PROCESS)
			do {
				Date now = new Date();
				SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
		        LOGGER.info("Its "+ simpleDateformat.format(now));
		        Calendar calendar = Calendar.getInstance();
		        calendar.setTime(now);
		        if(calendar.get(Calendar.DAY_OF_WEEK) == 1) {
		        	unFollowTheFollowingUsers();
		        }else
		        	searchForNewUsersAndFollowThem();
			}while(!stopProcess());		
	}

	private void searchForNewUsersAndFollowThem() {
		
	}

	private void unFollowTheFollowingUsers() {
		InstagramSearchUsernameResult user = getUserDetails(IgUsername);
		LOGGER.info("Number of followers for("+IgUsername+"): " + user.getUser().getFollower_count());
		LOGGER.info("Number of following for("+IgUsername+"): " + user.getUser().getFollowing_count());
		long followingPeopleCount = user.getUser().getFollowing_count();
		long i = 1L;
		while(followingPeopleCount != 0) {
			InstagramGetUserFollowersResult userFollowingList = getFollowingUser(user.getUser().getPk());
			List<InstagramUserSummary> followings = userFollowingList.getUsers();
			if(followings != null && followings.size() !=0) {
				for(InstagramUserSummary followingUser : followings) {
					try {
						LOGGER.info("Unfollowing USER :"+ followingUser.getUsername());
						unFollowUser(followingUser.getPk());
					} catch (ClientProtocolException e) {
						LOGGER.error("ClientProtocolException while trigerring unfollow user command ("+followingUser.getUsername()+")",e);
					} catch (IOException e) {
						LOGGER.error("IOException while trigerring unfollow user command ("+followingUser.getUsername()+")",e);
					}
					i++;
					if(i%50 ==0)
						sleep();
				}
				user = getUserDetails(IgUsername);
				followingPeopleCount = user.getUser().getFollowing_count();
			}else
				followingPeopleCount =0L;
							
		}
	}

	
}
