package com.coderscampus.Assignment15.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Progress {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Integer progressId; 
    private Integer streak;

}
