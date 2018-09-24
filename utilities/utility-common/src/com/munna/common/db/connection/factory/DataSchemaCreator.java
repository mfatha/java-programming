package com.munna.common.db.connection.factory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.common.util.Util;

public class DataSchemaCreator {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataSchemaCreator.class);

	private Connection connection = null;

	private String tableName;

	public DataSchemaCreator() {
		this.connection = DatabaseConnectionFactory.getInstance().getConnection();
	}

	public DataSchemaCreator(Connection connection) {
		this.connection = connection;
	}

	public DataSchemaCreator(Connection connection, String tableName) {
		this.connection = connection;
		this.tableName = tableName;
	}

	public Boolean isTableExist(String tableName) {
		if (this.connection != null)
			return isTableExist(this.connection, tableName);
		LOGGER.error("Database Connection is not Established");
		return false;
	}

	public Boolean isTableExist(Connection connection, String tableName) {
		if (connection != null && !Util.isEmpty(tableName)) {
			ResultSet tables;
			try {
				DatabaseMetaData dbm = connection.getMetaData();
				tables = dbm.getTables(null, null, tableName, null);
				if (tables.next()) {
					return true;
				} else {
					LOGGER.info(tableName + " Table does not exist.");
					return false;
				}
			} catch (SQLException e) {
				LOGGER.error("Error while closing the statement...", e);
			}
		}
		return false;
	}

	public void createTable(String tableQuery) throws Exception {
		try {
			createTable(connection, tableQuery);
		} catch (Exception e) {
			LOGGER.error("Database Connection is not Established");
			throw new Exception(e != null ? e.getMessage() : "Unable to create Table with Query : " + tableQuery, e);
		}
	}

	public void createTable(Connection connection, String tableQuery) throws Exception {
		if (connection != null && !Util.isEmpty(tableQuery)) {
			LOGGER.debug("creating table with Query : " + tableQuery);
			Statement statement = null;
			try {
				statement = connection.createStatement();
				statement.executeUpdate(tableQuery);
			} catch (SQLException e) {
				LOGGER.error("Error while creating table..", e);
				throw new Exception(e != null ? e.getMessage() : "Error while creating table..", e);
			} finally {
				LOGGER.info("Table created...");
				try {
					statement.close();
				} catch (SQLException e) {
					LOGGER.error("Error while closing the statement...", e);
					throw new Exception(e != null ? e.getMessage() : "Error while closing the statement...", e);
				}
			}
		}
	}
	
	public Boolean isColumnFamilyExist(String tableName, Map<String,String> columnDataSchema) throws Exception{
		//TODO column exist
		return false;
	}
	
	public void addColumnFamily(String tableName, Map<String,String> columnDataSchema) throws Exception{
		//TODO Add column
		if(isColumnFamilyExist(tableName, columnDataSchema)) {
			
		}
	}
	
	public void updateColumnFamily(String tableName, Map<String,String> columnDataSchema) {
		//TODOUpdate Column
	}
}
