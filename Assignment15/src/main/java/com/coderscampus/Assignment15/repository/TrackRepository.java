package com.coderscampus.Assignment15.repository;

import com.coderscampus.Assignment15.domain.Track;
import com.coderscampus.Assignment15.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Track Repository - extends both Spring Data JPA and custom Hibernate repository
 */
public interface TrackRepository extends JpaRepository<Track, Long>, TrackRepositoryCustom {

    /**
     * Find track entry by activity ID (Spring Data JPA method)
     * Note: You can also use the custom Hibernate method: findTrackByActivityId()
     */
    Optional<Track> findByActivityId(Long activityId);

    /**
     * Find all track entries for a user on a specific date (Spring Data JPA method)
     * Note: You can also use the custom Hibernate method: findTracksByUserAndDate()
     */
    @Query("SELECT t FROM Track t WHERE t.user = :user AND t.activityDate = :date")
    List<Track> findByUserAndActivityDate(@Param("user") User user, @Param("date") LocalDate date);
}

