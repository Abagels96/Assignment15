package com.coderscampus.Assignment15.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.DiscriminatorValue;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@DiscriminatorValue("EAT")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Eat extends Activity {
	
	
	private String mealDescription;
	@Enumerated(EnumType.STRING)

	private Meal mealType;
	private LocalDateTime eatTime;
	
	
	public Meal getMealType() {
		return mealType;
	}
	public void setMealType(Meal mealType) {
		this.mealType = mealType;
	}
	public LocalDateTime getEatTime() {
		return eatTime;
	}
	public void setEatTime(LocalDateTime eatTime) {
		this.eatTime = eatTime;
	}
	public String getMealDescription() {
		return mealDescription;
	}
	public void setMealDescription(String mealDescription) {
		this.mealDescription = mealDescription;
	}
	
	

}
