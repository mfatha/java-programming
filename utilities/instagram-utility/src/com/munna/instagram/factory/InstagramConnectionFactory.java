package com.munna.instagram.factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.instagram.constants.InstaConstants;

public class InstagramConnectionFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InstagramConnectionFactory.class);
	
	private Map<String, Instagram4j> connectionMap = new HashMap<String, Instagram4j>();
	
	private InstagramConnectionFactory() {
	}
	
	private static InstagramConnectionFactory instagramConnectionFactory;
	
	public static InstagramConnectionFactory getInstance() {
		if (instagramConnectionFactory == null) {
			synchronized (InstagramConnectionFactory.class) {
				if (instagramConnectionFactory == null) {
					instagramConnectionFactory = new InstagramConnectionFactory();
				}
			}
		}
		return instagramConnectionFactory;
	}
	
	public void initializeConnection(File configFile) throws Exception {
		initializeConnection(InstaConstants.DEFAULT_IG_CONNECTION, configFile);
	}
	
	public void initializeConnection(String connectionName, File configFile) throws Exception {
		try (InputStream iStream = new FileInputStream(configFile)) {
			Properties properties = new Properties();
			properties.load(iStream);
			String mailId = properties.getProperty(InstaConstants.AuthenticationConstant.EMAIL);
			String password = properties.getProperty(InstaConstants.AuthenticationConstant.PASSWORD);
			InstagramConnectionFactory.getInstance().initializeConnection(connectionName, mailId, password);
		} catch (Exception e) {
			LOGGER.error("Error occured while initializinng the database connection for ".concat(connectionName), e);
			throw new Exception(e.getMessage(), e);
		}
	}

	private void initializeConnection(String connectionName, String mailId, String password) throws Exception{
		try {
			if (connectionMap.containsKey(connectionName)) {
				closeConnection(connectionName);
			}
			Instagram4j connection = Instagram4j.builder().username(mailId).password(password).build();
			connection.setup();
			connection.login();
			connectionMap.put(connectionName, connection);
		} catch (Exception e) {
			LOGGER.error("Error occurred while creating the connection", e);
			throw new Exception("Failed to establish conneection to the database", e);
		}
	}
	
	public Instagram4j getConnection() {
		return getConnection(InstaConstants.DEFAULT_IG_CONNECTION);
	}

	public Instagram4j getConnection(String connectionName) {
		if(connectionMap.containsKey(connectionName))
			return connectionMap.get(connectionName);
		else return null;
	}

	public void closeConnection() {
		closeConnection(InstaConstants.DEFAULT_IG_CONNECTION);
	}

	public void closeConnection(String connectionName) {
		try {
			connectionMap.remove(connectionName);
		} catch (Exception e) {
			LOGGER.error("Error occurred while closing the connection", e);
		}
	}


}
