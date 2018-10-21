package com.munna.instagram.factory;

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
import org.brunocvcunha.instagram4j.requests.InstagramLikeRequest;
import org.brunocvcunha.instagram4j.requests.InstagramPostCommentRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.InstagramTagFeedRequest;
import org.brunocvcunha.instagram4j.requests.InstagramUnfollowRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.instagram.constants.InstaConstants;

 public class InstagramManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InstagramManager.class);

	public static InstagramSearchUsernameResult getUserDetails(String username) {
		synchronized (InstagramManager.class) {
			try {
				return InstagramConnectionFactory.getInstance().getConnection().sendRequest(new InstagramSearchUsernameRequest(username));
			} catch (ClientProtocolException e) {
				LOGGER.error("ClientProtocol Error. ",e);	
			} catch (IOException e) {
				LOGGER.error("IOException Error. ",e);	
			}
			return null;
		}
	}
	
	public static InstagramGetUserFollowersResult getFollowingUser(long igId) {
		synchronized (InstagramManager.class) {
			try {
				return InstagramConnectionFactory.getInstance().getConnection().sendRequest(new InstagramGetUserFollowingRequest(igId));
			} catch (ClientProtocolException e) {
				LOGGER.error("ClientProtocol Error. ",e);	
			} catch (IOException e) {
				LOGGER.error("IOException Error. ",e);	
			}
			return null;
		}
	} 
	
	public static InstagramGetUserFollowersResult getFollowerUser(long igId, String maxId) {
		synchronized (InstagramManager.class) {
			try {
				if(maxId == null) {
					return InstagramConnectionFactory.getInstance().getConnection().sendRequest(new InstagramGetUserFollowersRequest(igId));
				}
				return InstagramConnectionFactory.getInstance().getConnection().sendRequest(new InstagramGetUserFollowersRequest(igId, maxId));
			} catch (ClientProtocolException e) {
				LOGGER.error("ClientProtocol Error. ",e);	
			} catch (IOException e) {
				LOGGER.error("IOException Error. ",e);	
			}
			return null;
		}
	} 
	
	public static void unFollowUser(long igId) throws ClientProtocolException, IOException {
		synchronized (InstagramManager.class) {
			InstagramConnectionFactory.getInstance().getConnection().sendRequest(new InstagramUnfollowRequest(igId));
		}		
	}
	
	public static void followUser(long igId) throws ClientProtocolException, IOException {
		synchronized (InstagramManager.class) {
			InstagramConnectionFactory.getInstance().getConnection().sendRequest(new InstagramFollowRequest(igId));
		}
	}
	
	public static Map<String,List<String>> getFollowers(String igUsername){
		synchronized (InstagramManager.class) {
			InstagramSearchUsernameResult user = getUserDetails(igUsername);
			long followerUserAdded =0L;
			LOGGER.info("Number of followers for("+igUsername+"): " + user.getUser().getFollower_count());
			Map<String,List<String>> followersMap = new HashMap<String,List<String>>();
			String maxId = null;
			do {
				InstagramGetUserFollowersResult userFollowingList = getFollowerUser(user.getUser().getPk(), maxId);
				if(userFollowingList.getUsers() != null) {
					List<InstagramUserSummary> followersList = userFollowingList.getUsers();
					maxId = userFollowingList.getNext_max_id();
					if(followersList != null && followersList.size() !=0) {
						for(InstagramUserSummary followerUser : followersList) {
							String hashCode = String.valueOf((followerUser.getUsername().hashCode())% 1000);
							if(followersMap.containsKey(hashCode)) {
								if(!followersMap.get(hashCode).contains(followerUser.getUsername())) {
									followersMap.get(hashCode).add(followerUser.getUsername());
									followerUserAdded++;
								}
							}else {
								List<String> userList = new ArrayList<>();
								userList.add(followerUser.getUsername());
								followersMap.put(hashCode, userList);
								followerUserAdded++;
							}
						}
					}
				}else {
					LOGGER.error("Error with getting Followers list , where maxId = "+maxId+" : "+ userFollowingList);
					maxId = null;			
				}
				if(followerUserAdded == user.getUser().getFollower_count())
					maxId = null;
			}while(maxId!= null);
			LOGGER.info("Got all Followers List, Count : "+ followerUserAdded);
			return followersMap;			
		}
	}
	
	public static void message(List<String> users){
		message(users,InstaConstants.DEFAULT_MESSAGE);
	}
	
	public static void message(List<String> users,String message) {
		synchronized (InstagramManager.class) {
			try {
				InstagramConnectionFactory.getInstance().getConnection().sendRequest(InstagramDirectShareRequest.builder(ShareType.MESSAGE, users).message(message).build());
			} catch (IOException e) {
				LOGGER.error("Error while sending message to users ",e);
			}finally {
				LOGGER.info("MESSAGE sent Successfully.");
			}
		}
	}

	public static InstagramFeedResult getTagFeeds(String randomTag) {
		synchronized (InstagramManager.class) {
			try {
				return InstagramConnectionFactory.getInstance().getConnection()
				.sendRequest(new InstagramTagFeedRequest(randomTag));
			} catch (ClientProtocolException e) {
				LOGGER.error("Client Protocol error while getting feeds",e);
			} catch (IOException e) {
				LOGGER.error("IO error while getting feeds",e);
			}
			return null;
		}
	}

	public static void likePost(long postId) {
		synchronized (InstagramManager.class) {
			try {
				InstagramConnectionFactory.getInstance().getConnection().sendRequest(new InstagramLikeRequest(postId));
			}  catch (ClientProtocolException e) {
				LOGGER.error("Client Protocol error while liking feeds",e);
			} catch (IOException e) {
				LOGGER.error("IO error while liking feeds",e);
			} finally{
				LOGGER.info("Liked successfully..");
			}
		}
	}

	public static void commentPost(long postId) {
		commentPost(postId,InstaConstants.DEFAULT_COMMENT);
	}

	private static void commentPost(long postId, String commentMessage) {
		synchronized (InstagramManager.class) {
			try {
				InstagramConnectionFactory.getInstance().getConnection().sendRequest(new InstagramPostCommentRequest(postId,commentMessage));
			}  catch (ClientProtocolException e) {
				LOGGER.error("Client Protocol error while commenting feeds",e);
			} catch (IOException e) {
				LOGGER.error("IO error while commenting feeds",e);
			} finally{
				LOGGER.info("Commented successfully..");
			}
		}
	}
	
	public static boolean stopProcess() {
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

}
