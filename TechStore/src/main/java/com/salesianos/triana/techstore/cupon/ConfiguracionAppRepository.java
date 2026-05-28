package com.salesianos.triana.techstore.cupon;

import org.springframework.data.jpa.repository.JpaRepository;

// Solo hay una fila (id = 1). Las consultas siempre van por findById(1L).
public interface ConfiguracionAppRepository extends JpaRepository<ConfiguracionApp, Long> {
}
