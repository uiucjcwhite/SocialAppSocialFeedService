package com.socialfeed.domain.entity;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Cameron + aaron
 *
 */
public interface DatabaseObject {

	public String getId();
	
	/**
	 * Get all the properties of the entity for insertion into the database.
	 * @return
	 */
	public abstract List<String> getSerializedPropertyNames();
	
	/**
	 * Get all the properties of the entity for insertion into the database.
	 * @return
	 */
	public abstract List<String> getSerializedProperties();
}
