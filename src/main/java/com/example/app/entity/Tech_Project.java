package com.example.app.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Table
@Entity(name = "tech_projects")
@Data

public class Tech_Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(
            name = "tech_id",
            nullable = false
    )
    private User tech;

    @ManyToOne
    @JoinColumn(
            name = "admin_id",
            nullable = false
    )
    private User admin;

    @ManyToOne
    @JoinColumn(
            name = "project_id",
            nullable = false
    )
    private Project project;

    @CreationTimestamp // added on insertion
    @Column(
            name = "attribution_data",
            updatable = false
    )
    private Date attributionData ;

}
