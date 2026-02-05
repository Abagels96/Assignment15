package com.coderscampus.Assignment15.dto;

public class TaskCompletionRequest {
    private Boolean completed;
    private String completionDate; // YYYY-MM-DD
    private Long activityId;

    public TaskCompletionRequest() {
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}


