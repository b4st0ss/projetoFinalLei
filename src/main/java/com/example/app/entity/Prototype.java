package com.example.app.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Table
@Entity(name = "prototypes")
@Data

public class Prototype {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int user_id ;
    private int service_id ;
    private String title ;
    private String message ;
    private String image ;
    private Date creation_data ;
    private String status;

}
