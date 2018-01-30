/**
 * 
 */
package com.socialfeed.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.socialfeed.domain.DataProvider;
import com.socialfeed.domain.FeedData;
import com.socialfeed.domain.entity.Entity;
import com.socialfeed.domain.entity.User;
import com.socialapplibrary.core.entity.enums.ObjectIdPrefixEnum;
/**
 * @author Cameron
 *
 */
public class DataProviderWorkflow extends FeedWorkflow {


	@Override
	public void beginWorkflow(FeedData feedData) {
		
		ArrayList<Future<HashSet<String>>> databaseSubscriptionCalls = new ArrayList<Future<HashSet<String>>>();
		ArrayList<Future<HashSet<Entity>>> databaseDataCalls = new ArrayList<Future<HashSet<Entity>>>();
		
		String id = feedData.getId();
		databaseSubscriptionCalls.add(DataProvider.getSubscribedIds(DataProvider.CONNECTION_TABLE, 100, id));
		databaseSubscriptionCalls.add(DataProvider.getSubscribedIds(DataProvider.EVENT_RELATION_TABLE, 100, id));
		databaseSubscriptionCalls.add(DataProvider.getSubscribedIds(DataProvider.GROUP_MEMBERSHIP_TABLE, 100, id));
		databaseSubscriptionCalls.add(DataProvider.getSubscribedIds(DataProvider.USER_INTEREST_SUBSCRIPTION_TABLE, 200, id));

		//Add interest db table
		//Add timeout. Pass on what data we have if timeout occurs.
		//TODO Get interests of the groups/events we are subscribed to and rank based on the importance of that interest.
		int subscriptionIndex = 0;
		int dataIndex = 0;
		while (!databaseSubscriptionCalls.isEmpty() && !databaseDataCalls.isEmpty())
		{
			if (!databaseSubscriptionCalls.isEmpty())
			{
				Future<HashSet<String>> currentSubscriptionCall = databaseSubscriptionCalls.get(subscriptionIndex);
				if (currentSubscriptionCall.isDone())
				{
					try {
						HashSet<String> subscriptionIds = currentSubscriptionCall.get(); //We have all of the user's subscribed ids. We now need to get their data.
						if (subscriptionIds.size() > 0)
						{
							String sampleId = subscriptionIds.iterator().next();
							
							if (ObjectIdPrefixEnum.valueOf(sampleId.substring(0, 3)) == ObjectIdPrefixEnum.USER_INTEREST_SUBSCRIPTION)
							{
								feedData.setInterests(subscriptionIds);
							}
							else
							{
								databaseDataCalls.add(DataProvider.getSubscribedData(this.getTableNameBasedOnPrefix(sampleId), subscriptionIds));
							}
						}
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
					
					databaseSubscriptionCalls.remove(subscriptionIndex);
				}
				
				if (!databaseSubscriptionCalls.isEmpty())
				{
					subscriptionIndex = (subscriptionIndex++) % databaseSubscriptionCalls.size();
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
							this.setFeedDataEntity(feedData, entities);
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
	}
	
	private void setFeedDataEntity(FeedData feedData, HashSet<Entity> entities)
	{
		//All the entity ids should be of the same sub entity.
		String sampleId = entities.iterator().next().getId();
		String samplePrefix = sampleId.substring(0, 3);
		ObjectIdPrefixEnum prefix = ObjectIdPrefixEnum.valueOf(samplePrefix);
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
	}
	
	private String getTableNameBasedOnPrefix(String id)
	{
		String prefixStr = id.substring(0, 3);
		ObjectIdPrefixEnum prefix = ObjectIdPrefixEnum.valueOf(prefixStr);
		String tableName = "";
		switch (prefix)
		{
		case USER:
			tableName = DataProvider.USER_TABLE;
			break;
		case GROUP:
			tableName = DataProvider.GROUP_TABLE;
			break;
		case EVENT:
			tableName = DataProvider.EVENT_TABLE;
			break;
		case USER_PROFILE:
			tableName = DataProvider.PROFILE_TABLE;
			break;
		case USER_CONNECTION:
			tableName = DataProvider.CONNECTION_TABLE;
			break;
		case GROUP_MEMBERSHIP:
			tableName = DataProvider.GROUP_MEMBERSHIP_TABLE;
			break;
		case EVENT_ATTENDANCE_RELATION:
			tableName = DataProvider.EVENT_RELATION_TABLE;
			break;
		case INTEREST_RELATIONSHIP:
			tableName = DataProvider.INTEREST_TABLE;
			break;
		case USER_INTEREST_SUBSCRIPTION:
			tableName = "";
			break;
		case EVENT_INTEREST_SUBSCRIPTION:
			tableName = "";
			break;
		case GROUP_INTEREST_SUBSCRIPTION:
			tableName = "";
			break;
		}
		
		return tableName;
	}
}
