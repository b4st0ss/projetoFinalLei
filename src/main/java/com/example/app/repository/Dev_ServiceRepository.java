package com.example.app.repository;

import com.example.app.entity.Dev_Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Dev_ServiceRepository extends JpaRepository<Dev_Service,Long> {

    // To verify if the same dev is on the same service
    @Query(value = "SELECT * FROM dev_services WHERE dev_id = :devID AND service_id = :serviceID", nativeQuery = true)
    Optional<Dev_Service> findByDevAndService(@Param("devID") long dev_id, @Param("serviceID") long service_id);


}
