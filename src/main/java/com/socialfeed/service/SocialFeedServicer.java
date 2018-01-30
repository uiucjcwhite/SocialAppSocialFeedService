/**
 * 
 */
package com.socialfeed.service;

import java.util.LinkedList;

import com.socialfeed.domain.FeedData;
import com.socialfeed.domain.entity.Entity;

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
	public static LinkedList<Entity> getSocialFeed(String id)
	{
		FeedData feedData = new FeedData(id);
		DataProviderWorkflow dataProvider = new DataProviderWorkflow(feedData);
		dataProvider.beginWorkflow();
		
		SubscribedFeedWorkflow subscribedFeed = new SubscribedFeedWorkflow(feedData);
		subscribedFeed.beginWorkflow();
		
		SuggestedFeedWorkflow suggestedFeed = new SuggestedFeedWorkflow(feedData);
		suggestedFeed.beginWorkflow();
		
		return feedData.getFeedItems();
	}
}
