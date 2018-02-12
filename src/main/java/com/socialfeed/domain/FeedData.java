/**
 * 
 */
package com.socialfeed.domain;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import com.controller.models.entities.*;
import com.socialapplibrary.utility.GeoUtility;
import com.socialapplibrary.utility.GeoUtility.Location;;
/**
 * @author Cameron
 *
 */
public class FeedData {

	private String id;
	private Location location;
	private HashSet<String> interests;
	private HashSet<Event> events;
	private HashSet<Group> groups;
	private HashSet<User> friends;
	private HashSet<EntityPost> entityPosts;
	
	/**
	 * The list that gets sent back to the user. The list is a ranked list
	 * where the first element is the most and important the last is the least important.
	 */
	private LinkedList<Entity> feedItems;
	
	public FeedData(String id, String address)
	{
		this.id = id;
		this.interests = new HashSet<String>();
		this.events = new HashSet<Event>();
		this.groups = new HashSet<Group>();
		this.friends = new HashSet<User>();
		this.entityPosts = new HashSet<EntityPost>();
		this.feedItems = new LinkedList<Entity>();
		this.location = GeoUtility.convertAddressToGeoLocation(address);
	}
	
	public void addInterest(String interest)
	{
		if (!this.interests.contains(interest))
		{
			interests.add(interest);
		}
	}
	
	public void addFeedItem(Entity entity, int position)
	{
		if (this.feedItems.size() == 0)
		{
			this.feedItems.add(entity);
			return;
		}
		
		int index = this.feedItems.indexOf(entity);
		// Check if we are adding to the back
		if (position >= this.feedItems.size())
		{
			position = this.feedItems.size();
		}
		
		if (index == -1)
		{
			System.out.println(String.format("Inserting Entity %s at postion %d", entity.getId(), position));
			this.feedItems.add(position, entity);
		} 
		// Only PROMOTE the ranking of an entity
		else if (position < index)
		{
			this.feedItems.remove(index);
			this.feedItems.add(position, entity);
			System.out.println(String.format("Removing Entity %s from index %d and moving to postion %d",
					entity.getId(),
					index,
					position));

		}
		else
		{
			System.out.println("Not adding entity post " + entity.getId() + " to feed items at position " + Integer.toString(position));
		}
	}
	
	public void addFeedItemFront(Entity entity)
	{
		this.addFeedItem(entity, 0);
	}
	
	public void addFeedItemToBack(Entity entity)
	{
		this.addFeedItem(entity, Math.max(this.feedItems.size() - 1, 0));
	}
	
	public void addEvent(Event event)
	{
		this.events.add(event);
	}
	
	public void addGroup(Group group)
	{
		this.groups.add(group);
	}
	
	public void addFriend(User friend)
	{
	    this.friends.add(friend);
		
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public HashSet<String> getInterests()
	{
		return this.interests;
	}
	
	public HashSet<Group> getGroups()
	{
		return this.groups;
	}
	
	public HashSet<Event> getEvents()
	{
		return this.events;
	}
	
	public HashSet<User> getFriends()
	{
		return this.friends;
	}
	
	public LinkedList<Entity> getFeedItems() {
		return this.feedItems;
	}
	
	public Location getLocation() {
		return this.location;
	}
	
	public HashSet<EntityPost> getEntityPosts() {
		return this.entityPosts;
	}
	
	public void setInterests(HashSet<String> interests)
	{
		this.interests = interests;
	}
	
	public void setGroups(HashSet<Entity> groups)
	{
		Iterator<Entity> iter = groups.iterator();
		this.groups = new HashSet<Group>();
		while(iter.hasNext())
		{
			this.groups.add((Group)iter.next());
		}	
	}
	
	public void setEvents(HashSet<Entity> events)
	{
		Iterator<Entity> iter = events.iterator();
		this.events = new HashSet<Event>();
		while (iter.hasNext())
		{
			this.events.add((Event)iter.next());
		}	
	}
	
	public void setUsers(HashSet<Entity> friends)
	{
		Iterator<Entity> iter = friends.iterator();
		this.friends = new HashSet<User>();
		while (iter.hasNext())
		{
			this.friends.add((User)iter.next());
		}
	}
	
	public void setEntityPosts(HashSet<EntityPost> entityPosts)
	{
		this.entityPosts = entityPosts;
	}
}
