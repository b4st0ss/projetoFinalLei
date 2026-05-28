package com.example.app.repository;

import com.example.app.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback,Long> {

    @NativeQuery("SELECT * FROM feedbacks " +
                 "WHERE project_answer_id=?1 " +
                 "ORDER BY id ASC")
    List<Feedback> findFeedbackByAnswerId(long answerId);

    @NativeQuery("SELECT * FROM feedbacks " +
                 "WHERE requirement_id =?1 " +
                 "ORDER BY id ASC")
    List<Feedback> findFeedbackByRequirementId(long requirementId);

}
