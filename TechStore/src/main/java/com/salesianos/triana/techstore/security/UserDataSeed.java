package com.salesianos.triana.techstore.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserDataSeed {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {

        // Solo insertar si la tabla está vacía (evita duplicados al reiniciar)
        if (userRepository.count() > 0) return;

        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .email("admin@techstore.com")
                .fullname("Administrador TechStore")
                .role(UserRole.ADMIN)
                .build();

        User cliente = User.builder()
                .username("cliente")
                .password(passwordEncoder.encode("cliente123"))
                .email("cliente@techstore.com")
                .fullname("Cliente de Prueba")
                .role(UserRole.CLIENTE)
                .build();

        userRepository.save(admin);
        userRepository.save(cliente);
    }
}
