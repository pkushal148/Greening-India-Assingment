package com.taskflow.domain.dto.project;

import com.taskflow.domain.dto.task.TaskResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProjectDetailResponse(
        UUID id,
        String name,
        String description,
        UUID ownerId,
        LocalDateTime createdAt,
        List<TaskResponse> tasks) {
}