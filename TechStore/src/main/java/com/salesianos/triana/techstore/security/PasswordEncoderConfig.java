package com.salesianos.triana.techstore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/*
 * Configuración del codificador de contraseñas.
 *
 * Se extrae a una clase separada para evitar dependencias circulares:
 * si el PasswordEncoder y el UserDetailsService estuviesen en la misma
 * clase que SecurityFilterChain, Spring podría tener problemas al
 * resolver las dependencias entre beans.
 *
 * createDelegatingPasswordEncoder() crea un encoder que soporta múltiples
 * algoritmos y almacena el tipo usado como prefijo en el hash: {bcrypt}...
 * Esto facilita migrar el algoritmo en el futuro sin romper contraseñas existentes.
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
