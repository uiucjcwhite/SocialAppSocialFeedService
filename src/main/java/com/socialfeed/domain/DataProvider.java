/**
 * 
 */
package com.socialfeed.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Future;

import org.joda.time.Instant;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;

import com.socialapplibrary.database.SQLConnection;
import com.socialapplibrary.utility.GeoUtility;
import com.socialapplibrary.utility.GeoUtility.Location;
import com.socialapplibrary.utility.Utility;
import com.socialfeed.domain.entity.Entity;
import com.socialfeed.domain.entity.Event;
import com.socialfeed.domain.entity.Group;
import com.socialfeed.domain.entity.User;

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
	
	private static SQLConnection sqlConnection = new SQLConnection(host, port, dbName, dbUser, dbPass, dbSchemaName);
	
	public static final String CONNECTION_TABLE = "\"Connection\"";
	public static final String EVENT_TABLE = "\"Event\"";
	public static final String EVENT_RELATION_TABLE = "\"Event Relation\"";
	public static final String GROUP_TABLE = "\"Group\"";
	public static final String GROUP_MEMBERSHIP_TABLE = "\"Group Membership\"";
	public static final String INTEREST_TABLE = "\"Interest\"";
	public static final String PROFILE_TABLE = "\"Profile\"";
	public static final String USER_TABLE = "\"User\"";
	
	public static final String USER_INTEREST_SUBSCRIPTION_TABLE = "\"User Interest Subscription\"";
	public static final String EVENT_INTEREST_SUBSCRIPTION_TABLE = "\"Event Interest Subscription\"";
	public static final String GROUP_INTEREST_SUBSCRIPTION_TABLE = "\"Group Interest Subscription\"";


	/**
	 * @param tableName
	 * @param limit
	 * @param id
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@Async
	public static Future<HashSet<String>> getSubscribedIds(String tableName, int limit, String id) {
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
			
			while (resultSet.next())
			{
				ids.add(resultSet.getString("id"));
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		

		return new AsyncResult<HashSet<String>>(ids);
	}
	
	/**
	 * @param tableName
	 * @param limit
	 * @param id
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@Async
	public static Future<HashSet<Entity>> getSubscribedData(String tableName, HashSet<String> ids) {
		ResultSet resultSet;
		HashSet<Entity> entities;
		try
		{
			ArrayList<String> queryIds = new ArrayList<String>();
			queryIds.addAll(ids);
			resultSet = sqlConnection.getTableRowsByListSearch(tableName, "id", queryIds);
			entities = DataProvider.buildEntitiesFromResultSet(resultSet, tableName);
			return new AsyncResult<HashSet<Entity>>(entities);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		return new AsyncResult<HashSet<Entity>>(new HashSet<Entity>());
	}
	
	private static void updateIdSet(ResultSet resultSet, HashSet<String> ids) throws SQLException
	{
		while (resultSet.next())
		{
			ids.add(resultSet.getString(0));
		}
	}
	
	private static HashSet<Entity> buildEntitiesFromResultSet(ResultSet resultSet, String tableName) throws SQLException
	{
		HashSet<Entity> entities = new HashSet<Entity>();
		while(resultSet.next())
		{
			String id = resultSet.getString("id");
			Location exactLocation;
			
			switch (tableName)
			{
			case DataProvider.PROFILE_TABLE:
				entities.add(new User(id, User.Gender.valueOf(resultSet.getString("gender"))));
				break;
			case DataProvider.EVENT_TABLE:
				Instant eventStartDate = Utility.parseSQLTimestampToInstant(resultSet.getTimestamp("start_date").toString());
				exactLocation = GeoUtility.Location.parseLocation(resultSet.getString("exact_location"));
				entities.add(new Event(id, eventStartDate, exactLocation));
				break;
			case DataProvider.GROUP_TABLE:
				exactLocation = GeoUtility.Location.parseLocation(resultSet.getString("exact_location"));
				entities.add(new Group(id, exactLocation));
				break;
			}
		}
		
		return entities;
	}
}
