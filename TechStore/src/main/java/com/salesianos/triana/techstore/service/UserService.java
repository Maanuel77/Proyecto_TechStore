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

    // Actualiza los datos de perfil (no toca la contraseña ni el rol).
    public void editarDatos(Long id, String username, String email, String fullname, String telefono) {
        User user = repository.findById(id).orElseThrow();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullname(fullname);
        user.setTelefono(telefono);
        repository.save(user);
    }

    // La nueva password se codifica con BCrypt antes de persistir.
    public void cambiarPassword(Long id, String nuevaPassword) {
        User user = repository.findById(id).orElseThrow();
        user.setPassword(passwordEncoder.encode(nuevaPassword));
        repository.save(user);
    }


    public boolean existeUsername(String username) {
        return repository.existsByUsername(username);
    }

    // Alta desde el formulario de registro: codifica la contraseña y fuerza
    // rol CLIENTE (los ADMIN solo se crean en el seed o vía toggleRole).
    public User registrar(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.CLIENTE);
        return repository.save(user);
    }

    // Alterna ADMIN <-> CLIENTE.
    // Restricciones: el superadmin es intocable y nadie puede degradarse a sí mismo.
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
