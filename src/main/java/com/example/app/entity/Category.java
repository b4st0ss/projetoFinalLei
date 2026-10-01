package com.example.app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Table
@Entity(name = "category")
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    private String name;

    private int position;
}