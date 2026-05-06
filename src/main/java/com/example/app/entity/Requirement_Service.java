package com.example.app.entity;


import jakarta.persistence.*;
import lombok.Data;

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
    private Date attribution_data ;

}
