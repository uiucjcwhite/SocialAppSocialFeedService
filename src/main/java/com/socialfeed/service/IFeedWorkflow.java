/**
 * 
 */
package com.socialfeed.service;

import com.socialfeed.domain.FeedData;

/**
 * @author Cameron
 *
 */
public interface IFeedWorkflow {

	/**
	 * All workflow steps run through this method.
	 * @return
	 */
	public abstract void beginWorkflow();
}
