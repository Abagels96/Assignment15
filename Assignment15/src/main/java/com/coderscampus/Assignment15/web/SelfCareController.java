package com.coderscampus.Assignment15.web;

import com.coderscampus.Assignment15.dto.SummaryDTO;
import com.coderscampus.Assignment15.domain.Activity;
import com.coderscampus.Assignment15.domain.Sleep;
import com.coderscampus.Assignment15.domain.Eat;
import com.coderscampus.Assignment15.domain.Shower;
import com.coderscampus.Assignment15.service.SelfCareService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Self-Care Activities.
 * Base URL: /api/v1/selfcare
 *
 * NOTE: @CrossOrigin is added to allow requests from the frontend (running on a different port).
 * For production, you should configure this more restrictively.
 */
@CrossOrigin(origins = "*") // Allows all cross-origin requests
@RestController
@RequestMapping("/selfcare")
public class SelfCareController {

    private final SelfCareService selfCareService;

    @Autowired
    public SelfCareController(SelfCareService selfCareService) {
        this.selfCareService = selfCareService;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Endpoint: POST /selfcare/record
     * Used by the frontend to record a new activity (Eat, Sleep, Shower).
     */
    @PostMapping("/record")
    public ResponseEntity<Activity> recordActivity(@RequestBody Map<String, Object> activityData) {
        String type = (String) activityData.get("type");
        Activity activity = null;
        System.out.println("sleep has been recorded");
        // Create the appropriate subclass based on type
        if ("SLEEP".equals(type)) {
            activity = objectMapper.convertValue(activityData, Sleep.class);
            
        } else if ("EAT".equals(type)) {
            activity = objectMapper.convertValue(activityData, Eat.class);
        } else if ("SHOWER".equals(type)) {
            activity = objectMapper.convertValue(activityData, Shower.class);
        } else {
            // Fallback to base Activity if type is unknown
            activity = objectMapper.convertValue(activityData, Activity.class);
        }
        
        // Ensure timestamp is set on the server-side if not provided
        if (activity.getTimestamp() == null) {
             activity.setTimestamp(Instant.now());
        }

        Activity savedActivity = selfCareService.saveActivity(activity);
        return new ResponseEntity<>(savedActivity, HttpStatus.CREATED);
    }

    /**
     * Endpoint: POST /selfcare/record/sleep
     * Used by the frontend to record a sleep activity with quality.
     */
    @PostMapping("/record/sleep")
    public ResponseEntity<Sleep> recordSleep(@RequestBody Sleep sleep) {
        // Validate sleep quality - Jackson will reject invalid enum values during deserialization
        if (sleep.getQuality() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        // Ensure timestamp is set on the server-side if not provided
       
        Sleep savedSleep = (Sleep) selfCareService.saveActivity(sleep);
        return new ResponseEntity<>(savedSleep, HttpStatus.CREATED);
    }
    
    /**
     * Endpoint: POST /selfcare/record/shower
     * Used by the frontend to record a shower activity with rating and length.
     */
    @PostMapping("/record/shower")
    public ResponseEntity<Shower> recordShower(@RequestBody Shower shower) {
        try {
            System.out.println("Received shower activity: " + shower);
            System.out.println("Rating: " + shower.getRating());
            System.out.println("Length: " + shower.getLengthInMinutes());
            
            // Validate shower rating and length
            if (shower.getRating() == null) {
                System.out.println("Validation failed: Rating is null");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (shower.getLengthInMinutes() == null || shower.getLengthInMinutes() < 1) {
                System.out.println("Validation failed: Length is null or < 1");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Ensure timestamp is set on the server-side if not provided
            if (shower.getTimestamp() == null) {
                shower.setTimestamp(Instant.now());
            }
            
            Shower savedShower = (Shower) selfCareService.saveActivity(shower);
            System.out.println("Shower saved successfully with ID: " + savedShower.getId());
            return new ResponseEntity<>(savedShower, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error saving shower activity: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
     


     
        @PostMapping("/record/eat")
        public ResponseEntity<Eat> recordEat(@RequestBody Eat eat) {
            try {
                System.out.println("Received eat activity: " + eat);
                System.out.println("Meal description: " + eat.getMealDescription());
                
                // Validate meal description
                if (eat.getMealDescription() == null || eat.getMealDescription().trim().isEmpty()) {
                    System.out.println("Validation failed: Meal description is null or empty");
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                
                // Ensure timestamp is set on the server-side if not provided
                if (eat.getTimestamp() == null) {
                    eat.setTimestamp(Instant.now());
                }
                
                Eat savedEat = (Eat) selfCareService.saveActivity(eat);
                System.out.println("Eat saved successfully with ID: " + savedEat.getId());
                return new ResponseEntity<>(savedEat, HttpStatus.CREATED);
            } catch (Exception e) {
                System.err.println("Error saving eat activity: " + e.getMessage());
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
     

    /**
     * Endpoint: GET /api/v1/selfcare/history
     * Used by the frontend to fetch all recorded activities for display.
     */
    @GetMapping("/history")
    public ResponseEntity<List<Activity>> getHistory() {
        List<Activity> history = selfCareService.findAllActivities();
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    /**
     * NEW Endpoint: GET /api/v1/selfcare/summary
     * Used by the Progress Page to get summary data for visualizations.
     */
    @GetMapping("/summary")
    public ResponseEntity<SummaryDTO> getSummary() {
        // Calculate time boundaries
        Instant now = Instant.now();
        Instant aDayAgo = now.minus(24, ChronoUnit.HOURS);
        Instant aWeekAgo = now.minus(7, ChronoUnit.DAYS);

        // Fetch summary data from the service
        Map<String, Long> last24Hours = selfCareService.getSummaryForPeriod(aDayAgo);
        Map<String, Long> last7Days = selfCareService.getSummaryForPeriod(aWeekAgo);
        
        // Ensure all types are present in the map, even if count is 0
        last24Hours.putIfAbsent("EAT", 0L);
        last24Hours.putIfAbsent("SLEEP", 0L);
        last24Hours.putIfAbsent("SHOWER", 0L);

        last7Days.putIfAbsent("EAT", 0L);
        last7Days.putIfAbsent("SLEEP", 0L);
        last7Days.putIfAbsent("SHOWER", 0L);

        // Create the DTO to send to the frontend
        SummaryDTO summary = new SummaryDTO(last24Hours, last7Days);
        
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }

    /**
     * NEW Endpoint: DELETE /api/v1/selfcare/history/clear
     * Used by the frontend to delete all history.
     */
    @DeleteMapping("/history/clear")
    public ResponseEntity<Void> clearAllHistory() {
        selfCareService.deleteAllActivities();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content is standard for success
    }

    /**
     * Endpoint: DELETE /selfcare/activity/{id}
     * Used by the frontend to delete a single activity by ID.
     */
    @DeleteMapping("/activity/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        selfCareService.deleteActivityById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

