package com.example.app.entity;

import com.example.app.entity.status.RequirementStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Table
@Entity(name = "requirements")
@Data

public class Requirement {

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
            name = "project_id",
            nullable = false
    )
    private Project project;

    private String title ;

    @Column(
            columnDefinition = "TEXT"
    )
    private String description ;

    @CreationTimestamp // added on insertion
    @Column(
            name = "creation_data",
            updatable = false)
    private Date creationData ;

    @Enumerated(
            EnumType.STRING
    )
    private RequirementStatus status;
}
