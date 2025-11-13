package com.coderscampus.Assignment15.repository;

import com.coderscampus.Assignment15.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // Custom query to find activities after a certain time
    List<Activity> findByTimestampAfter(Instant after);

    // Custom query to count activities by type after a certain time
    @Query("SELECT a.type, COUNT(a) FROM Activity a WHERE a.timestamp > :after GROUP BY a.type")
    List<Object[]> countActivitiesByTypeAfter(@Param("after") Instant after);
}

