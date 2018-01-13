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

	/**
	 * All workflow steps run through this method.
	 * @return
	 */
	@Override
	public abstract FeedData beginWorkflow(FeedData feedData);

	
}
