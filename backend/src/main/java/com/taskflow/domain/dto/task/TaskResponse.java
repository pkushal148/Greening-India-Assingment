package com.taskflow.domain.dto.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        String description,
        String status,
        String priority,
        UUID projectId,
        UUID assigneeId,
        LocalDate dueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}