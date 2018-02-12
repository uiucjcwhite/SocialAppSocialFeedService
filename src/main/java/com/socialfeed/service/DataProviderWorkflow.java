package com.socialfeed.service;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.socialfeed.domain.DataProvider;
import com.socialfeed.domain.FeedData;
import com.controller.models.entities.Entity;
import com.socialapplibrary.core.entity.enums.ObjectIdPrefixEnum;
import com.socialapplibrary.core.exceptions.InvalidEnumValueException;

/**
 * @author Cameron
 *
 */
public class DataProviderWorkflow extends FeedWorkflow {
	
	@Override
	public FeedData beginWorkflow(FeedData feedData) {
		
		ArrayList<Future<HashSet<String>>> databaseSubscriptionCalls = new ArrayList<Future<HashSet<String>>>();
		ArrayList<Future<HashSet<Entity>>> databaseDataCalls = new ArrayList<Future<HashSet<Entity>>>();
		
		String id = feedData.getId();
		databaseSubscriptionCalls.add(DataProvider.getSubscribedIds(DataProvider.CONNECTION, 100, id));
		databaseSubscriptionCalls.add(DataProvider.getSubscribedIds(DataProvider.EVENT_RELATION, 100, id));
		databaseSubscriptionCalls.add(DataProvider.getSubscribedIds(DataProvider.GROUP_MEMBERSHIP, 100, id));
		//databaseSubscriptionCalls.add(DataProvider.getSubscribedIds(DataProvider.USER_INTEREST_SUBSCRIPTION, 200, id));

		//Add interest db table
		//Add timeout. Pass on what data we have if timeout occurs.
		//TODO Get interests of the groups/events we are subscribed to and rank based on the importance of that interest.
		int subscriptionIndex = 0;
		int dataIndex = 0;
		boolean subscriptionCallsPending = true;
		while (!databaseSubscriptionCalls.isEmpty() && (!databaseDataCalls.isEmpty() || subscriptionCallsPending))
		{
			if (!databaseSubscriptionCalls.isEmpty())
			{				
				Future<HashSet<String>> currentSubscriptionCall = databaseSubscriptionCalls.get(subscriptionIndex);
				if (currentSubscriptionCall.isDone())
				{
					System.out.println("Current call is done");
					try {
						HashSet<String> subscriptionIds = currentSubscriptionCall.get(); //We have all of the user's subscribed ids. We now need to get their data.
						if (subscriptionIds.size() > 0)
						{
							String sampleId = subscriptionIds.iterator().next();
							System.out.println(String.format("Sample id: %s", sampleId));
							if (ObjectIdPrefixEnum.getEnum(sampleId.substring(0, 3)) == ObjectIdPrefixEnum.USER_INTEREST_SUBSCRIPTION)
							{
								System.out.println("Setting user interests");
								feedData.setInterests(subscriptionIds);
							}
							else
							{
								String prefix = this.getDataEntityTypeFromRelationship(sampleId);
								System.out.println(String.format("Adding subscribed data call for %s", prefix));
								databaseDataCalls.add(DataProvider.getSubscribedData(prefix, subscriptionIds));
							}
						}
						else
						{
							System.out.println("No subscribed ids for this call");
						}
					} catch (InterruptedException | ExecutionException | InvalidEnumValueException e) {
						e.printStackTrace();
					}
					
					databaseSubscriptionCalls.remove(subscriptionIndex);
					System.out.println("Removing call");
				}
				
				if (!databaseSubscriptionCalls.isEmpty())
				{
					subscriptionIndex = (subscriptionIndex++) % databaseSubscriptionCalls.size();
				} else
				{
					subscriptionCallsPending = false;
				}
			}
			
			if (!databaseDataCalls.isEmpty())
			{
				Future<HashSet<Entity>> currentDataCall = databaseDataCalls.get(dataIndex);
				if (currentDataCall.isDone())
				{
					try {
						HashSet<Entity> entities = currentDataCall.get(); //We have all of the user's subscribed ids. We now need to get their data.
						if (entities.size() > 0)
						{
							feedData = this.setFeedDataEntity(entities, feedData);
						}
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
					
					databaseDataCalls.remove(dataIndex);
				}
				
				if (!databaseDataCalls.isEmpty()) // Can't do % 0;
				{
					dataIndex = (dataIndex++) % databaseDataCalls.size();
				}
			}
		}
		
		System.out.println("Data provider step finished");
		return feedData;
	}
	
	private FeedData setFeedDataEntity(HashSet<Entity> entities, FeedData feedData)
	{
		if (entities == null)
		{
			return feedData;
		}
		
		//All the entity ids should be of the same sub entity.
		String sampleId = entities.iterator().next().getId();
		String samplePrefix = sampleId.substring(0, 3);
		ObjectIdPrefixEnum prefix = null;
		try {
			prefix = ObjectIdPrefixEnum.getEnum(samplePrefix);
		} catch (InvalidEnumValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(String.format("Setting feed data for %s prefix", prefix.toString()));
		switch (prefix)
			{
			case USER:
				feedData.setUsers(entities);
				break;
			case GROUP:
				feedData.setGroups(entities);
				break;
			case EVENT:
				feedData.setEvents(entities);
				break;
			}
		
		return feedData;
	}
	
	private String getDataEntityTypeFromRelationship(String id) throws InvalidEnumValueException
	{
		String prefixStr = id.substring(0, 3);
		ObjectIdPrefixEnum prefix = ObjectIdPrefixEnum.getEnum(prefixStr);
		String endpoint = "";
		switch (prefix)
		{
		case USER:
			endpoint = DataProvider.PROFILE;
			break;
		case GROUP:
			endpoint = DataProvider.GROUP;
			break;
		case EVENT:
			endpoint = DataProvider.EVENT;
			break;
		case USER_INTEREST_SUBSCRIPTION:
		case EVENT_INTEREST_SUBSCRIPTION:
		case GROUP_INTEREST_SUBSCRIPTION:
			endpoint = DataProvider.INTEREST;
			break;
		}
		
		System.out.println(String.format("Getting endpoint %s from prefix %s", endpoint, prefixStr));
		return endpoint;
	}
}
