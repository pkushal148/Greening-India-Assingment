package com.taskflow.domain.dto.project;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String name,
        String description,
        UUID ownerId,
        LocalDateTime createdAt) {
}