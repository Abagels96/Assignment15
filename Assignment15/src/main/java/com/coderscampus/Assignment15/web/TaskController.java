package com.coderscampus.Assignment15.web;

import com.coderscampus.Assignment15.domain.Task;
import com.coderscampus.Assignment15.domain.TaskCompletion;
import com.coderscampus.Assignment15.domain.User;
import com.coderscampus.Assignment15.dto.TaskCompletionRequest;
import com.coderscampus.Assignment15.dto.TaskCompletionResponse;
import com.coderscampus.Assignment15.dto.TaskResponse;
import com.coderscampus.Assignment15.service.TaskService;
import com.coderscampus.Assignment15.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;

    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody Task task, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userService.findByUsername(authentication.getName());
        Task created = taskService.createTask(user, task);

        TaskResponse response = new TaskResponse(created.getTaskId(), created.getName(), created.getFrequency());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userService.findByUsername(authentication.getName());
        List<TaskResponse> tasks = taskService.getTasksForUser(user).stream()
                .map(task -> new TaskResponse(task.getTaskId(), task.getName(), task.getFrequency()))
                .collect(Collectors.toList());

        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @PostMapping("/{taskId}/completion")
    public ResponseEntity<TaskCompletionResponse> upsertCompletion(
            @PathVariable Long taskId,
            @RequestBody TaskCompletionRequest request,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userService.findByUsername(authentication.getName());
        Task task = taskService.getTaskForUser(taskId, user);

        if (task == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        LocalDate completionDate;
        if (request.getCompletionDate() != null && !request.getCompletionDate().isBlank()) {
            completionDate = LocalDate.parse(request.getCompletionDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            completionDate = LocalDate.now();
        }

        boolean completed = request.getCompleted() != null && request.getCompleted();
        TaskCompletion completion = taskService.upsertCompletion(task, completionDate, completed, request.getActivityId());

        TaskCompletionResponse response = new TaskCompletionResponse(
                completion.getId(),
                completion.getTask().getTaskId(),
                completion.getCompletionDate(),
                completion.getCompleted(),
                completion.getActivity() != null ? completion.getActivity().getId() : null
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/completion")
    public ResponseEntity<List<TaskCompletionResponse>> getCompletionsForDate(
            @RequestParam String date,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userService.findByUsername(authentication.getName());
        LocalDate completionDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);

        List<TaskCompletionResponse> responses = taskService.getCompletionsForUserAndDate(user, completionDate)
                .stream()
                .map(tc -> new TaskCompletionResponse(
                        tc.getId(),
                        tc.getTask().getTaskId(),
                        tc.getCompletionDate(),
                        tc.getCompleted(),
                        tc.getActivity() != null ? tc.getActivity().getId() : null
                ))
                .collect(Collectors.toList());

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}

