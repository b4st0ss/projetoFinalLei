package com.example.app.repository;

import com.example.app.entity.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement,Long> {


    @NativeQuery(value = "SELECT * FROM requirements WHERE project_id = ?1 AND title = ?2")
    Optional<Requirement> findByProjectIdAndTitle(long projectId, String title);


    List<Requirement> findByProjectId(long projectId);

}
