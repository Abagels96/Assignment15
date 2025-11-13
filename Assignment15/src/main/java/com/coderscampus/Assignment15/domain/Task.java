package com.coderscampus.Assignment15.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Task {

    private String name;
    private Boolean isCompleted;
private Timing frequency;
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer taskId; 
    public String getName() {
        return name;
    }

    public Boolean getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
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

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }
}

