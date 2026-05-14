package com.salesianos.triana.techstore.security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/*
 * Repositorio de la entidad User.
 *
 * Hereda todos los métodos CRUD de JpaRepository y añade
 * findByUsername, que es el método que Spring Security
 * necesita para cargar un usuario durante el proceso de login.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
