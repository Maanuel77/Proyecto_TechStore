package com.salesianos.triana.techstore.security;

import java.util.List;

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

        if (userRepository.count() > 0) return;

        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .email("admin@techstore.com")
                .fullname("Administrador TechStore")
                .telefono("600000001")
                .role(UserRole.ADMIN)
                .superadmin(true)
                .build();
        
        User cliente = User.builder()
                .username("cliente")
                .password(passwordEncoder.encode("cliente123"))
                .email("cliente@techstore.com")
                .fullname("Cliente de Prueba")
                .telefono("600000002")
                .role(UserRole.CLIENTE)
                .build();
        
        User clienteSara = User.builder()
                .username("sara.ruiz")
                .password(passwordEncoder.encode("sara123"))
                .email("sara.ruiz@techstore.com")
                .fullname("Sara Ruiz")
                .telefono("611000001")
                .role(UserRole.CLIENTE)
                .build();

        

        User clienteLucia = User.builder()
                .username("lucia.garcia")
                .password(passwordEncoder.encode("lucia123"))
                .email("lucia.garcia@example.com")
                .fullname("Lucía García")
                .telefono("611000002")
                .role(UserRole.CLIENTE)
                .build();

        User clienteJavier = User.builder()
                .username("javier.romero")
                .password(passwordEncoder.encode("javier123"))
                .email("javier.romero@example.com")
                .fullname("Javier Romero")
                .telefono("611000003")
                .role(UserRole.CLIENTE)
                .build();

        User clienteMarta = User.builder()
                .username("marta.sanchez")
                .password(passwordEncoder.encode("marta123"))
                .email("marta.sanchez@example.com")
                .fullname("Marta Sánchez")
                .telefono("611000004")
                .role(UserRole.CLIENTE)
                .build();

        User clienteCarlos = User.builder()
                .username("carlos.fernandez")
                .password(passwordEncoder.encode("carlos123"))
                .email("carlos.fernandez@example.com")
                .fullname("Carlos Fernández")
                .telefono("611000005")
                .role(UserRole.CLIENTE)
                .build();

        User clienteAna = User.builder()
                .username("ana.lopez")
                .password(passwordEncoder.encode("ana123"))
                .email("ana.lopez@example.com")
                .fullname("Ana López")
                .telefono("611000006")
                .role(UserRole.CLIENTE)
                .build();

        userRepository.saveAll(List.of(
                admin, cliente,
                clienteSara, clienteLucia, clienteJavier, clienteMarta, clienteCarlos, clienteAna
        ));
    }
}
