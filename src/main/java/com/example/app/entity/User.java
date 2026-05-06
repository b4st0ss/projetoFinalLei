package com.example.app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;


@Table
@Entity(name = "users")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name ;

    @Column(
            name = "username",
            nullable = false,
            unique = true
    )
    private String username ;
    private String password ;

    @Column(
            name = "email",
            nullable = false,
            unique = true
    )
    private String email ;

    @Column(
            name = "role",
            nullable = false
    )
    private String role ;

    @CreationTimestamp // Adiciona a data automaticamente no INSERT
    @Column(updatable = false)
    private Date creation_data ;

    @UpdateTimestamp
    private Date lastedit_data ;
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
