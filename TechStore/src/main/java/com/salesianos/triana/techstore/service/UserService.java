package com.salesianos.triana.techstore.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.salesianos.triana.techstore.security.User;
import com.salesianos.triana.techstore.security.UserRepository;
import com.salesianos.triana.techstore.security.UserRole;
import com.salesianos.triana.techstore.service.base.BaseServiceImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService extends BaseServiceImpl<User, Long, UserRepository> {

    private final PasswordEncoder passwordEncoder;

    public void editarDatos(Long id, String username, String email, String fullname, String telefono) {
        User user = repository.findById(id).orElseThrow();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullname(fullname);
        user.setTelefono(telefono);
        repository.save(user);
    }

    public void cambiarPassword(Long id, String nuevaPassword) {
        User user = repository.findById(id).orElseThrow();
        user.setPassword(passwordEncoder.encode(nuevaPassword));
        repository.save(user);
    }


    public boolean existeUsername(String username) {
        return repository.existsByUsername(username);
    }

    /**
     * Registra un nuevo usuario con rol CLIENTE por defecto.
     * Codifica la contraseña antes de guardarla.
     */
    public User registrar(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.CLIENTE);
        return repository.save(user);
    }

    /**
     * Cambia el rol del usuario: si es CLIENTE pasa a ADMIN y viceversa.
     *
     * Verificaciones de seguridad:
     *   - No se puede cambiar el rol del superadmin (es inmutable).
     *   - Un admin no puede cambiarse el rol a sí mismo (evita autodegradarse).
     *
     * @param id                identificador del usuario al que se le cambia el rol
     * @param usernameSolicitante username del admin que ejecuta la acción
     * @throws IllegalStateException si la operación viola alguna regla de seguridad
     */
    public void toggleRole(Long id, String usernameSolicitante) {
        User user = repository.findById(id).orElseThrow();

        if (user.isSuperadmin()) {
            throw new IllegalStateException("No se puede cambiar el rol del superadmin");
        }

        if (user.getUsername().equals(usernameSolicitante)) {
            throw new IllegalStateException("No puedes cambiar tu propio rol");
        }

        user.setRole(user.getRole() == UserRole.ADMIN ? UserRole.CLIENTE : UserRole.ADMIN);
        repository.save(user);
    }
}
