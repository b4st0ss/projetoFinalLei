package com.example.app.repository;

import com.example.app.entity.FormQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormQuestionRepository extends JpaRepository<FormQuestion,Long> {
    @NativeQuery(value = "SELECT * FROM form_questions WHERE selected=TRUE")
    List<FormQuestion> findSelected();
}
