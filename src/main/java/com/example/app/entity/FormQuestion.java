package com.example.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;


@Table
@Entity(name = "form_questions")
@Data
public class FormQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String question;

    @CreationTimestamp // added on insertion
    @Column(
            name = "creation_data",
            updatable = false
    )
    private Date creationData;

    private Boolean selected;
}
