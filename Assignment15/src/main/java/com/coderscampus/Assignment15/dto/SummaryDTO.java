package com.coderscampus.Assignment15.dto;

import java.util.Map;

// This object will be converted to JSON for the frontend
public class SummaryDTO {
    private Map<String, Long> last24Hours;
    private Map<String, Long> last7Days;

    // Constructors, Getters, and Setters
    public SummaryDTO() {}
    
    public SummaryDTO(Map<String, Long> last24Hours, Map<String, Long> last7Days) {
        this.last24Hours = last24Hours;
        this.last7Days = last7Days;
    }
    
    public Map<String, Long> getLast24Hours() { return last24Hours; }
    public void setLast24Hours(Map<String, Long> last24Hours) { this.last24Hours = last24Hours; }
    public Map<String, Long> getLast7Days() { return last7Days; }
    public void setLast7Days(Map<String, Long> last7Days) { this.last7Days = last7Days; }
}

