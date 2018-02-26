/**
 * 
 */
package com.socialfeed.service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

import com.controller.models.entities.Entity;
import com.controller.models.entities.Event;
import com.socialfeed.domain.DataProvider;
import com.socialfeed.domain.FeedData;

/**
 * @author Cameron
 *
 */
public class SuggestedFeedWorkflow extends FeedWorkflow {
	@Override
	public FeedData beginWorkflow(FeedData feedData) {
		try {
			Future<ArrayList<Event>> closestEventsCall = DataProvider.getClosestEvents(feedData.getLocation());
			Future<ArrayList<Event>> subscribedGroupEventsCall = DataProvider.getEventsFromSubscribedGroups(feedData.getGroups());
			boolean closestEventsCallHandled = false;
			boolean subscribedGroupEventsCallHandled = false;
			while (!closestEventsCallHandled && !subscribedGroupEventsCallHandled)
			{
				if (closestEventsCall.isDone() && !closestEventsCallHandled)
				{
					closestEventsCallHandled = true;
					ArrayList<Event> closestEvents = closestEventsCall.get();
					System.out.println("Adding closest events");
					feedData = this.addClosestEventsToFeedData(closestEvents, feedData);
				}
				
				if (subscribedGroupEventsCall.isDone() && !subscribedGroupEventsCallHandled)
				{
					subscribedGroupEventsCallHandled = true;
					ArrayList<Event> subscribedGroupEvents = subscribedGroupEventsCall.get();
					System.out.println("Adding subscribed events");
					feedData = this.addSubscribedGroupEventsToFeedData(subscribedGroupEvents, feedData);
				}
			}
			
		} catch (IOException | ParseException | SQLException |
				InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		return feedData;
	}
	
	public FeedData addSubscribedGroupEventsToFeedData(ArrayList<Event> groupEvents, FeedData feedData)
	{
		int feedDataIndex = 0;
		int feedDataSize = feedData.getFeedItems().size();

		while (!groupEvents.isEmpty() && feedDataIndex < feedDataSize)
		{
			int randomNum = ThreadLocalRandom.current().nextInt(0, 100); //Will give numbers from 0-99;
			
			// 40% chance we insert into the current position in the feed data
			if (randomNum < 40)
			{
				Entity currentEntity = groupEvents.iterator().next();
				feedData.addFeedItem(currentEntity, feedDataIndex);
				groupEvents.remove(currentEntity);
			}
			
			feedDataIndex++;
		}
		
		if (!groupEvents.isEmpty())
		{
			for (Entity entity: groupEvents)
			{
				feedData.addFeedItemToBack(entity);
			}
		}
		
		return feedData;
	}
	
	
	public FeedData addClosestEventsToFeedData(ArrayList<Event> closestEvents, FeedData feedData)
	{
		int feedDataIndex = 0;
		int feedDataSize = feedData.getFeedItems().size();
		while (!closestEvents.isEmpty() || feedDataIndex < feedDataSize)
		{
			int randomNum = ThreadLocalRandom.current().nextInt(0, 100); //Will give numbers from 0-99;
			
			// 20% chance we insert into the current position in the feed data
			if (randomNum < 20)
			{
				Entity currentEntity = closestEvents.remove(0);
				feedData.addFeedItem(currentEntity, feedDataIndex);
				System.out.println("Added entity " + currentEntity.getId());
			} 
			
			feedDataIndex++;
		}
		
		if (!closestEvents.isEmpty())
		{
			for (Entity entity: closestEvents)
			{
				feedData.addFeedItemToBack(entity);
				System.out.println("Added entity to back " + entity.getId());

			}
		}
		
		return feedData;
	}

}
