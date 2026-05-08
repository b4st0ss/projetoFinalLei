package com.example.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Table
@Entity(name = "services")
@Data
@Getter
@Setter
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long user_id ;
    private String title ;
    private String description ;

    @CreationTimestamp // added on insertion
    @Column(updatable = false)
    private Date submission_data ;

    private Date conclusion_data ;
    private String status;

}
