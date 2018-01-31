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
public class User extends Entity {

	public enum Gender
	{
		Male,
		Female,
		Other,
		PreferNotToSpecify
	};
	
	private final List<String> columnNames = Collections.unmodifiableList(Arrays.asList("id", "gender", "exact_location"));

	private Gender gender;
	
	public User(String id, Gender gender) {
		super(id, null);
		this.gender = gender;
	}
	

	/* (non-Javadoc)
	 * @see com.socialfeed.domain.entity.BaseDatabaseObject#getSerializedProperties()
	 */
	@Override
	public ArrayList<Object> getSerializedProperties() {
		// TODO Auto-generated method stub
		return new ArrayList<Object> (Arrays.asList(this.getId(), this.gender.toString()));
	}
	
	
}
