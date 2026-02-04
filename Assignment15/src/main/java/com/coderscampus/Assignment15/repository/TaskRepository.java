package com.coderscampus.Assignment15.repository;

import com.coderscampus.Assignment15.domain.Task;
import com.coderscampus.Assignment15.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    Optional<Task> findByTaskIdAndUser(Long taskId, User user);
}

