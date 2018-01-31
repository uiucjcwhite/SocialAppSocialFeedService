/**
 * 
 */
package com.socialfeed.domain.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.socialapplibrary.utility.GeoUtility.Location;

/**
 * @author Cameron
 *
 */
public class Group extends Entity {

	private final List<String> columnNames = Collections.unmodifiableList(Arrays.asList("id", "exact_location"));
	private Location exactLocation;
	/**
	 * @param id
	 */
	public Group(String id, Location location) {
		super(id, location);
		this.exactLocation = location;
	}

	/* (non-Javadoc)
	 * @see com.socialfeed.domain.entity.BaseDatabaseObject#getSerializedProperties()
	 */
	@Override
	public ArrayList<Object> getSerializedProperties() {
		return new ArrayList<Object> (Arrays.asList(this.getId(), this.exactLocation.longitude, this.exactLocation.latitude));
	}
}
