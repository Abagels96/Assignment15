package com.coderscampus.Assignment15.service;

import com.coderscampus.Assignment15.domain.DailyGoal;
import com.coderscampus.Assignment15.domain.User;
import com.coderscampus.Assignment15.repository.DailyGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DailyGoalService {

    @Autowired
    private DailyGoalRepository dailyGoalRepository;

    // Default goals if user hasn't set any
    private static final int DEFAULT_EAT_GOAL = 3;
    private static final int DEFAULT_SLEEP_GOAL = 1;
    private static final int DEFAULT_SHOWER_GOAL = 1;

    /**
     * Get daily goals for a user, returning defaults if not set
     */
    public Map<String, Integer> getDailyGoals(User user) {
        List<DailyGoal> goals = dailyGoalRepository.findByUser(user);
        Map<String, Integer> goalMap = new HashMap<>();

        // Initialize with defaults
        goalMap.put("EAT", DEFAULT_EAT_GOAL);
        goalMap.put("SLEEP", DEFAULT_SLEEP_GOAL);
        goalMap.put("SHOWER", DEFAULT_SHOWER_GOAL);

        // Override with user's custom goals
        for (DailyGoal goal : goals) {
            goalMap.put(goal.getActivityType(), goal.getGoalCount());
        }

        return goalMap;
    }

    /**
     * Update daily goals for a user
     * @param user The user
     * @param goalsMap Map of activity type to goal count (e.g., {"EAT": 3, "SLEEP": 1})
     */
    @Transactional
    public void updateDailyGoals(User user, Map<String, Integer> goalsMap) {
        for (Map.Entry<String, Integer> entry : goalsMap.entrySet()) {
            String activityType = entry.getKey();
            Integer goalCount = entry.getValue();

            // Validate goal count
            if (goalCount == null || goalCount < 0) {
                throw new IllegalArgumentException("Goal count must be a non-negative integer for " + activityType);
            }

            // Validate activity type
            if (!activityType.equals("EAT") && !activityType.equals("SLEEP") && !activityType.equals("SHOWER")) {
                throw new IllegalArgumentException("Invalid activity type: " + activityType);
            }

            // Find existing goal or create new one
            DailyGoal goal = dailyGoalRepository.findByUserAndActivityType(user, activityType)
                    .orElse(new DailyGoal(user, activityType, goalCount));

            goal.setGoalCount(goalCount);
            dailyGoalRepository.save(goal);
        }
    }
}

