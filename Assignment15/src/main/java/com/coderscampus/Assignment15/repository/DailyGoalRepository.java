package com.coderscampus.Assignment15.repository;

import com.coderscampus.Assignment15.domain.DailyGoal;
import com.coderscampus.Assignment15.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DailyGoalRepository extends JpaRepository<DailyGoal, Long> {

    /**
     * Find all daily goals for a user
     */
    List<DailyGoal> findByUser(User user);

    /**
     * Find a specific goal for a user and activity type
     */
    Optional<DailyGoal> findByUserAndActivityType(User user, String activityType);
}

