package com.salesianos.triana.techstore.security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // Cambia in-place la subclase del usuario (Admin <-> Cliente) modificando
    // sólo la columna discriminadora. SINGLE_TABLE no permite mutarla con JPA,
    // así que usamos SQL nativo. Como Admin tiene `superadmin` y Cliente no,
    // forzamos FALSE para evitar dejar el flag colgando al promover/degradar.
    //
    // flushAutomatically=true sincroniza pendientes antes del UPDATE.
    // clearAutomatically=true vacía el caché de primer nivel después, así la
    // próxima carga devuelve la subclase correcta y no el proxy obsoleto.
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE usuarios SET user_type = :tipo, superadmin = FALSE WHERE id = :id",
           nativeQuery = true)
    void cambiarTipoUsuario(@Param("id") Long id, @Param("tipo") String tipo);
}
