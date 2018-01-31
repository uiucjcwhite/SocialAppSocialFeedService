/**
 * 
 */
package com.socialfeed.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.joda.time.Instant;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.socialapplibrary.database.SQLConnection;
import com.socialapplibrary.endpoints.EndpointConstants;
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

	public static final String CONNECTION_TABLE = "Connection";
	public static final String EVENT_TABLE = "Event";
	public static final String EVENT_RELATION_TABLE = "Event Relation";
	public static final String GROUP_TABLE = "Group";
	public static final String GROUP_MEMBERSHIP_TABLE = "Group Membership";
	public static final String INTEREST_TABLE = "Interest";
	public static final String PROFILE_TABLE = "Profile";
	public static final String USER_TABLE = "User";
	
	public static final String USER_INTEREST_SUBSCRIPTION_TABLE = "User Interest Subscription";
	public static final String EVENT_INTEREST_SUBSCRIPTION_TABLE = "Event Interest Subscription";
	public static final String GROUP_INTEREST_SUBSCRIPTION_TABLE = "Group Interest Subscription";


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
			String urlName = EndpointConstants.HOST + ":";
			Gson g = new Gson();
			List<Map<String, String>> idList = null;
			URL url;
			HttpURLConnection conn;
			String data;
			switch(tableName)
			{
			case DataProvider.CONNECTION_TABLE:
				String getUserIds = urlName + EndpointConstants.CORE_PORT + "/" + EndpointConstants.USER_CONNECTION;
				url = new URL(getUserIds + String.format("?user1={0}&type=Friend", id));
				conn = (HttpURLConnection) url.openConnection();
				
				data = DataProvider.getJsonDataFromConnection(conn);
				conn.disconnect();
				
				idList = g.fromJson(data, new TypeToken<List<Entity>>(){}.getType());
				
				url = new URL(getUserIds + String.format("?user2={0}", id));
				conn = (HttpURLConnection) url.openConnection();
				data = DataProvider.getJsonDataFromConnection(conn);
				conn.disconnect();
				idList.addAll(g.fromJson(data, new TypeToken<List<Map<String, String>>>(){}.getType()));
				break;
				
			case DataProvider.EVENT_RELATION_TABLE:
				String getEventIds = urlName + EndpointConstants.CORE_PORT + "/" + EndpointConstants.EVENT_ATTENDANCE_RELATION +
					String.format("?user_id={0}&type=Accepted&type=Maybe", id);
				url = new URL(getEventIds);
				conn = (HttpURLConnection) url.openConnection();

				data = DataProvider.getJsonDataFromConnection(conn);
				
				conn.disconnect();
				break;
			
			case DataProvider.GROUP_MEMBERSHIP_TABLE:
				String getGroupIds = urlName + EndpointConstants.CORE_PORT + "/" + EndpointConstants.GROUP_MEMBERSHIP +
					String.format("?user_id={0}&type=Member", id);
				url = new URL(getGroupIds);
				conn = (HttpURLConnection) url.openConnection();
				data = DataProvider.getJsonDataFromConnection(conn);
				idList = (g.fromJson(data, new TypeToken<List<Map<String, String>>>(){}.getType()));
				conn.disconnect();
				break;
				
			case DataProvider.USER_INTEREST_SUBSCRIPTION_TABLE:
				String getInterests = urlName + EndpointConstants.INTERESTS_PORT + "/" + EndpointConstants.USER_INTEREST_SUBSCRIPTION +
					String.format("entityId={0}", id);
				url = new URL(getInterests);
				conn = (HttpURLConnection) url.openConnection();
				data = DataProvider.getJsonDataFromConnection(conn);
				idList = g.fromJson(data, new TypeToken<List<Map<String, String>>>(){}.getType());
				break;
			}
			
			ids = DataProvider.getIdSet(idList);
		}	
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		

		return new AsyncResult<HashSet<String>>(ids);
	}
	
	private static String getJsonDataFromConnection(HttpURLConnection conn) throws IOException
	{
		String finalData = "";
		String output;
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
		while ((output = br.readLine()) != null) {
			finalData += output;
		}
		
		return finalData;
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
	
	private static HashSet<String> getIdSet(List<Map<String, String>> idList) throws SQLException
	{
		HashSet<String> ids = new HashSet<String>();
		
		for (Map<String,String> currentObj: idList)
		{
			if (currentObj.containsKey("id"))
			{
				String interestID = currentObj.get("id");
				ids.add(interestID);
			}
		}
		
		return ids;
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
