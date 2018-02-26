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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.joda.time.Instant;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;

import com.controller.models.entities.Entity;
import com.controller.models.entities.EntityPost;
import com.controller.models.entities.Event;
import com.controller.models.entities.Group;
import com.controller.models.entities.Profile;
import com.controller.models.entities.enums.RecurringPeriodEnum;
import com.controller.models.relationships.EventAttendanceRelation;
import com.controller.models.relationships.GroupMembership;
import com.controller.models.relationships.UserConnection;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.socialapplibrary.core.entity.BaseDatabaseObject;
import com.socialapplibrary.core.entity.enums.InvitePolicyEnum;
import com.socialapplibrary.core.entity.enums.ViewPrivacy;
import com.socialapplibrary.database.SQLConnection;
import com.socialapplibrary.endpoints.EndpointConstants;
import com.socialapplibrary.utility.GeoUtility;
import com.socialapplibrary.utility.GeoUtility.Location;
import com.socialapplibrary.utility.Utility;


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
	public static final String ENTITY_POST = "Entity Post";
	
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
			//List<BaseDatabaseObject> relationships = null;
			URL url = null;
			HttpURLConnection conn = null;
			String data;
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JodaModule());
			TypeFactory typeFactory = objectMapper.getTypeFactory();	
			switch(tableName)
			{
			case DataProvider.CONNECTION:
				String getUserIds = urlName + EndpointConstants.CORE_PORT  + EndpointConstants.USER_CONNECTION;
				url = new URL(getUserIds + String.format("?user1=%s&type=Friend", id));
				conn = (HttpURLConnection) url.openConnection();
				
				data = DataProvider.getJsonDataFromConnection(conn);
				conn.disconnect();
				System.out.println(data);
				//ArrayList<UserConnection> userConnections = g.fromJson(data, new TypeToken<ArrayList<UserConnection>>(){}.getType());

				ArrayList<UserConnection> userConnections = objectMapper.readValue(data,  typeFactory.constructCollectionType(ArrayList.class, UserConnection.class));
				for (UserConnection userConnection: userConnections)
				{
					System.out.println("User Id " + userConnection.getId());
					System.out.println(String.format("Adding User2 %s", userConnection.getUser2()));
					ids.add(userConnection.getUser2());
				}
				
//				for (UserConnection userConnection: jacksonUserConnections)
//				{
//					System.out.println(String.format("Jackson Adding User2 %s", userConnection.getUser2()));
//					//ids.add(userConnection.getUser2());
//				}
				
				url = new URL(getUserIds + String.format("?user2=%s&type=Friend", id));
				conn = (HttpURLConnection) url.openConnection();
				data = DataProvider.getJsonDataFromConnection(conn);
				//userConnections = g.fromJson(data, new TypeToken<List<UserConnection>>(){}.getType());
				userConnections = objectMapper.readValue(data,  typeFactory.constructCollectionType(ArrayList.class, UserConnection.class));
				for (UserConnection userConnection: userConnections)
				{
					System.out.println("User 2 Id " + userConnection.getId());
					System.out.println(String.format("Adding User1 %s", userConnection.getUser1()));
					ids.add(userConnection.getUser1());
				}
				break;
				
			case DataProvider.EVENT_RELATION:
				String getEventIds = urlName + EndpointConstants.CORE_PORT + EndpointConstants.EVENT_ATTENDANCE_RELATION +
					String.format("?user_id=%s&type=Accepted&type=Maybe", id);
				url = new URL(getEventIds);
				conn = (HttpURLConnection) url.openConnection();

				data = DataProvider.getJsonDataFromConnection(conn);
				//List<EventAttendanceRelation> eventAttendanceRelations = g.fromJson(data, new TypeToken<List<EventAttendanceRelation>>(){}.getType());
				ArrayList<EventAttendanceRelation> eventAttendanceRelations = objectMapper.readValue(data,  typeFactory.constructCollectionType(ArrayList.class, EventAttendanceRelation.class));
				for (EventAttendanceRelation eventAttendanceRelation: eventAttendanceRelations) {
					System.out.println(String.format("Adding Event %s", eventAttendanceRelation.getEventId()));
					ids.add(eventAttendanceRelation.getEventId());
				}
				break;

			case DataProvider.GROUP_MEMBERSHIP:
				String getGroupIds = urlName + EndpointConstants.CORE_PORT + EndpointConstants.GROUP_MEMBERSHIP +
					String.format("?user_id=%s&type=Member", id);
				url = new URL(getGroupIds);
				conn = (HttpURLConnection) url.openConnection();
				data = DataProvider.getJsonDataFromConnection(conn);
				//List<GroupMembership> groupMemberships = g.fromJson(data, new TypeToken<List<GroupMembership>>(){}.getType());
				ArrayList<GroupMembership> groupMemberships = objectMapper.readValue(data,  typeFactory.constructCollectionType(ArrayList.class, GroupMembership.class));
				for (GroupMembership groupMembership: groupMemberships) {
					System.out.println(String.format("Adding Group %s", groupMembership.getGroup()));
					ids.add(groupMembership.getGroup());
				}
				break;

			case DataProvider.USER_INTEREST_SUBSCRIPTION: //TODO: Figure out if we are importing Interest bean into the feed or if it's going to go in core with the other beans.
				String getInterests = urlName + EndpointConstants.INTERESTS_PORT + EndpointConstants.USER_INTEREST_SUBSCRIPTION +
					String.format("?entityId=%s", id);
				url = new URL(getInterests);
				conn = (HttpURLConnection) url.openConnection();
				data = DataProvider.getJsonDataFromConnection(conn);
				// = g.fromJson(data, new TypeToken<List<Map<String, String>>>(){}.getType());
				break;
			}

			if (url != null)
			{
				System.out.println(url.toString());
			}
			
			conn.disconnect();
		}	
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return new AsyncResult<HashSet<String>>(ids);
	}

	public static String getJsonDataFromConnection(HttpURLConnection conn) throws IOException
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
		System.out.println(String.format("Getting subscribed %s data", endpoint));
		ResultSet resultSet;
		HashSet<Entity> entities = new HashSet<Entity>();
		try
		{
			String idQuery = "?id=" + String.join("&id=", ids);
			String urlName = EndpointConstants.HOST + ":" + EndpointConstants.CORE_PORT;
			Gson g = new Gson();
			ArrayList<Entity> entityList = null;
			URL url = null;
			HttpURLConnection conn;
			String data = null;
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JodaModule());
			TypeFactory typeFactory = objectMapper.getTypeFactory();	
			switch (endpoint)
			{
			case DataProvider.EVENT:
				String getEvents = urlName + EndpointConstants.EVENT + idQuery;
				url = new URL(getEvents);
				conn = (HttpURLConnection) url.openConnection();
				
				data = DataProvider.getJsonDataFromConnection(conn);
				entityList = objectMapper.readValue(data,  typeFactory.constructCollectionType(ArrayList.class, Event.class));
				conn.disconnect();
				break;
			case DataProvider.GROUP:
				String getGroups = urlName + EndpointConstants.GROUP + idQuery;
				url = new URL(getGroups);
				conn = (HttpURLConnection) url.openConnection();
				
				data = DataProvider.getJsonDataFromConnection(conn);
				entityList = objectMapper.readValue(data,  typeFactory.constructCollectionType(ArrayList.class, Group.class));
				conn.disconnect();
				break;
			
			case DataProvider.PROFILE:
				//TODO: Figure out what we're doing with the friends
				return new AsyncResult<HashSet<Entity>>(entities);
				
			default:
				System.out.println(String.format("No DataProvider found for given provider: %s", endpoint));
			}
			
			if (url != null)
			{
				System.out.println(url.toString());
			}
			
			//ArrayList<Map<String, String>> entityList = g.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>(){}.getType());
			//entities = DataProvider.buildEntitiesFromJson(entityList, endpoint);
			if (entityList != null)
			{
				entities.addAll(entityList);
			}

			return new AsyncResult<HashSet<Entity>>(entities);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return new AsyncResult<HashSet<Entity>>(new HashSet<Entity>());
	}
	
	private static Entity buildEntityFromJsonMap(Map<String, String> entity, String endpoint) throws ParseException
	{
		Entity returnEntity = null;
		String id = BaseDatabaseObject.ID;
		switch (endpoint)
		{
		case DataProvider.PROFILE:
			returnEntity = new Profile(
					id,
					entity.get(Profile.USER),
					entity.get(Profile.FIRST_NAME),
					entity.get(Profile.LAST_NAME),
					Profile.Gender.valueOf(entity.get(Profile.GENDER)),
					entity.get(Profile.CITY),
					entity.get(Profile.STATE),
					entity.get(Profile.COUNTRY),
					Profile.Type.valueOf(entity.get(Profile.TYPE)),
					entity.get(Profile.EMAIL),
					Instant.parse(entity.get(BaseDatabaseObject.CREATEDDATE)));
			break;
		case DataProvider.EVENT:
			returnEntity = new Event(
					id,
					entity.get(Event.NAME),
					entity.get(Event.OWNER),
					Instant.parse(entity.get(Event.START_DATE)),
					Instant.parse(entity.get(Event.END_DATE)),
					entity.get(Event.ADDRESS),
					GeoUtility.Location.parseLocation(entity.get(Event.LOCATION)),
					entity.get(Event.DESCRIPTION),
					Integer.parseInt(entity.get(Event.MAX_ATTENDEES)),
					InvitePolicyEnum.valueOf(entity.get(Event.INVITE_POLICY)),
					Boolean.parseBoolean(entity.get(Event.RECURRING)),
					Instant.parse(entity.get(Event.NEXT_EVENT_START_DATE)),
					Instant.parse(entity.get(Event.NEXT_EVENT_END_DATE)),
					RecurringPeriodEnum.valueOf(entity.get(Event.RECURRING_PERIOD)),
					ViewPrivacy.valueOf(entity.get(Event.VIEW_PRIVACY)),
					Instant.parse(entity.get(BaseDatabaseObject.CREATEDDATE)));
			break;
		case DataProvider.GROUP:
			returnEntity = new Group(
					id,
					entity.get(Group.NAME),
					entity.get(Group.DESCRIPTION),
					InvitePolicyEnum.valueOf(entity.get(Group.INVITE_POLICY)),
					entity.get(Group.CITY),
					entity.get(Group.STATE),
					GeoUtility.Location.parseLocation(entity.get("exact_location")),
					ViewPrivacy.valueOf(entity.get(Group.VIEW_PRIVACY)),
					Double.parseDouble(entity.get(Group.MEMBERSHIP_COST)),
					entity.get(Group.ORGANIZATION),
					Instant.parse(entity.get(BaseDatabaseObject.CREATEDDATE)));
			break;
		case DataProvider.ENTITY_POST:
			returnEntity = new EntityPost(
					id,
					entity.get(EntityPost.PARENT),
					entity.get(EntityPost.ENTITY),
					entity.get(EntityPost.CONTENT),
					entity.get(EntityPost.POSTER),
					Instant.parse(entity.get(BaseDatabaseObject.CREATEDDATE)));
		}
		
		return returnEntity;
	}
	
	public static HashSet<Entity> buildEntitiesFromJson(List<Map<String, String>> entityData, String endpoint) throws SQLException, ParseException
	{
		HashSet<Entity> entities = new HashSet<Entity>();
		for (Map<String, String> entity: entityData)
		{			
			entities.add(DataProvider.buildEntityFromJsonMap(entity, endpoint));
			
		}
		
		return entities;
	}
	
	/**
	 * Assume the entityData list is sorted.
	 * @param entityData
	 * @param endpoint
	 * @return
	 * @throws ParseException 
	 */
	private static ArrayList<Entity> buildSortedEntitiesFromJson(List<Map<String, String>> entityData, String endpoint) throws ParseException
	{
		ArrayList<Entity> entities = new ArrayList<Entity>();
		for (Map<String, String> entity: entityData)
		{
			entities.add(DataProvider.buildEntityFromJsonMap(entity, endpoint));
		}
		
		return entities;
	}
	
	@Async
	public static Future<ArrayList<Event>> getClosestEvents(Location location) throws IOException, ParseException
	{
		String getClosestEvents = EndpointConstants.HOST + ":" + EndpointConstants.CORE_PORT + EndpointConstants.EVENT;
		URL url = new URL(getClosestEvents + String.format("?view_privacy=Public&exact_location=%s&limit=50&sort=distance&orderby=ASC", location.toString()));
		System.out.println(url.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		String data = DataProvider.getJsonDataFromConnection(conn);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JodaModule());
		TypeFactory typeFactory = objectMapper.getTypeFactory();	
		ArrayList<Event> events = objectMapper.readValue(data,  typeFactory.constructCollectionType(ArrayList.class, Event.class));
		conn.disconnect();
		
		return new AsyncResult<ArrayList<Event>>(events);	
	}
	
	@Async
	public static Future<ArrayList<Event>> getEventsFromSubscribedGroups(HashSet<Group> subscribedGroups) throws IOException, SQLException, ParseException
	{
		String getEventsFromGroups = EndpointConstants.HOST + ":" + EndpointConstants.CORE_PORT + "/" + EndpointConstants.EVENT;
		
		String paramList = "?";
		Iterator<Group> it = subscribedGroups.iterator();
		while (it.hasNext())
		{
			Group group = it.next();
			paramList += "owner=" + group.getId();

			if (it.hasNext())
			{
				paramList += "&";
			}
		}
		
		URL url = new URL(getEventsFromGroups + paramList);
		System.out.println(url.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		String data = DataProvider.getJsonDataFromConnection(conn);
		Gson g = new Gson();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JodaModule());
		TypeFactory typeFactory = objectMapper.getTypeFactory();	
		ArrayList<Event> events = objectMapper.readValue(data,  typeFactory.constructCollectionType(ArrayList.class, Event.class));
		
		return new AsyncResult<ArrayList<Event>>(events);
	}
}