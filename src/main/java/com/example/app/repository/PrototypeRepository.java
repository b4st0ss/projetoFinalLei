package com.example.app.repository;

import com.example.app.entity.Prototype;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrototypeRepository extends JpaRepository<Prototype,Long> { }
