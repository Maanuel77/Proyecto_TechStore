package com.salesianos.triana.techstore.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Entidad que representa a un usuario de la aplicación.
 *
 * Implementa UserDetails, la interfaz que Spring Security requiere
 * para conocer los datos de autenticación y autorización del usuario.
 * Al implementarla, esta clase se convierte en el objeto que Spring
 * Security maneja internamente durante toda la sesión.
 *
 * Los métodos isAccountNonExpired, isAccountNonLocked, isCredentialsNonExpired
 * e isEnabled están implícitamente activados (devuelven true por defecto
 * en la interfaz desde Spring Security 6), por lo que no es necesario
 * sobreescribirlos salvo que necesitemos lógica específica.
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;
    private String email;
    private String fullname;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    /*
     * Devuelve los permisos/roles del usuario.
     * Spring Security espera el prefijo "ROLE_", por eso lo concatenamos
     * con el nombre del enum (ADMIN → ROLE_ADMIN, CLIENTE → ROLE_CLIENTE).
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
