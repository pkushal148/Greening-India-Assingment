package com.taskflow.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private String error;
    private String message;
    private Map<String, String> fields;
    private LocalDateTime timestamp;
    private int status;

    public ApiError(String error, String message, Map<String, String> fields, LocalDateTime timestamp, int status) {
        this.error = error;
        this.message = message;
        this.fields = fields;
        this.timestamp = timestamp;
        this.status = status;
    }
}