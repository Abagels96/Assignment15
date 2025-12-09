package com.coderscampus.Assignment15.service;

import com.coderscampus.Assignment15.domain.Activity;
import com.coderscampus.Assignment15.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SelfCareService {

    @Autowired
    private ActivityRepository activityRepository;

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

    // This is for the progress page!
    public Map<String, Long> getSummaryForPeriod(Instant after) {
        return activityRepository.countActivitiesByTypeAfter(after)
                .stream()
                .collect(Collectors.toMap(
                        obj -> (String) obj[0], // Key: "EAT", "SLEEP", "SHOWER"
                        obj -> (Long) obj[1]   // Value: 5, 2, 3
                ));
    }
}

