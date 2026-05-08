package com.example.app.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Table
@Entity(name = "dev_services")
@Data

public class Dev_Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long dev_id ;
    private long admin_id ;
    private long service_id ;

    @CreationTimestamp // added on insertion
    @Column(updatable = false)
    private Date attribution_data ;

}
