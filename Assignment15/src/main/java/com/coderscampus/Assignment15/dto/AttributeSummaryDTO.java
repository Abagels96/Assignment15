package com.coderscampus.Assignment15.dto;

import com.coderscampus.Assignment15.domain.Eat;
import java.util.List;
import java.util.Map;

/**
 * DTO for attribute-based activity summaries.
 * Contains different fields depending on activity type being queried.
 */
public class AttributeSummaryDTO {
    
    // For Sleep activities
    private Map<String, Long> sleepQualityCounts;
    private Double averageSleepDurationHours;
    
    // For Eat activities
    private Long mealCount;
    private List<MealInfo> meals;
    
    // For Shower activities
    private Double averageShowerLengthMinutes;
    
    // Constructors
    public AttributeSummaryDTO() {}
    
    // Constructor for Sleep
    public AttributeSummaryDTO(Map<String, Long> sleepQualityCounts, Double averageSleepDurationHours) {
        this.sleepQualityCounts = sleepQualityCounts;
        this.averageSleepDurationHours = averageSleepDurationHours;
    }
    
    // Constructor for Eat
    public AttributeSummaryDTO(Long mealCount, List<MealInfo> meals) {
        this.mealCount = mealCount;
        this.meals = meals;
    }
    
    // Constructor for Shower
    public AttributeSummaryDTO(Double averageShowerLengthMinutes) {
        this.averageShowerLengthMinutes = averageShowerLengthMinutes;
    }
    
    // Getters and Setters
    public Map<String, Long> getSleepQualityCounts() {
        return sleepQualityCounts;
    }
    
    public void setSleepQualityCounts(Map<String, Long> sleepQualityCounts) {
        this.sleepQualityCounts = sleepQualityCounts;
    }
    
    public Double getAverageSleepDurationHours() {
        return averageSleepDurationHours;
    }
    
    public void setAverageSleepDurationHours(Double averageSleepDurationHours) {
        this.averageSleepDurationHours = averageSleepDurationHours;
    }
    
    public Long getMealCount() {
        return mealCount;
    }
    
    public void setMealCount(Long mealCount) {
        this.mealCount = mealCount;
    }
    
    public List<MealInfo> getMeals() {
        return meals;
    }
    
    public void setMeals(List<MealInfo> meals) {
        this.meals = meals;
    }
    
    public Double getAverageShowerLengthMinutes() {
        return averageShowerLengthMinutes;
    }
    
    public void setAverageShowerLengthMinutes(Double averageShowerLengthMinutes) {
        this.averageShowerLengthMinutes = averageShowerLengthMinutes;
    }
    
    /**
     * Inner class to represent meal information
     */
    public static class MealInfo {
        private String timestamp;
        private String description;
        
        public MealInfo() {}
        
        public MealInfo(String timestamp, String description) {
            this.timestamp = timestamp;
            this.description = description;
        }
        
        public String getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
    }
}

