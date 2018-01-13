package com.socialfeed.domain.entity;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseDatabaseObject implements DatabaseObject {

	protected final String id;
	
	public BaseDatabaseObject(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof BaseDatabaseObject) && (this.id == ((BaseDatabaseObject) obj).id);
	}
	
	/**
	 * Get all the property names of the entity for insertion into the database.
	 * @return
	 */
	public abstract List<String> getSerializedPropertyNames();
	
	
	/**
	 * Get all the properties of the entity for insertion into the database.
	 * @return
	 */
	public abstract ArrayList<String> getSerializedProperties();
}
