package com.coderscampus.Assignment15.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "daily_goal", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "activity_type"})
})
public class DailyGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "activity_type", nullable = false, length = 20)
    private String activityType; // "EAT", "SLEEP", "SHOWER"

    @Column(name = "goal_count", nullable = false)
    private Integer goalCount;

    // Constructors
    public DailyGoal() {
    }

    public DailyGoal(User user, String activityType, Integer goalCount) {
        this.user = user;
        this.activityType = activityType;
        this.goalCount = goalCount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public Integer getGoalCount() {
        return goalCount;
    }

    public void setGoalCount(Integer goalCount) {
        this.goalCount = goalCount;
    }
}

