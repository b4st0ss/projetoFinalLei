package com.example.app.repository;

import com.example.app.entity.Dev_Service;
import com.example.app.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service,Long> {

    Optional<Service> findServiceByStatusAndId(String status, long id);

}
