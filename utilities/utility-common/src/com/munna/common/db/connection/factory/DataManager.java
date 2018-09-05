package com.munna.common.db.connection.factory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munna.common.util.Util;

public class DataManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataManager.class);
	
	Connection connection = DatabaseConnectionFactory.getInstance().getConnection();

	public Boolean isTableExist(String tableName){
		if(connection!= null && !Util.isEmpty(tableName)) {
			ResultSet tables;
			try {
				DatabaseMetaData dbm = connection.getMetaData();
				tables = dbm.getTables(null, null, tableName, null);
				if (tables.next()) {
					return true;
				}else {
					return false;
				}
			} catch (SQLException e) {
				LOGGER.error("Error while closing the statement...",e);
			}			
		}		
		return false;
	}
	
	public void createTable(String query) {
		if(connection!= null && !Util.isEmpty(query)) {
			LOGGER.debug("creating table with Query : " + query);
			Statement statement = null;
			try {
				statement =  connection.createStatement();
				statement.executeUpdate(query);
			} catch (SQLException e) {
				LOGGER.error("Error while creating table..",e);
			} finally {
				LOGGER.info("Table created...");
				try {
					statement.close();
				} catch (SQLException e) {
					LOGGER.error("Error while closing the statement...",e);
				}
				
			}
		}
	}
	
	public Boolean isColumnExist(){
		return false;
	}
	
	public void addCloumn() {
		
	}
	
	public Boolean isDataExist() {
		return false;
	}
	
}
