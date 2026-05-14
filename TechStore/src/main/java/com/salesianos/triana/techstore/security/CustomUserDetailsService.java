package com.salesianos.triana.techstore.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/*
 * Servicio que Spring Security llama automáticamente durante el login
 * para cargar los datos del usuario a partir del nombre de usuario
 * introducido en el formulario.
 *
 * Al implementar UserDetailsService y estar anotada con @Service,
 * Spring la detecta automáticamente y la usa en lugar del
 * InMemoryUserDetailsManager que teníamos antes.
 *
 * Si el usuario no existe en la base de datos, lanza
 * UsernameNotFoundException y Spring Security devuelve un error de
 * credenciales inválidas al formulario de login.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No se encontró ningún usuario con el nombre: " + username));
    }
}
