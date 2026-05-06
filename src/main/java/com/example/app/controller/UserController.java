package com.example.app.controller;

import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserRepository repository;


    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<String> getUser(){
        return ResponseEntity.ok("sucess!");
    }

//    @GetMapping
//    public List<User> list() {
//        return repository.findAll();
//    }
}