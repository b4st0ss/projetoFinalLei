package com.example.app.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Table
@Entity(name = "services")
@Data

public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int user_id ;
    private String title ;
    private String description ;
    private Date submission_data ;
    private Date conclusion_data ;
    private String status;

}
