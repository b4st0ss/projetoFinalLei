package com.example.app.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Table
@Entity(name = "requirements")
@Data

public class Requirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int user_id ;
    private String title ;
    private String description ;
    private Date creation_data ;
    private Date lastedit_data ;

}
