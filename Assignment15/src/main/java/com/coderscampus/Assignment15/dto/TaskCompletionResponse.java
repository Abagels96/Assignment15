package com.coderscampus.Assignment15.dto;

import java.time.LocalDate;

public class TaskCompletionResponse {
    private Long id;
    private Long taskId;
    private LocalDate completionDate;
    private Boolean completed;
    private Long activityId;

    public TaskCompletionResponse() {
    }

    public TaskCompletionResponse(Long id, Long taskId, LocalDate completionDate, Boolean completed, Long activityId) {
        this.id = id;
        this.taskId = taskId;
        this.completionDate = completionDate;
        this.completed = completed;
        this.activityId = activityId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}


