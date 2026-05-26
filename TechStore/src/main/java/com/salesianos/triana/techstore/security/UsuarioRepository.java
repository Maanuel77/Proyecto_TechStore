package com.salesianos.triana.techstore.security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio único para Usuario (Admin + Cliente). Spring Data JPA recupera
// la subclase correcta gracias al discriminador (SINGLE_TABLE).
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Lo usa CustomUserDetailsService al hacer login.
    Optional<Usuario> findByUsername(String username);

    // Para validar en el registro que el username no esté pillado.
    boolean existsByUsername(String username);

    // Acceso directo a un Cliente concreto (cuando el contexto lo requiere,
    // p.ej. en /carrito/tramitar para asociar el pedido a una entidad Cliente).
    Optional<Cliente> findClienteByUsername(String username);
}
