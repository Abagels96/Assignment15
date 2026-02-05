package com.coderscampus.Assignment15.service;

import com.coderscampus.Assignment15.domain.Activity;
import com.coderscampus.Assignment15.domain.Sleep;
import com.coderscampus.Assignment15.domain.Eat;
import com.coderscampus.Assignment15.domain.Shower;
import com.coderscampus.Assignment15.domain.Track;
import com.coderscampus.Assignment15.domain.TrackStatus;
import com.coderscampus.Assignment15.domain.User;
import com.coderscampus.Assignment15.dto.AttributeSummaryDTO;
import com.coderscampus.Assignment15.repository.ActivityRepository;
import com.coderscampus.Assignment15.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SelfCareService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private TrackRepository trackRepository;

    public Activity saveActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    public List<Activity> findAllActivities() {
        // We sort here in Java to avoid complex DB queries
        List<Activity> activities = activityRepository.findAll();
        activities.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return activities;
    }

    public void deleteAllActivities() {
        activityRepository.deleteAll();
    }

    public void deleteActivityById(Long id) {
        activityRepository.deleteById(id);
    }

    public Activity findActivityById(Long id) {
        return activityRepository.findById(id).orElse(null);
    }

    // This is for the progress page!
    public Map<String, Long> getSummaryForPeriod(Instant after) {
        return activityRepository.countActivitiesByTypeAfter(after)
                .stream()
                .collect(Collectors.toMap(
                        obj -> (String) obj[0], // Key: "EAT", "SLEEP", "SHOWER"
                        obj -> (Long) obj[1]   // Value: 5, 2, 3
                ));
    }
    
    // Attribute-based tracking methods
    
    /**
     * Get counts of sleep activities grouped by quality
     */
    public Map<String, Long> getSleepQualityCounts(Instant after) {
        List<Sleep> sleepActivities = activityRepository.findSleepActivitiesAfter(after);
        
        // Initialize map with all quality levels at 0
        Map<String, Long> counts = new HashMap<>();
        counts.put("EXCELLENT", 0L);
        counts.put("GOOD", 0L);
        counts.put("FAIR", 0L);
        counts.put("POOR", 0L);
        
        // Count by quality
        Map<String, Long> actualCounts = sleepActivities.stream()
                .filter(sleep -> sleep.getQuality() != null)
                .collect(Collectors.groupingBy(
                        sleep -> sleep.getQuality().name(),
                        Collectors.counting()
                ));
        
        // Merge actual counts into initialized map
        counts.putAll(actualCounts);
        
        return counts;
    }
    
    /**
     * Calculate average sleep duration in hours
     */
    public Double getAverageSleepDuration(Instant after) {
        List<Sleep> sleepActivities = activityRepository.findSleepActivitiesAfter(after);
        
        // Filter to only include sleeps with both start and end times
        List<Sleep> completedSleeps = sleepActivities.stream()
                .filter(sleep -> sleep.getStartDateTime() != null && sleep.getEndDateTime() != null)
                .collect(Collectors.toList());
        
        if (completedSleeps.isEmpty()) {
            return 0.0;
        }
        
        // Calculate total duration in hours
        double totalHours = completedSleeps.stream()
                .mapToDouble(sleep -> {
                    Duration duration = Duration.between(sleep.getStartDateTime(), sleep.getEndDateTime());
                    return duration.toMinutes() / 60.0;
                })
                .sum();
        
        return totalHours / completedSleeps.size();
    }
    
    /**
     * Get list of meal descriptions with timestamps
     */
    public List<AttributeSummaryDTO.MealInfo> getMealDescriptions(Instant after) {
        List<Eat> eatActivities = activityRepository.findEatActivitiesAfter(after);
        
        return eatActivities.stream()
                .map(eat -> new AttributeSummaryDTO.MealInfo(
                        eat.getTimestamp().toString(),
                        eat.getMealDescription()
                ))
                .sorted((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp())) // Most recent first
                .collect(Collectors.toList());
    }
    
    /**
     * Calculate average shower length in minutes
     */
    public Double getAverageShowerLength(Instant after) {
        List<Shower> showerActivities = activityRepository.findShowerActivitiesAfter(after);
        
        // Filter to only include showers with length recorded
        List<Shower> showersWithLength = showerActivities.stream()
                .filter(shower -> shower.getLengthInMinutes() != null && shower.getLengthInMinutes() > 0)
                .collect(Collectors.toList());
        
        if (showersWithLength.isEmpty()) {
            return 0.0;
        }
        
        double totalMinutes = showersWithLength.stream()
                .mapToInt(Shower::getLengthInMinutes)
                .sum();
        
        return totalMinutes / showersWithLength.size();
    }

    // Track-related methods

    /**
     * Save activity and create/update corresponding track entry
     */
    @Transactional
    public Activity saveActivityWithTrack(User user, Activity activity) {
        // Save the activity first
        Activity savedActivity = activityRepository.save(activity);

        try {
            // Derive activityDate from timestamp (using system default zone)
            LocalDate activityDate = savedActivity.getTimestamp()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Determine initial status based on activity type
            TrackStatus status;
            if (savedActivity instanceof Sleep) {
                Sleep sleep = (Sleep) savedActivity;
                // Sleep is DONE only if endDateTime is set, otherwise NOT_STARTED
                status = (sleep.getEndDateTime() != null) ? TrackStatus.DONE : TrackStatus.NOT_STARTED;
            } else {
                // Eat and Shower are immediately DONE when created
                status = TrackStatus.DONE;
            }

            // Check if track entry already exists for this activity using Hibernate directly
            Track existingTrack = trackRepository.findTrackByActivityId(savedActivity.getId()).orElse(null);

            if (existingTrack != null) {
                // Update existing track using Hibernate directly
                existingTrack.setActivityDate(activityDate);
                existingTrack.setStatus(status);
                trackRepository.saveTrack(existingTrack);
                System.out.println("Updated track entry for activity ID: " + savedActivity.getId() + " using Hibernate");
            } else {
                // Create new track entry using Hibernate directly
                Track track = new Track(user, savedActivity, activityDate, status);
                trackRepository.saveTrack(track);
                System.out.println("Created track entry for activity ID: " + savedActivity.getId() + ", user: " + user.getUsername() + ", status: " + status + " using Hibernate");
            }
        } catch (Exception e) {
            System.err.println("Error creating/updating track entry for activity ID: " + savedActivity.getId());
            e.printStackTrace();
            // Don't fail the activity save if track creation fails
        }

        return savedActivity;
    }

    /**
     * Update track status for an activity (e.g., when sleep end time is added)
     * Creates a track entry if it doesn't exist (for backward compatibility)
     * Uses Hibernate directly via custom repository
     */
    @Transactional
    public void updateTrackStatusForActivity(Activity activity, User user) {
        Track track = trackRepository.findTrackByActivityId(activity.getId()).orElse(null);
        
        if (track == null) {
            // Create track entry if it doesn't exist (for backward compatibility)
            LocalDate activityDate = activity.getTimestamp()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            
            TrackStatus status;
            if (activity instanceof Sleep) {
                Sleep sleep = (Sleep) activity;
                status = (sleep.getEndDateTime() != null) ? TrackStatus.DONE : TrackStatus.NOT_STARTED;
            } else {
                status = TrackStatus.DONE;
            }
            
            track = new Track(user, activity, activityDate, status);
        } else {
            // Update status based on activity type
            if (activity instanceof Sleep) {
                Sleep sleep = (Sleep) activity;
                // Sleep is DONE only if endDateTime is set
                track.setStatus((sleep.getEndDateTime() != null) ? TrackStatus.DONE : TrackStatus.NOT_STARTED);
            } else {
                // Eat and Shower remain DONE
                track.setStatus(TrackStatus.DONE);
            }

            // Update activityDate if timestamp changed
            LocalDate activityDate = activity.getTimestamp()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            track.setActivityDate(activityDate);
        }

        trackRepository.saveTrack(track);
    }

    /**
     * Get all track entries for a user on a specific date
     * Uses Hibernate directly via custom repository
     */
    public List<Track> getTracksForUserAndDate(User user, LocalDate date) {
        return trackRepository.findTracksByUserAndDate(user, date);
    }
}

