/**
 * 
 */
package com.socialfeed.service;

import com.socialfeed.domain.FeedData;

/**
 * @author Cameron
 *
 */
public class SuggestedFeedWorkflow extends FeedWorkflow {

	public SuggestedFeedWorkflow(FeedData feedData) {
		super(feedData);
	}
	
	/* (non-Javadoc)
	 * @see com.socialfeed.service.FeedWorkflow#beginWorkflow()
	 */
	@Override
	public void beginWorkflow() {
		//No use yet, we need location working first. LOCATION IS WORKING BB
		
		
	}

}
