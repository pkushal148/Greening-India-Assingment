package com.taskflow.application.task;

import com.taskflow.domain.dto.task.CreateTaskRequest;
import com.taskflow.domain.dto.task.TaskResponse;
import com.taskflow.domain.dto.task.UpdateTaskRequest;
import com.taskflow.domain.entity.Project;
import com.taskflow.domain.entity.Task;
import com.taskflow.domain.entity.Task.TaskPriority;
import com.taskflow.domain.entity.Task.TaskStatus;
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
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    public List<TaskResponse> getProjectTasks(UUID projectId, UUID userId,
            String status, UUID assigneeId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!hasAccess(project, userId)) {
            throw new UnauthorizedException("You do not have access to this project");
        }

        List<Task> tasks = taskRepository.findByProjectId(projectId);

        if (status != null) {
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            tasks = tasks.stream()
                    .filter(task -> task.getStatus() == taskStatus)
                    .collect(Collectors.toList());
        }

        if (assigneeId != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getAssignee() != null && task.getAssignee().getId().equals(assigneeId))
                    .collect(Collectors.toList());
        }

        return tasks.stream().map(this::toTaskResponse).collect(Collectors.toList());
    }

    public TaskResponse createTask(UUID projectId, UUID userId, CreateTaskRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!hasAccess(project, userId)) {
            throw new UnauthorizedException("You do not have access to this project");
        }

        User assignee = null;
        if (request.assigneeId() != null) {
            assignee = userRepository.findById(request.assigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }

        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setPriority(TaskPriority.valueOf(request.priority().toUpperCase()));
        task.setProject(project);
        task.setAssignee(assignee);
        task.setDueDate(request.dueDate());

        task = taskRepository.save(task);
        return toTaskResponse(task);
    }

    public TaskResponse updateTask(UUID taskId, UUID userId, UpdateTaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!canModifyTask(task, userId)) {
            throw new UnauthorizedException("You do not have permission to modify this task");
        }

        if (request.title() != null) {
            task.setTitle(request.title());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.status() != null) {
            task.setStatus(TaskStatus.valueOf(request.status().toUpperCase()));
        }
        if (request.priority() != null) {
            task.setPriority(TaskPriority.valueOf(request.priority().toUpperCase()));
        }
        if (request.assigneeId() != null) {
            User assignee = userRepository.findById(request.assigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            task.setAssignee(assignee);
        }
        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }

        task = taskRepository.save(task);
        return toTaskResponse(task);
    }

    public void deleteTask(UUID taskId, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!canModifyTask(task, userId)) {
            throw new UnauthorizedException("You do not have permission to delete this task");
        }

        taskRepository.delete(task);
    }

    private boolean hasAccess(Project project, UUID userId) {
        if (project.getOwner().getId().equals(userId)) {
            return true;
        }

        return project.getOwner().getId().equals(userId)
                || taskRepository.findByProjectId(project.getId()).stream()
                        .anyMatch(task -> task.getAssignee() != null && task.getAssignee().getId().equals(userId));
    }

    private boolean canModifyTask(Task task, UUID userId) {
        return task.getProject().getOwner().getId().equals(userId);
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