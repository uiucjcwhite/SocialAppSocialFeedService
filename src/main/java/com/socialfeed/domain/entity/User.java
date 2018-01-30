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
public class User extends Entity {

	public enum Gender
	{
		Male,
		Female,
		Other,
		PreferNotToSpecify
	};
	
	private final List<String> columnNames = Collections.unmodifiableList(Arrays.asList("id", "gender"));

	private Gender gender;
	
	public User(String id, Gender gender) {
		super(id);
		this.gender = gender;
	}
	
	/* (non-Javadoc)
	 * @see main.java.com.core.entity.BaseDatabaseObject#getSerializedPropertyNames()
	 */
	@Override
	public List<String> getSerializedPropertyNames() {
		return this.columnNames;
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
