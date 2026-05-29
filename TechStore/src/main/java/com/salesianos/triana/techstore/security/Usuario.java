package com.salesianos.triana.techstore.security;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.ToString;

// Clase base de los usuarios (Admin y Cliente). SINGLE_TABLE: una sola tabla
// `usuarios` con la columna discriminadora `user_type` para distinguir el tipo.
// @SuperBuilder se necesita para que el patrón builder funcione con herencia.
@SuppressWarnings("serial")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// columnDefinition fuerza VARCHAR en lugar del tipo ENUM nativo de H2 que
// Hibernate genera por defecto. Sin esto, el UPDATE nativo de cambiarTipoUsuario
// (UsuarioRepository) falla con "check constraint invalid" al intentar
// asignar un String a una columna ENUM.
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING,
                     columnDefinition = "varchar(20)")
public abstract class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 20, message = "Debe tener entre 3 y 20 caracteres")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String password;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Introduce un email válido (ejemplo@dominio.com)")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String fullname;

    @Pattern(regexp = "^$|^[0-9]{9}$",
             message = "El teléfono debe tener 9 dígitos numéricos")
    private String telefono;

    // Devuelve el rol de la subclase concreta. Implementado en Admin y Cliente.
    // Los templates pueden usar `usuario.role.name()` como antes.
    public abstract UserRole getRole();

    // Solo el Admin puede ser superadmin. Cliente devuelve siempre false.
    public boolean isSuperadmin() {
        return false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getRole().name()));
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy hp
                ? hp.getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hp
                ? hp.getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Usuario that = (Usuario) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hp
                ? hp.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
