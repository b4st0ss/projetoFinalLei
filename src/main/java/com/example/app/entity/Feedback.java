package com.example.app.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Table
@Entity(name = "feedbacks")
@Data

public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int user_id ;
    private int service_id ;
    private String title ;
    private String message ;
    private Date creation_data ;
    private String status;

}
