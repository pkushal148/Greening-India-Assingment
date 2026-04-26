package com.taskflow.domain.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CreateTaskRequest(
        @NotBlank(message = "Title is required") @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters") String title,

        @Size(max = 1000, message = "Description must not exceed 1000 characters") String description,

        @NotNull(message = "Priority is required") String priority,

        UUID assigneeId,
        LocalDate dueDate) {
}