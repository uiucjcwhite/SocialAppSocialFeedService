/**
 * 
 */
package com.socialfeed.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

import org.joda.time.Instant;

import com.socialapplibrary.utility.Utility;
import com.socialfeed.domain.FeedData;
import com.socialfeed.domain.entity.Entity;
import com.socialfeed.domain.entity.Event;
import com.socialfeed.domain.entity.Group;

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
	public void beginWorkflow(FeedData feedData) {
		
		ArrayList<Event> sortedEvents = this.sortEvents(feedData.getEvents());
		ArrayList<Group> groups = new ArrayList<Group>(feedData.getGroups()); //No sorting yet;
		
		// Merge the sortedEvents and groups into the feedList.
		ArrayList<Entity> partialFeedList = this.mergeSortedEventsAndGroups(sortedEvents, groups);
		
		for (Entity entity: partialFeedList)
		{
			feedData.addFeedItemToBack(entity);
		}
	}
	
	private ArrayList<Entity> mergeSortedEventsAndGroups(ArrayList<Event> sortedEvents, ArrayList<Group> groups)
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
				interimFeedList.add(sortedEvents.remove(0));
			}
			else
			{
				interimFeedList.add(groups.remove(0));
			}
		}
		
		return interimFeedList;
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

	private void insertSortedEvent(ArrayList<Event> events, Event event)
	{
		if (events.isEmpty())
		{
			events.add(event);
			return;
		}
		
		if (event.getStartDate().isBefore(events.get(0).getStartDate()))
		{
			events.add(0, event);
			return;
		}
		
		if (event.getStartDate().isAfter(events.get(events.size() - 1).getStartDate()))
		{
			events.add(events.size() - 1, event);
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
