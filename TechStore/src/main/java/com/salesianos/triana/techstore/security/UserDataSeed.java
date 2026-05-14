package com.salesianos.triana.techstore.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/*
 * Componente que carga los usuarios iniciales en la base de datos
 * al arrancar la aplicación.
 *
 * @PostConstruct garantiza que este método se ejecuta una sola vez,
 * justo después de que Spring haya inyectado todas las dependencias,
 * pero antes de que la aplicación empiece a recibir peticiones.
 *
 * Las contraseñas se codifican con el PasswordEncoder configurado,
 * nunca se guardan en texto plano.
 */
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
