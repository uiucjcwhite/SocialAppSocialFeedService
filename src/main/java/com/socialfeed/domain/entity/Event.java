/**
 * 
 */
package com.socialfeed.domain.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joda.time.Instant;

/**
 * @author Cameron
 *
 */
public class Event extends Entity {

	private final List<String> columnNames = Collections.unmodifiableList(Arrays.asList("id", "start_date"));

	private Instant startDate;

	public Event(String id, Instant startDate) {
		super(id);
		this.startDate = startDate;
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
		return new ArrayList<String> (Arrays.asList(this.id, this.startDate.toString()));
	}
	
	
}
