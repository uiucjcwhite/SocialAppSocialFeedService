package com.socialfeed.domain.entity;

import java.util.HashSet;

/**
 * @author Cameron
 * A superclass for the three main entities: Group, Event, User.
 */
public abstract class Entity extends BaseDatabaseObject {
	
	/**
	 * When we create an Entity from existing data
	 *  we don't need to generate an id for it.
	 */
	public Entity(String id) {
		super(id);
	}
	
	/**
	 * Interests of the entity. This is the social aspect
	 * that links entities together.
	 */
	private HashSet<String> interests = new HashSet<String>();
	
	/**
	 * @return the interests
	 */
	public HashSet<String> getInterests() {
		return interests;
	}

	/**
	 * @param interests the interests to set
	 */
	public void setInterests(HashSet<String> interests) {
		if (interests != null)
		{
			this.interests = interests;
		}
	}
}