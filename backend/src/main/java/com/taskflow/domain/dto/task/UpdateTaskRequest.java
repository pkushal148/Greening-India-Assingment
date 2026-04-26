package com.taskflow.domain.dto.task;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateTaskRequest(
        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters") String title,
        String description,
        String status,
        String priority,
        UUID assigneeId,
        LocalDate dueDate) {
}