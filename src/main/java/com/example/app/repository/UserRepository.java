package com.example.app.repository;

import com.example.app.entity.Project;
import com.example.app.entity.User;
import com.example.app.entity.status.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    List<User> findUserByStatusIs(Boolean status);

    List<User> findByStatusTrueAndRole (UserRoles role);
}