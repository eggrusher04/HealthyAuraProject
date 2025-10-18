package com.FeedEmGreens.HealthyAura.repository;

import com.FeedEmGreens.HealthyAura.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


//Repository is used to handle direct access to the database and talks to the entities
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
    boolean existsByUsername(String username);
}
