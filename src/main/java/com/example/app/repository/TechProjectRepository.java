package com.example.app.repository;

import com.example.app.entity.Tech_Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;

import java.util.List;
import java.util.Optional;

public interface TechProjectRepository extends JpaRepository<Tech_Project,Long>{

    List<Tech_Project> findByProjectId(long projectId);

    @Modifying
    @NativeQuery(value = "DELETE FROM tech_projects " +
                        "WHERE project_id =?1 AND tech_id=?2")
    void deleteByProjectIdAndTechId(long projectId, long techId);

    Optional<Tech_Project> findByProjectIdAndTechId(long projectId, long techId);

}
