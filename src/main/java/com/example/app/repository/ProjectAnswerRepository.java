package com.example.app.repository;

import com.example.app.dto.answer.AnswerItemDTO;
import com.example.app.entity.ProjectAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectAnswerRepository extends JpaRepository<ProjectAnswer,Long> {

    @NativeQuery(value = "SELECT * FROM project_answers WHERE project_id = ?1 AND form_question_id = ?2")
    Optional<ProjectAnswer> findProjectAnserByProjectAndFormQuestion(long projectId, long formQuestionId);

    List<ProjectAnswer> findByProjectId(Long projectId);
}
