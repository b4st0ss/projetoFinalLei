package com.example.app.entity;

import com.example.app.entity.status.ProjectStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Table
@Entity(name = "projects")
@Data
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(
            name = "client_id",
            nullable = false
    )
    private User client;

    private String title ;
    private String description ;

    @CreationTimestamp // added on insertion
    @Column(
            name = "creation_data",
            updatable = false
    )
    private Date creationData ;

    @UpdateTimestamp
    @Column(
            name = "conclusion_data"
    )
    private Date conclusionData ;

    @Enumerated(
            EnumType.STRING
    )
    private ProjectStatus status;

}
