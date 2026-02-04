package com.coderscampus.Assignment15.dto;

import com.coderscampus.Assignment15.domain.Timing;

public class TaskResponse {
    private Long taskId;
    private String name;
    private Timing frequency;

    public TaskResponse() {
    }

    public TaskResponse(Long taskId, String name, Timing frequency) {
        this.taskId = taskId;
        this.name = name;
        this.frequency = frequency;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timing getFrequency() {
        return frequency;
    }

    public void setFrequency(Timing frequency) {
        this.frequency = frequency;
    }
}


