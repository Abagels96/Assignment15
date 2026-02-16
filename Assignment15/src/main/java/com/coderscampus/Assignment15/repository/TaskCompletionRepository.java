package com.coderscampus.Assignment15.repository;

import com.coderscampus.Assignment15.domain.Task;
import com.coderscampus.Assignment15.domain.TaskCompletion;
import com.coderscampus.Assignment15.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskCompletionRepository extends JpaRepository<TaskCompletion, Long> {
    Optional<TaskCompletion> findByTaskAndUserAndCompletionDate(Task task, User user, LocalDate completionDate);

    @Query("SELECT tc FROM TaskCompletion tc WHERE tc.user = :user AND tc.completionDate = :date")
    List<TaskCompletion> findByUserAndCompletionDate(@Param("user") User user, @Param("date") LocalDate date);
}


