/**
 * 
 */
package com.socialfeed.service;

import java.util.Iterator;
import java.util.LinkedList;

import com.controller.models.entities.Entity;
import com.controller.models.entities.Event;
import com.controller.models.entities.Group;
import com.socialfeed.domain.FeedData;

/**
 * @author Cameron
 *
 */
public class SocialFeedServicer {

	/**
	 * This class is kind of useless for now but will allow us to cache
	 * things in the future.
	 * @param id
	 */
	public static LinkedList<Entity> getSocialFeed(String id, String address)
	{
		FeedData feedData = new FeedData(id, address);
		DataProviderWorkflow dataProvider = new DataProviderWorkflow();
		feedData = dataProvider.beginWorkflow(feedData);
		
		Iterator<Event> eventIt = feedData.getEvents().iterator();
		while(eventIt.hasNext())
		{
			System.out.println(String.format("Subscribed Event id: %s", eventIt.next().getId()));
		}
		
		Iterator<Group> groupIt = feedData.getGroups().iterator();
		while(groupIt.hasNext())
		{
			System.out.println(String.format("Subscribed Group id: %s", groupIt.next().getId()));
		}
		
		SubscribedFeedWorkflow subscribedFeed = new SubscribedFeedWorkflow();
		feedData = subscribedFeed.beginWorkflow(feedData);
	
		for (Entity entity: feedData.getFeedItems())
		{
			System.out.println("Feed item: " + entity.getId());
		}
		
		SuggestedFeedWorkflow suggestedFeed = new SuggestedFeedWorkflow();
		feedData = suggestedFeed.beginWorkflow(feedData);
		
		for (Entity entity: feedData.getFeedItems())
		{
			System.out.println("Feed item: " + entity.getId());
		}
		
		return feedData.getFeedItems();
	}
}