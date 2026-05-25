package com.salesianos.triana.techstore.security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

    // Lo usa CustomUserDetailsService al hacer login.
    Optional<User> findByUsername(String username);

    // Para validar en el registro que el username no esté pillado.
    boolean existsByUsername(String username);
}
