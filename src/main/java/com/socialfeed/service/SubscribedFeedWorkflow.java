/**
 * 
 */
package com.socialfeed.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.joda.time.Instant;

import com.controller.models.entities.Entity;
import com.controller.models.entities.EntityPost;
import com.controller.models.entities.Event;
import com.controller.models.entities.Group;
import com.controller.models.relationships.UserConnection;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.socialapplibrary.endpoints.EndpointConstants;
import com.socialapplibrary.utility.Utility;
import com.socialfeed.domain.DataProvider;
import com.socialfeed.domain.FeedData;

/**
 * @author Cameron
 *
 */
public class SubscribedFeedWorkflow extends FeedWorkflow {
	
	/**
	 * This workflow step does the following:
	 * 1. Sort the event updates by event start time and then update time
	 * 2. Sort the group updates by update time
	 */
	@Override
	public FeedData beginWorkflow(FeedData feedData) {
		
		ArrayList<Event> sortedEvents = this.sortEvents(feedData.getEvents());
		ArrayList<Group> groups = new ArrayList<Group>(feedData.getGroups()); //No sorting yet;
		
		// Get the updates for these groups and entities
		String getUpdates = EndpointConstants.HOST + ":" + EndpointConstants.CORE_PORT + 
				EndpointConstants.ENTITY_POST + "?parent_id=root&";
		Iterator<Event> eventsIterator = sortedEvents.iterator();
		while (eventsIterator.hasNext())
		{
			getUpdates += "entity_id=" + eventsIterator.next().getId();
			
			if (eventsIterator.hasNext())
			{
				getUpdates += "&";
			}
		}
		
		if (groups.size() > 0)
		{
			getUpdates += "&";
		}
		
		Iterator<Group> groupsIterator = groups.iterator();
		while(groupsIterator.hasNext())
		{
			getUpdates += "entity_id=" + groupsIterator.next().getId();

			if (groupsIterator.hasNext())
			{
				getUpdates += "&";
			}
		}
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JodaModule());
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		URL url;
		HttpURLConnection conn = null;
		try {
			url = new URL(getUpdates);
			conn = (HttpURLConnection) url.openConnection();
			String data = DataProvider.getJsonDataFromConnection(conn);
			conn.disconnect();
			
			ArrayList<EntityPost> entityPosts = objectMapper.readValue(data,  typeFactory.constructCollectionType(ArrayList.class, EntityPost.class));
			HashSet<EntityPost> entityPostsSet = new HashSet<EntityPost>(entityPosts);
			feedData.setEntityPosts(entityPostsSet);
			this.sortEntityPosts(entityPostsSet);
			
			for (Entity entityPost: entityPostsSet)
			{
				feedData.addFeedItemToBack(entityPost);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (conn != null)
			{
				conn.disconnect();
			}
			
			e.printStackTrace();
		}
		
		return feedData;
		// Merge the sortedEvents and groups into the feedList.
		//ArrayList<Entity> partialFeedList = this.mergeSortedEventsAndGroupsIntoFeedData(sortedEvents, groups);
	}

	private ArrayList<Entity> mergeSortedEventsAndGroupsIntoFeedData(ArrayList<Event> sortedEvents, ArrayList<Group> groups, FeedData feedData)
	{
		ArrayList<Entity> interimFeedList = new ArrayList<Entity>();
		
		while (!sortedEvents.isEmpty() && !groups.isEmpty())
		{
			if (sortedEvents.isEmpty())
			{
				interimFeedList.addAll(groups);
				break;
			}
			else if (groups.isEmpty())
			{
				interimFeedList.addAll(sortedEvents);
				break;
			}
			
			int randomNum = ThreadLocalRandom.current().nextInt(0, 100); //Will give numbers from 0-99;
			
			if (randomNum >= 50)
			{
				feedData.addFeedItemToBack(sortedEvents.remove(0));
			}
			else
			{
				feedData.addFeedItemToBack(groups.remove(0));
			}
		}
		
		return interimFeedList;
	}
	
	private ArrayList<EntityPost> sortEntityPosts(HashSet<EntityPost> entityPosts)
	{
		Iterator<EntityPost> entityPostIterator = entityPosts.iterator();
		ArrayList<EntityPost> sortedEntityPosts = new ArrayList<EntityPost>();
		Instant currentTime = Utility.getUtcNow();
		while(entityPostIterator.hasNext())
		{
			EntityPost entityPost = (EntityPost)entityPostIterator.next();
			this.insertSortedEntityPost(sortedEntityPosts, entityPost);
		}
		
		return sortedEntityPosts;
	}
	
	private ArrayList<Event> sortEvents(HashSet<Event> events)
	{
		ArrayList<Event> sortedEventList = new ArrayList<Event>();
		ArrayList<Event> pastEvents = new ArrayList<Event>();
		ArrayList<Event> upcomingEvents = new ArrayList<Event>();
		
		Iterator<Event> eventsItr = events.iterator();
		Instant currentTime = Utility.getUtcNow();
		while (eventsItr.hasNext())
		{
			Event currentEvent = eventsItr.next();
			if (currentEvent.getStartDate().isAfter(currentTime))
			{
				this.insertSortedEvent(upcomingEvents, currentEvent);
			}
			else
			{
				// We don't particularly care about sorting past events on their start date.
				pastEvents.add(currentEvent);
			}
		}
		
		sortedEventList.addAll(upcomingEvents);
		sortedEventList.addAll(pastEvents);
		
		return sortedEventList;
	}

	private void insertSortedEntityPost(ArrayList<EntityPost> sortedEntityPosts, EntityPost entityPost)
	{
		if (sortedEntityPosts.isEmpty() ||
				entityPost.getCreatedDate().isAfter(((EntityPost)sortedEntityPosts.get(sortedEntityPosts.size() - 1)).getCreatedDate()))
		{
			sortedEntityPosts.add(entityPost);
			return;
		}
		
		if (entityPost.getCreatedDate().isBefore(((EntityPost)sortedEntityPosts.get(0)).getCreatedDate()))
		{
			sortedEntityPosts.add(0, entityPost);
			return;
		}
		
		for (int i = 0; i < sortedEntityPosts.size(); i++)
		{
			EntityPost currentEntityPost = (EntityPost)sortedEntityPosts.get(i);
			if (entityPost.getCreatedDate().isAfter(currentEntityPost.getCreatedDate()))
			{
				sortedEntityPosts.add(i, entityPost);
				return;
			}
		}
			
	}
	private void insertSortedEvent(ArrayList<Event> events, Event event)
	{
		if (events.isEmpty() ||
				event.getStartDate().isAfter(events.get(events.size() - 1).getStartDate()))
		{
			events.add(event);
			return;
		}
		
		if (event.getStartDate().isBefore(events.get(0).getStartDate()))
		{
			events.add(0, event);
			return;
		}
		
		for (int i = 0; i < events.size(); i++)
		{
			Event currentEvent = events.get(i);
			
			if (event.getStartDate().isAfter(currentEvent.getStartDate()))
			{
				events.add(i, event);
				return;
			}
		}
	}
}
