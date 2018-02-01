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

	public static final String CONNECTION = "Connection";
	public static final String EVENT = "Event";
	public static final String EVENT_RELATION = "Event Relation";
	public static final String GROUP = "Group";
	public static final String GROUP_MEMBERSHIP = "Group Membership";
	public static final String INTEREST = "Interest";
	public static final String PROFILE = "Profile";
	public static final String USER = "User";
	
	public static final String USER_INTEREST_SUBSCRIPTION = "User Interest Subscription";
	public static final String EVENT_INTEREST_SUBSCRIPTION = "Event Interest Subscription";
	public static final String GROUP_INTEREST_SUBSCRIPTION = "Group Interest Subscription";


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
			HttpURLConnection conn = null;
			String data;
			switch(tableName)
			{
			case DataProvider.CONNECTION:
				String getUserIds = urlName + EndpointConstants.CORE_PORT + "/" + EndpointConstants.USER_CONNECTION;
				url = new URL(getUserIds + String.format("?user1={0}&type=Friend", id));
				conn = (HttpURLConnection) url.openConnection();
				
				data = DataProvider.getJsonDataFromConnection(conn);
				conn.disconnect();
				
				idList = g.fromJson(data, new TypeToken<List<Entity>>(){}.getType());
				
				url = new URL(getUserIds + String.format("?user2={0}", id));
				conn = (HttpURLConnection) url.openConnection();
				data = DataProvider.getJsonDataFromConnection(conn);
				idList.addAll(g.fromJson(data, new TypeToken<List<Map<String, String>>>(){}.getType()));
				break;
				
			case DataProvider.EVENT_RELATION:
				String getEventIds = urlName + EndpointConstants.CORE_PORT + "/" + EndpointConstants.EVENT_ATTENDANCE_RELATION +
					String.format("?user_id={0}&type=Accepted&type=Maybe", id);
				url = new URL(getEventIds);
				conn = (HttpURLConnection) url.openConnection();

				data = DataProvider.getJsonDataFromConnection(conn);
				idList = g.fromJson(data, new TypeToken<List<Map<String, String>>>(){}.getType());

				break;

			case DataProvider.GROUP_MEMBERSHIP:
				String getGroupIds = urlName + EndpointConstants.CORE_PORT + "/" + EndpointConstants.GROUP_MEMBERSHIP +
					String.format("?user_id={0}&type=Member", id);
				url = new URL(getGroupIds);
				conn = (HttpURLConnection) url.openConnection();
				data = DataProvider.getJsonDataFromConnection(conn);
				idList = g.fromJson(data, new TypeToken<List<Map<String, String>>>(){}.getType());
				break;

			case DataProvider.USER_INTEREST_SUBSCRIPTION:
				String getInterests = urlName + EndpointConstants.INTERESTS_PORT + "/" + EndpointConstants.USER_INTEREST_SUBSCRIPTION +
					String.format("entityId={0}", id);
				url = new URL(getInterests);
				conn = (HttpURLConnection) url.openConnection();
				data = DataProvider.getJsonDataFromConnection(conn);
				idList = g.fromJson(data, new TypeToken<List<Map<String, String>>>(){}.getType());
				break;
			}

			conn.disconnect();
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
	public static Future<HashSet<Entity>> getSubscribedData(String endpoint, HashSet<String> ids) {
		ResultSet resultSet;
		HashSet<Entity> entities;
		try
		{
			String idQuery = "?id=" + String.join("&id=", ids);
			String urlName = EndpointConstants.HOST + ":" + EndpointConstants.CORE_PORT;
			Gson g = new Gson();
			List<Map<String, String>> jsonEntities = null;
			URL url;
			HttpURLConnection conn;
			String data = null;
			switch (endpoint)
			{
			case DataProvider.EVENT:
				String getEvents = urlName + EndpointConstants.EVENT + idQuery;
				url = new URL(getEvents);
				conn = (HttpURLConnection) url.openConnection();
				
				data = DataProvider.getJsonDataFromConnection(conn);
				conn.disconnect();
				break;
			case DataProvider.GROUP:
				String getGroups = urlName + EndpointConstants.GROUP + idQuery;
				url = new URL(getGroups);
				conn = (HttpURLConnection) url.openConnection();
				
				data = DataProvider.getJsonDataFromConnection(conn);
				conn.disconnect();
				break;
			
			case DataProvider.PROFILE:
				//TODO: Figure out what we're doing with the friends
				return new AsyncResult<HashSet<Entity>>(new HashSet<Entity>());
			}

			ArrayList<Map<String, String>> entityList = g.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>(){}.getType());
			entities = DataProvider.buildEntitiesFromResultSet(entityList, endpoint);
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
	
	private static HashSet<Entity> buildEntitiesFromResultSet(List<Map<String, String>> entityData, String tableName) throws SQLException
	{
		HashSet<Entity> entities = new HashSet<Entity>();
		for (Map<String, String> entity: entityData)
		{
			String id = entity.get("id");
			Location exactLocation;
			
			switch (tableName)
			{
			case DataProvider.PROFILE:
				entities.add(new User(id, User.Gender.valueOf(entity.get("gender"))));
				break;
			case DataProvider.EVENT:
				Instant eventStartDate = Instant.parse(entity.get("start_date"));
				exactLocation = GeoUtility.Location.parseLocation(entity.get("exact_location"));
				entities.add(new Event(id, eventStartDate, exactLocation));
				break;
			case DataProvider.GROUP:
				exactLocation = GeoUtility.Location.parseLocation(entity.get("exact_location"));
				entities.add(new Group(id, exactLocation));
				break;
			}
		}
		
		return entities;
	}
}
