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
import java.util.Random;

import org.apache.http.client.ClientProtocolException;
import org.brunocvcunha.instagram4j.requests.InstagramSearchTagsRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.InstagramTagFeedRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedItem;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchTagsResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.instagram.constants.InstaConstants;
import com.munna.instagram.factory.InstagramConnectionFactory;

/**
 * @author Mohammed Fathauddin
 * @since 2018
 */

public class InstaFollowingHanlder extends InstagramHandler{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InstaFollowingHanlder.class);
	
	String IgUsername = InstaConstants.AuthenticationConstant.IG_USERNAME;
	Boolean isFollowingEmpty = false;
	
	@Override
	public void init() {
		if(!InstaConstants.AuthenticationConstant.IG_STOP_PROCESS)
			do {
				Date now = new Date();
				SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
		        LOGGER.info("Its "+ simpleDateformat.format(now));
		        Calendar calendar = Calendar.getInstance();
		        calendar.setTime(now);
		        if(calendar.get(Calendar.DAY_OF_WEEK) == 7 && !isFollowingEmpty) {
		        	unFollowTheFollowingUsers();
		        } else {
		        	searchForNewUsersAndFollowThem();
		        }
			}while(!stopProcess());		
	}

	private void searchForNewUsersAndFollowThem(){
		LOGGER.info("Searching for new Users based on feeds.");
		long fixedCount = InstaConstants.FollowingConstants.FIXED_COUNT;
		long cc = 0L;
		String[] hashTag_arr = InstaConstants.FollowingConstants.HASH_TAGS.split(",");
		String randomTag = hashTag_arr[new Random().nextInt(hashTag_arr.length)];
		InstagramFeedResult tagFeed = null;
		try {
			LOGGER.info("Searching feeds under : #"+randomTag);
			tagFeed = InstagramConnectionFactory.getInstance().getConnection()
					.sendRequest(new InstagramTagFeedRequest(randomTag));
		}	
		catch (ClientProtocolException e) {
			LOGGER.error("ClientProtocolException while getting random tag feeds",e);
		} catch (IOException e) {
			LOGGER.error("IOException while getting random tag feeds",e);
		}
		LOGGER.info("Number of following count  on " + new Date() + " :"
				+ getUserDetails(IgUsername).getUser().getFollowing_count());
		if(tagFeed != null) {
			for (InstagramFeedItem feedResult : tagFeed.getItems()) {
				try {
					if (cc != fixedCount) {
						LOGGER.info("Post ID : " + feedResult.getPk() + "posted by : " + feedResult.getUser().getUsername());
						followUser(feedResult.getUser().getPk());
						LOGGER.info("Following user : " + feedResult.getUser().getUsername());
						cc++;
						LOGGER.info("User Count in loop: " + cc);
					} else {
						break;
					}
					if (cc % 10 == 0) {
						sleep();
					}
				} catch (ClientProtocolException e) {
					LOGGER.error("ClientProtocolException while trigerring unfollow user command ("
							+ feedResult.getUser().getUsername() + ")", e);
				} catch (IOException e) {
					LOGGER.error("IOException while trigerring unfollow user command (" + feedResult.getUser().getUsername()
							+ ")", e);
				}
			}
		}
	}
	

	private void unFollowTheFollowingUsers() {
		InstagramSearchUsernameResult user = getUserDetails(IgUsername);
		LOGGER.info("Number of followers for("+IgUsername+"): " + user.getUser().getFollower_count());
		LOGGER.info("Number of following for("+IgUsername+"): " + user.getUser().getFollowing_count());
		long followingPeopleCount = user.getUser().getFollowing_count();
		long i = 1L;
		if(followingPeopleCount != 0) {
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
		}else {
			LOGGER.info("Following list is empty");
			isFollowingEmpty = true;
		}
		
	}

	
}
