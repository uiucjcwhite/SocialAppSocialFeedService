/**
 * 
 */
package com.socialfeed.domain.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joda.time.Instant;

import com.socialapplibrary.utility.GeoUtility.Location;

/**
 * @author Cameron
 *
 */
public class Event extends Entity {

	private final List<String> columnNames = Collections.unmodifiableList(Arrays.asList("id", "start_date"));

	private Instant startDate;
	private Location exactLocation;
	
	public Event(String id, Instant startDate, Location location) {
		super(id);
		this.startDate = startDate;
		this.exactLocation = location;
	}

	public Instant getStartDate()
	{
		return this.startDate;
	}
	
	public Location getLocation()
	{
		return this.exactLocation;
	}

	/* (non-Javadoc)
	 * @see com.socialfeed.domain.entity.BaseDatabaseObject#getSerializedProperties()
	 */
	@Override
	public ArrayList<Object> getSerializedProperties() {
		return new ArrayList<Object> (Arrays.asList(
				this.getId(),
				this.startDate.toString(),
				this.exactLocation.longitude,
				this.exactLocation.latitude));
	}
	
	
}
