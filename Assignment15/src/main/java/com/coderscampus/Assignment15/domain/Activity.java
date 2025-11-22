package com.coderscampus.Assignment15.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Transient;
import jakarta.persistence.DiscriminatorColumn;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonGetter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "activity_type")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    
    private Instant timestamp;

    // Constructors, Getters, and Setters
    public Activity() {
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    
    
    @Transient
    @JsonGetter("type")
    public String getType() {
        if (this instanceof Sleep) {
            return "SLEEP";
        }
        if (this instanceof Eat) {
            return "EAT";
        }
        if (this instanceof Shower) {
            return "SHOWER";
        }
        return null;

    }
}
   
