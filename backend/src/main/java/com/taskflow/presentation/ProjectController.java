package com.taskflow.presentation;

import com.taskflow.application.project.ProjectService;
import com.taskflow.domain.dto.project.CreateProjectRequest;
import com.taskflow.domain.dto.project.ProjectDetailResponse;
import com.taskflow.domain.dto.project.ProjectResponse;
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
@RequestMapping("/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getProjects(Authentication authentication) {
        UUID userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(projectService.getProjectsForUser(userId));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            Authentication authentication) {
        UUID userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDetailResponse> getProject(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(projectService.getProjectById(id, userId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody CreateProjectRequest request,
            Authentication authentication) {
        UUID userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(projectService.updateProject(id, userId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        projectService.deleteProject(id, userId);
        return ResponseEntity.noContent().build();
    }
}