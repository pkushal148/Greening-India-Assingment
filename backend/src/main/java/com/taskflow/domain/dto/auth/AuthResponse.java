package com.taskflow.domain.dto.auth;

public record AuthResponse(String token, UserResponse user) {
}