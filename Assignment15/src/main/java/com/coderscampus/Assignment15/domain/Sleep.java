package com.coderscampus.Assignment15.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.time.LocalDateTime;

@Entity
public class Sleep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    
    @Enumerated(EnumType.STRING)
    private SleepQuality quality;

    // Constructors
    public Sleep() {}

    public Sleep(LocalDateTime startDateTime, LocalDateTime endDateTime, SleepQuality quality) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.quality = quality;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public SleepQuality getQuality() {
        return quality;
    }

    public void setQuality(SleepQuality quality) {
        this.quality = quality;
    }
}

