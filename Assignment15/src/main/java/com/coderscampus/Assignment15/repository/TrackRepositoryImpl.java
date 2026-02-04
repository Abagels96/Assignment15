package com.coderscampus.Assignment15.repository;

import com.coderscampus.Assignment15.domain.Track;
import com.coderscampus.Assignment15.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Custom repository implementation using Hibernate EntityManager directly
 */
@Repository
public class TrackRepositoryImpl implements TrackRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Track> findTrackByActivityId(Long activityId) {
        String jpql = "SELECT t FROM Track t WHERE t.activity.id = :activityId";
        TypedQuery<Track> query = entityManager.createQuery(jpql, Track.class);
        query.setParameter("activityId", activityId);
        
        List<Track> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Track> findTracksByUserAndDate(User user, LocalDate date) {
        // Eagerly fetch activity to ensure type is available in JSON response
        String jpql = "SELECT t FROM Track t JOIN FETCH t.activity WHERE t.user = :user AND t.activityDate = :date ORDER BY t.id";
        TypedQuery<Track> query = entityManager.createQuery(jpql, Track.class);
        query.setParameter("user", user);
        query.setParameter("date", date);
        
        return query.getResultList();
    }

    @Override
    @Transactional
    public Track saveTrack(Track track) {
        if (track.getId() == null) {
            // New entity - persist
            entityManager.persist(track);
            return track;
        } else {
            // Existing entity - merge
            return entityManager.merge(track);
        }
    }

    @Override
    @Transactional
    public void deleteTrackByActivityId(Long activityId) {
        String jpql = "DELETE FROM Track t WHERE t.activity.id = :activityId";
        entityManager.createQuery(jpql)
                .setParameter("activityId", activityId)
                .executeUpdate();
    }

    @Override
    public boolean existsByActivityId(Long activityId) {
        String jpql = "SELECT COUNT(t) > 0 FROM Track t WHERE t.activity.id = :activityId";
        TypedQuery<Boolean> query = entityManager.createQuery(jpql, Boolean.class);
        query.setParameter("activityId", activityId);
        
        return query.getSingleResult();
    }
}

