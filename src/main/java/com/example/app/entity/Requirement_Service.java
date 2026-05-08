package com.example.app.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Table
@Entity(name = "requirements_services")
@Data

public class Requirement_Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long requirement_id ;
    private long service_id ;

    @CreationTimestamp // added on insertion
    @Column(updatable = false)
    private Date attribution_data ;

    private boolean status;

}
