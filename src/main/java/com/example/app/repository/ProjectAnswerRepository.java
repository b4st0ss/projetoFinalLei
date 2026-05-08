package com.example.app.repository;

import com.example.app.entity.Project;
import com.example.app.entity.ProjectAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectAnswerRepository extends JpaRepository<ProjectAnswer,Long> {
}
