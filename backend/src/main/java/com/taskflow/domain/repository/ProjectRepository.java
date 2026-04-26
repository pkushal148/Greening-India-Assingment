package com.taskflow.domain.repository;

import com.taskflow.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN Task t ON p.id = t.project.id " +
            "WHERE p.owner.id = :userId OR t.assignee.id = :userId")
    List<Project> findProjectsForUser(UUID userId);
}