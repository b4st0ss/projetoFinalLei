package com.example.app.repository;

import com.example.app.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {

    @NativeQuery(value = "SELECT * FROM projects WHERE client_id = ?1 AND status!='COMPLETED'")
    List<Project> findByClientId(long id);

    @NativeQuery(value = "SELECT * FROM projects WHERE status!='COMPLETED'")
    List<Project> findNotCompleted();

    @NativeQuery(value = "SELECT projects.* FROM projects " +
                         "JOIN tech_projects ON projects.id = tech_projects.project_id " +
                         "WHERE tech_projects.tech_id = ?1 AND projects.status != 'COMPLETED'")
    List<Project> findByTechProjectId(long id);

}

