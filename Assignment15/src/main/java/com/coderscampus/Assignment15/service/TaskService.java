package com.coderscampus.Assignment15.service;

import com.coderscampus.Assignment15.domain.Activity;
import com.coderscampus.Assignment15.domain.Task;
import com.coderscampus.Assignment15.domain.TaskCompletion;
import com.coderscampus.Assignment15.domain.User;
import com.coderscampus.Assignment15.repository.ActivityRepository;
import com.coderscampus.Assignment15.repository.TaskCompletionRepository;
import com.coderscampus.Assignment15.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskCompletionRepository taskCompletionRepository;
    private final ActivityRepository activityRepository;

    public TaskService(TaskRepository taskRepository,
                       TaskCompletionRepository taskCompletionRepository,
                       ActivityRepository activityRepository) {
        this.taskRepository = taskRepository;
        this.taskCompletionRepository = taskCompletionRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional
    public Task createTask(User user, Task task) {
        task.getUsers().add(user);
        if (task.getCompleted() == null) {
            task.setCompleted(false);
        }
        return taskRepository.save(task);
    }

    public List<Task> getTasksForUser(User user) {
        return taskRepository.findByUsersContaining(user);
    }

    public Task getTaskForUser(Long taskId, User user) {
        return taskRepository.findByTaskIdAndUsersContaining(taskId, user).orElse(null);
    }

    @Transactional
    public TaskCompletion upsertCompletion(User user, Task task, LocalDate date, boolean completed, Long activityId) {
        TaskCompletion existing = taskCompletionRepository
                .findByTaskAndUserAndCompletionDate(task, user, date)
                .orElse(null);

        Activity activity = null;
        if (activityId != null) {
            activity = activityRepository.findById(activityId).orElse(null);
        }

        if (existing != null) {
            existing.setCompleted(completed);
            existing.setActivity(activity);
            return taskCompletionRepository.save(existing);
        }

        TaskCompletion completion = new TaskCompletion(task, user, date, completed, activity);
        return taskCompletionRepository.save(completion);
    }

    public List<TaskCompletion> getCompletionsForUserAndDate(User user, LocalDate date) {
        return taskCompletionRepository.findByUserAndCompletionDate(user, date);
    }
}

