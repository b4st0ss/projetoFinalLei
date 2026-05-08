package com.example.app.entity;

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

    private long user_id ;
    private String title ;
    private String description ;

    @CreationTimestamp // added on insertion
    @Column(updatable = false)
    private Date creation_data ;

    private Date lastedit_data ;
    private Boolean status;

}
