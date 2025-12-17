package com.coderscampus.Assignment15.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@DiscriminatorValue("SHOWER")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Shower extends Activity {
	
	@Column(nullable = false)
	private Integer lengthInMinutes;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
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
