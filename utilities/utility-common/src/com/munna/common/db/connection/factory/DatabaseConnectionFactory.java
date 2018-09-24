package com.munna.common.db.connection.factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.common.cache.UtilityConstants;

/**
 * @author Mohammed Fathauddin
 * @since 2018
 */

public class DatabaseConnectionFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnectionFactory.class);
	
	private DatabaseConnectionFactory() {
		
	}
	
	private static DatabaseConnectionFactory databaseConnectionFactory;
	
	private Map<String, Connection> connectionMap = new HashMap<String, Connection>();
	
	public static DatabaseConnectionFactory getInstance() {
		if (databaseConnectionFactory == null) {
			synchronized (DatabaseConnectionFactory.class) {
				if (databaseConnectionFactory == null) {
					databaseConnectionFactory = new DatabaseConnectionFactory();
				}
			}
		}
		return databaseConnectionFactory;
	}

	public void initializeConnection(File configFile) throws Exception {
		initializeConnection(UtilityConstants.DataBaseConstant.DEFAULT_DB_CONNECTION, configFile);
	}
	
	public void initializeConnection(String connectionName, File configFile) throws Exception {
		try (InputStream iStream = new FileInputStream(configFile)) {
			Properties properties = new Properties();
			properties.load(iStream);
			String driver = properties.getProperty("DRIVER");
			String url = properties.getProperty("CONNECTION_URL");
			String dbUserName = properties.getProperty("USERNAME");
			String dbPassword = properties.getProperty("PASSWORD");
			DatabaseConnectionFactory.getInstance().initializeConnection(connectionName, driver, url, dbUserName, dbPassword);
		} catch (Exception e) {
			LOGGER.error("Error occured while initializinng the database connection for ".concat(connectionName), e);
			throw new Exception(e.getMessage(), e);
		}
	}
	
	public void initializeConnection(String connectionName, String driver, String url, String dbUserName, String dbPassword) throws Exception {
		try {
			if (connectionMap.containsKey(connectionName)) {
				closeConnection(connectionName);
			}
			Connection connection = init(driver, url, dbUserName, dbPassword);
			if (connection != null) {
				DatabaseMetaData dm = connection.getMetaData();
				LOGGER.info("Driver Information");
				LOGGER.info("Driver Name: " + dm.getDriverName());
				LOGGER.info("Driver Version: " + dm.getDriverVersion());
				LOGGER.info("Database Information ");
				LOGGER.info("Database Name: " + dm.getDatabaseProductName());
				LOGGER.info("Database Version: " + dm.getDatabaseProductVersion());
/*				LOGGER.info("Avalilable Catalogs ");
				ResultSet rs = dm.getCatalogs();
				while (rs.next()) {
					LOGGER.info("catalog: " + rs.getString(1));
				}
				DBUtil.releaseResources(rs);*/
			}
			connectionMap.put(connectionName, connection);
		} catch (Exception e) {
			LOGGER.error("Error occurred while creating the connection", e);
			throw new Exception("Failed to establish conneection to the database", e);
		}
	}
	
	private Connection init(String driver, String url, String dbUserName, String dbPassword) throws Exception {
		Connection connection;
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url, dbUserName, dbPassword);
			LOGGER.info("Database connection created successfully.. ");
			return connection;
		} catch (Exception e) {
			LOGGER.error("Database connection failure..", e);
			throw e;
		}
	}

	public Connection getConnection() {
		return getConnection(UtilityConstants.DataBaseConstant.DEFAULT_DB_CONNECTION);
	}

	public Connection getConnection(String connectionName) {
		return connectionMap.get(connectionName);
	}

	public void closeConnection() {
		closeConnection(UtilityConstants.DataBaseConstant.DEFAULT_DB_CONNECTION);
	}

	public void closeConnection(String connectionName) {
		try {
			connectionMap.get(connectionName).close();
			connectionMap.remove(connectionName);
		} catch (SQLException e) {
			LOGGER.error("Error occurred while closing the connection", e);
		}
	}
}
