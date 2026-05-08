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

    private long user_id ;
    private long service_id ;
    private String title ;
    private String message ;

    @CreationTimestamp // added on insertion
    @Column(updatable = false)
    private Date creation_data ;
    private String status;

}
