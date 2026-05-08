package com.example.app.entity;

import com.example.app.entity.status.RequirementStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;


@Table
@Entity(name = "project_answers")
@Data
public class ProjectAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "form_question_id")
    private FormQuestion formQuestion;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(
            columnDefinition = "TEXT"
    )
    private String answer;

    @CreationTimestamp // added on insertion
    @Column(
            name = "creation_data",
            updatable = false
    )
    private Date creationData;

    @Enumerated(
            EnumType.STRING
    )
    private RequirementStatus status;
}
