package com.salesianos.triana.techstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.salesianos.triana.techstore.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Usado por ClienteService para enlazar User (security) con Cliente (dominio).
    Optional<Cliente> findByEmail(String email);
}
