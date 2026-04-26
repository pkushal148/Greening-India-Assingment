package com.taskflow.presentation;

import com.taskflow.application.task.TaskService;
import com.taskflow.domain.dto.task.CreateTaskRequest;
import com.taskflow.domain.dto.task.TaskResponse;
import com.taskflow.domain.dto.task.UpdateTaskRequest;
import com.taskflow.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @PathVariable UUID projectId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID assignee,
            Authentication authentication) {
        UUID userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(taskService.getProjectTasks(projectId, userId, status, assignee));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateTaskRequest request,
            Authentication authentication) {
        UUID userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(projectId, userId, request));
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskRequest request,
            Authentication authentication) {
        UUID userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(taskService.updateTask(taskId, userId, request));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            Authentication authentication) {
        UUID userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        taskService.deleteTask(taskId, userId);
        return ResponseEntity.noContent().build();
    }
}