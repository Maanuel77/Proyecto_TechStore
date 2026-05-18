package com.salesianos.triana.techstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.salesianos.triana.techstore.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

}
