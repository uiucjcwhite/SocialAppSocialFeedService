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

	private static final String CONNECTION_TABLE = "\"Connection\"";
	private static final String EVENT_TABLE = "\"Event\"";
	private static final String EVENT_RELATION_TABLE = "\"Event Relation\"";
	private static final String GROUP_TABLE = "\"Group\"";
	private static final String GROUP_MEMBERSHIP_TABLE = "\"Group Membership\"";
	private static final String PROFILE_TABLE = "\"Profile\"";
	private static final String USER_TABLE = "\"User\"";
	

	@Override
	public FeedData beginWorkflow(FeedData feedData) {
		
		ArrayList<Future<HashSet<String>>> databaseCalls = new ArrayList<Future<HashSet<String>>>();
		String id = feedData.getId();
		databaseCalls.add(DataProvider.getSubscribedData(CONNECTION_TABLE, 100, id));
		databaseCalls.add(DataProvider.getSubscribedData(EVENT_RELATION_TABLE, 100, id));
		databaseCalls.add(DataProvider.getSubscribedData(GROUP_MEMBERSHIP_TABLE, 100, id));
		//Add interest db table
		//Add timeout. Pass on what data we have if timeout occurs.
		int index = 0;
		while (!databaseCalls.isEmpty())
		{
			Future<HashSet<String>> currentCall = databaseCalls.get(index);
			if (currentCall.isDone())
			{
				try {
					currentCall.get();
					// Next step: Gather the entity data (what interests they are subscribed too
					// Pass to this.setFeedDataEntity
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				
				databaseCalls.remove(index);
			}
			
			index = (index++) % databaseCalls.size();
			
		}
		
		return null;
		
		
	}
	
	private void setFeedDataEntity(FeedData feedData, HashSet<Entity> entityIds)
	{
		//All the entity ids should be of the same sub entity.
		String sampleId = entityIds.iterator().next().getId();
		String samplePrefix = sampleId.substring(0, 3);
		ObjectIdPrefixEnum prefix = ObjectIdPrefixEnum.valueOf(samplePrefix);
		switch (prefix)
			{
			case USER:
				feedData.setUsers(entityIds);
				break;
			case GROUP:
				feedData.setGroups(entityIds);
				break;
			case EVENT:
				feedData.setEvents(entityIds);
				break;	
			}

			
	}
}
