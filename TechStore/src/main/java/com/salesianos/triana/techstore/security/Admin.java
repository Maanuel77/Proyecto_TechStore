package com.salesianos.triana.techstore.security;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

// Administrador del sistema. Solo añade el flag superadmin (intocable).
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends Usuario {

    private boolean superadmin;

    @Override
    public UserRole getRole() {
        return UserRole.ADMIN;
    }

    @Override
    public boolean isSuperadmin() {
        return superadmin;
    }
}
