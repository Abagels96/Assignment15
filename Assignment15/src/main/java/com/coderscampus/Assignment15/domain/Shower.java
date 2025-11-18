package com.coderscampus.Assignment15.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("SHOWER")
public class Shower extends Activity {
	
	private Integer lengthInMinutes;
	private Rating rating;
	
	public Integer getLengthInMinutes() {
		return lengthInMinutes;
	}
	public void setLengthInMinutes(Integer lengthInMinutes) {
		this.lengthInMinutes = lengthInMinutes;
	}
	public Rating getRating() {
		return rating;
	}
	public void setRating(Rating rating) {
		this.rating = rating;
	}
	
	
	
	

}
