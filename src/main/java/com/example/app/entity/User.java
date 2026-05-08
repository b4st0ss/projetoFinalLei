package com.example.app.entity;

import com.example.app.entity.status.UserRoles;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;


@Table
@Entity(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name ;

    @Column(
            nullable = false,
            unique = true
    )
    private String username ;

    @Column(
            nullable = false
    )
    private String password ;

    @Column(
            nullable = false,
            unique = true
    )
    private String email ;

    @Column(
            nullable = false
    )
    @Enumerated(EnumType.STRING)
    private UserRoles role ;

    @CreationTimestamp // added on insertion
    @Column(
            name = "creation_data",
            updatable = false
    )
    private Date creationData ;

    @UpdateTimestamp
    @Column(
            name = "lastedit_data"
    )
    private Date lasteditData ;

    private String contact ;
    private String address ;
    private Boolean status;

//    public User(String username, String password, String email, String role, Boolean status) {
//        this.username = username;
//        this.password = password;
//        this.email = email;
//        this.role = role;
//        this.status = status;
//    }

}
