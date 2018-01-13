/**
 * 
 */
package com.socialfeed.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.concurrent.Future;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;

import com.socialapplibrary.database.SpringSQLConnection;

/**
 * @author Cameron
 *
 */
@SpringBootApplication
@EnableAsync
public class DataProvider {

	private static final String host = "localhost";
	private static final String port = "5432";
	private static final String dbUser = "postgres";
	private static final String dbPass = "SocialApp";
	
	private static String dbSchemaName = "social-app-schema";
	private static String dbName = "social-app-local";
	
	private static SpringSQLConnection sqlConnection = new SpringSQLConnection(host, port, dbName, dbUser, dbPass, dbSchemaName);
	

	/**
	 * Back to the future boys
	 * @param tableName
	 * @param limit
	 * @param id
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@Async
	public static Future<HashSet<String>> getSubscribedData(String tableName, int limit, String id) {
		ResultSet resultSet;
		
		HashSet<String> ids = new HashSet<String>();
		try
		{
			if (tableName.contains("Connection"))
			{
				resultSet = sqlConnection.getTableRowByColumnName(tableName, "user1", id);
				DataProvider.updateIdSet(resultSet, ids);
				
				resultSet = sqlConnection.getTableRowByColumnName(tableName, "user2", id);
				DataProvider.updateIdSet(resultSet, ids);

			}
			else
			{
				resultSet = sqlConnection.getTableRowByColumnName(tableName, "user_id", id);
				DataProvider.updateIdSet(resultSet, ids);
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		return new AsyncResult<HashSet<String>>(ids);
	}
	
	private static void updateIdSet(ResultSet resultSet, HashSet<String> ids) throws SQLException
	{
		while (resultSet.next())
		{
			ids.add(resultSet.getString(0));
		}
	}
}
