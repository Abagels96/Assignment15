package com.coderscampus.Assignment15.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.coderscampus.Assignment15.domain.Sleep;
import com.coderscampus.Assignment15.repository.SleepRepository;

public class SleepService {




@Autowired
private SleepRepository sleepRepository;




public Sleep saveSleep(Sleep sleep) {
sleepRepository.save(sleep);
return sleep;
}
public List<Sleep> findAllSleeps() {
return sleepRepository.findAll();
}
public void deleteAllSleeps() {
sleepRepository.deleteAll();
}
public Long totalSleepMinutesAfter(LocalDateTime after) {
return sleepRepository.totalSleepMinutesAfter(after);
}
public Double averageSleepMinutes() {
return sleepRepository.averageSleepMinutes();
}
public List<Object[]> averageSleepMinutesByQuality() {
return sleepRepository.averageSleepMinutesByQuality();
}
public List<Object[]> countByQuality() {
return sleepRepository.countByQuality();
}
public Long totalSleepMinutesBetween(LocalDateTime startInclusive, LocalDateTime endExclusive) {
return sleepRepository.totalSleepMinutesBetween(startInclusive, endExclusive);
}
}