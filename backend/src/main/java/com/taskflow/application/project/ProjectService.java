package com.taskflow.application.project;

import com.taskflow.domain.dto.project.CreateProjectRequest;
import com.taskflow.domain.dto.project.ProjectDetailResponse;
import com.taskflow.domain.dto.project.ProjectResponse;
import com.taskflow.domain.dto.task.TaskResponse;
import com.taskflow.domain.entity.Project;
import com.taskflow.domain.entity.Task;
import com.taskflow.domain.entity.User;
import com.taskflow.domain.exception.ResourceNotFoundException;
import com.taskflow.domain.exception.UnauthorizedException;
import com.taskflow.domain.repository.ProjectRepository;
import com.taskflow.domain.repository.TaskRepository;
import com.taskflow.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    public List<ProjectResponse> getProjectsForUser(UUID userId) {
        return projectRepository.findProjectsForUser(userId)
                .stream()
                .map(this::toProjectResponse)
                .collect(Collectors.toList());
    }

    public ProjectResponse createProject(UUID userId, CreateProjectRequest request) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setOwner(owner);

        project = projectRepository.save(project);
        return toProjectResponse(project);
    }

    public ProjectDetailResponse getProjectById(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!hasAccess(project, userId)) {
            throw new UnauthorizedException("You do not have access to this project");
        }

        List<TaskResponse> tasks = taskRepository.findByProjectId(projectId)
                .stream()
                .map(this::toTaskResponse)
                .collect(Collectors.toList());

        return new ProjectDetailResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getOwner().getId(),
                project.getCreatedAt(),
                tasks);
    }

    public ProjectResponse updateProject(UUID projectId, UUID userId, CreateProjectRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!project.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Only the project owner can update this project");
        }

        project.setName(request.name());
        project.setDescription(request.description());
        project = projectRepository.save(project);

        return toProjectResponse(project);
    }

    public void deleteProject(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!project.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Only the project owner can delete this project");
        }

        projectRepository.delete(project);
    }

    private boolean hasAccess(Project project, UUID userId) {
        if (project.getOwner().getId().equals(userId)) {
            return true;
        }

        return taskRepository.findByProjectId(project.getId())
                .stream()
                .anyMatch(task -> task.getAssignee() != null && task.getAssignee().getId().equals(userId));
    }

    private ProjectResponse toProjectResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getOwner().getId(),
                project.getCreatedAt());
    }

    private TaskResponse toTaskResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus().name().toLowerCase(),
                task.getPriority().name().toLowerCase(),
                task.getProject().getId(),
                task.getAssignee() != null ? task.getAssignee().getId() : null,
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt());
    }
}