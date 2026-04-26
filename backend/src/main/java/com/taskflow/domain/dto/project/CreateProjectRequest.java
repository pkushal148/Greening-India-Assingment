package com.taskflow.domain.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
        @NotBlank(message = "Project name is required") @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters") String name,

        @Size(max = 1000, message = "Description must not exceed 1000 characters") String description) {
}