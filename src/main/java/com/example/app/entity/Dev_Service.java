package com.example.app.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Table
@Entity(name = "dev_services")
@Data

public class Dev_Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int dev_id ;
    private int admin_id ;
    private int service_id ;
    private Date attribution_data ;

}
