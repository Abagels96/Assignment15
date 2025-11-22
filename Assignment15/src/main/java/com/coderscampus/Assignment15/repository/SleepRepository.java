package com.coderscampus.Assignment15.repository;

import com.coderscampus.Assignment15.domain.Sleep;
import com.coderscampus.Assignment15.domain.SleepQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SleepRepository extends JpaRepository<Sleep, Long> {

    // Basic filters
    List<Sleep> findByStartDateTimeAfter(LocalDateTime after);
    List<Sleep> findByStartDateTimeBetween(LocalDateTime startInclusive, LocalDateTime endExclusive);
    List<Sleep> findByQuality(SleepQuality quality);

    // Total minutes slept after a given time
    // Uses native query with MySQL's TIMESTAMPDIFF function
    // Note: MySQL uses TIMESTAMPDIFF(unit, date1, date2)
    @Query(value = "SELECT SUM(TIMESTAMPDIFF(MINUTE, s.start_date_time, s.end_date_time)) " +
           "FROM activity s WHERE s.activity_type = 'SLEEP' AND s.start_date_time >= :after", nativeQuery = true)
    Long totalSleepMinutesAfter(@Param("after") LocalDateTime after);

    // Average sleep duration in minutes (across all records)
    // Using MySQL's TIMESTAMPDIFF function - syntax: TIMESTAMPDIFF(MINUTE, date1, date2)
    @Query(value = "SELECT AVG(TIMESTAMPDIFF(MINUTE, s.start_date_time, s.end_date_time)) " +
           "FROM activity s WHERE s.activity_type = 'SLEEP'", nativeQuery = true)
    Double averageSleepMinutes();

    // Average sleep duration in minutes by quality
    @Query(value = "SELECT s.quality, AVG(TIMESTAMPDIFF(MINUTE, s.start_date_time, s.end_date_time)) " +
           "FROM activity s WHERE s.activity_type = 'SLEEP' GROUP BY s.quality", nativeQuery = true)
    List<Object[]> averageSleepMinutesByQuality();

    // Count of sleeps by quality
    @Query(value = "SELECT s.quality, COUNT(*) FROM activity s WHERE s.activity_type = 'SLEEP' GROUP BY s.quality", nativeQuery = true)
    List<Object[]> countByQuality();

    // Total minutes slept between two timestamps
    @Query(value = "SELECT SUM(TIMESTAMPDIFF(MINUTE, s.start_date_time, s.end_date_time)) " +
           "FROM activity s WHERE s.activity_type = 'SLEEP' AND s.start_date_time >= :startInclusive AND s.end_date_time <= :endExclusive", nativeQuery = true)
    Long totalSleepMinutesBetween(@Param("startInclusive") LocalDateTime startInclusive,
                                  @Param("endExclusive") LocalDateTime endExclusive);
}


