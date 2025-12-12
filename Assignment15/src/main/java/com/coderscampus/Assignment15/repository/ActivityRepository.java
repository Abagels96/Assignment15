package com.coderscampus.Assignment15.repository;

import com.coderscampus.Assignment15.domain.Activity;
import com.coderscampus.Assignment15.domain.Sleep;
import com.coderscampus.Assignment15.domain.Eat;
import com.coderscampus.Assignment15.domain.Shower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // Custom query to find activities after a certain time
    List<Activity> findByTimestampAfter(Instant after);

    // Custom query to count activities by type after a certain time
    // Uses native query to access the discriminator column 'activity_type'
    // Table name is lowercase 'activity' for MySQL compatibility
    @Query(value = "SELECT a.activity_type, COUNT(*) FROM activity a WHERE a.timestamp > :after GROUP BY a.activity_type", nativeQuery = true)
    List<Object[]> countActivitiesByTypeAfter(@Param("after") Instant after);
    
    // Custom queries for attribute-based tracking
    @Query("SELECT s FROM Sleep s WHERE s.timestamp > :after")
    List<Sleep> findSleepActivitiesAfter(@Param("after") Instant after);
    
    @Query("SELECT e FROM Eat e WHERE e.timestamp > :after")
    List<Eat> findEatActivitiesAfter(@Param("after") Instant after);
    
    @Query("SELECT s FROM Shower s WHERE s.timestamp > :after")
    List<Shower> findShowerActivitiesAfter(@Param("after") Instant after);
}

