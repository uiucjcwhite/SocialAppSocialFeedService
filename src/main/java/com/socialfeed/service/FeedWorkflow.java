/**
 * 
 */
package com.socialfeed.service;

import com.socialfeed.domain.FeedData;

/**
 * @author Cameron
 *
 */
public abstract class FeedWorkflow implements IFeedWorkflow {

	protected static FeedData feedData;
	
	public FeedWorkflow(FeedData feedData)
	{
		this.feedData = feedData;
	}
	
	/**
	 * All workflow steps run through this method.
	 * @return
	 */
	@Override
	public abstract void beginWorkflow();

	
}
