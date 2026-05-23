package com.salesianos.triana.techstore.security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findFirstByUsername(String username);
    
    // No sé si usar findByUsername().isPresent()
    boolean existsByUsername(String username);
}
