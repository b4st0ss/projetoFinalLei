package com.example.app.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Table
@Entity(name = "feedbacks")
@Data

public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @ManyToOne
    @JoinColumn(
            name = "project_answer_id",
            nullable = true
    )
    private ProjectAnswer projectAnswer;

    @ManyToOne
    @JoinColumn(
            name = "requirement_id",
            nullable = true
    )
    private Requirement requirement;

    @Column(
            columnDefinition = "TEXT",
            nullable = false
    )
    private String message ;

    @CreationTimestamp // added on insertion
    @Column(updatable = false)
    private Date creation_data ;

    @Column(
            name = "is_read",
            nullable = false
    )
    private boolean isRead = false;
}
