package com.coderscampus.Assignment15.repository;

import com.coderscampus.Assignment15.domain.Track;
import com.coderscampus.Assignment15.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Custom repository interface for direct Hibernate operations
 */
public interface TrackRepositoryCustom {
    
    /**
     * Find track entry by activity ID using Hibernate directly
     */
    Optional<Track> findTrackByActivityId(Long activityId);
    
    /**
     * Find all track entries for a user on a specific date using Hibernate
     */
    List<Track> findTracksByUserAndDate(User user, LocalDate date);
    
    /**
     * Save track entry using Hibernate directly
     */
    Track saveTrack(Track track);
    
    /**
     * Delete track entry by activity ID using Hibernate
     */
    void deleteTrackByActivityId(Long activityId);
    
    /**
     * Check if track exists for activity using Hibernate
     */
    boolean existsByActivityId(Long activityId);
}

