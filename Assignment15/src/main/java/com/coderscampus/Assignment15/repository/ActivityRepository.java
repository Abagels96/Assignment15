package com.coderscampus.Assignment15.repository;

import com.coderscampus.Assignment15.domain.Activity;
import com.coderscampus.Assignment15.domain.Sleep;
import com.coderscampus.Assignment15.domain.Eat;
import com.coderscampus.Assignment15.domain.Shower;
import com.coderscampus.Assignment15.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // User-scoped queries
    List<Activity> findByUserAndTimestampAfter(User user, Instant after);
    List<Activity> findByUser(User user);

    // Custom query to count activities by type after a certain time for a specific user
    // Uses native query to access the discriminator column 'activity_type'
    // Table name is lowercase 'activity' for MySQL compatibility
    @Query(value = "SELECT a.activity_type, COUNT(*) FROM activity a WHERE a.user_id = :userId AND a.timestamp > :after GROUP BY a.activity_type", nativeQuery = true)
    List<Object[]> countActivitiesByTypeAfterForUser(@Param("userId") Long userId, @Param("after") Instant after);
    
    // Custom queries for attribute-based tracking (user-scoped)
    @Query("SELECT s FROM Sleep s WHERE s.user = :user AND s.timestamp > :after")
    List<Sleep> findSleepActivitiesForUserAfter(@Param("user") User user, @Param("after") Instant after);
    
    @Query("SELECT e FROM Eat e WHERE e.user = :user AND e.timestamp > :after")
    List<Eat> findEatActivitiesForUserAfter(@Param("user") User user, @Param("after") Instant after);
    
    @Query("SELECT s FROM Shower s WHERE s.user = :user AND s.timestamp > :after")
    List<Shower> findShowerActivitiesForUserAfter(@Param("user") User user, @Param("after") Instant after);
}

