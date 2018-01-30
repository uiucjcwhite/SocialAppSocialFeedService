/**
 * 
 */
package com.socialfeed.domain.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Cameron
 *
 */
public class Group extends Entity {

	private final List<String> columnNames = Collections.unmodifiableList(Arrays.asList("id"));

	/**
	 * @param id
	 */
	public Group(String id) {
		super(id);
	}

	/* (non-Javadoc)
	 * @see com.socialfeed.domain.entity.BaseDatabaseObject#getSerializedPropertyNames()
	 */
	@Override
	public List<String> getSerializedPropertyNames() {
		return this.columnNames;
	}

	/* (non-Javadoc)
	 * @see com.socialfeed.domain.entity.BaseDatabaseObject#getSerializedProperties()
	 */
	@Override
	public ArrayList<String> getSerializedProperties() {
		return new ArrayList<String> (Arrays.asList(this.id));
	}
}
