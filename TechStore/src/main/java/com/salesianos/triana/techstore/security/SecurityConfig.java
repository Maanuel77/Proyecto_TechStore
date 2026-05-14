package com.salesianos.triana.techstore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

/*
 * Configuración principal de Spring Security.
 *
 * Define qué rutas son públicas y cuáles requieren autenticación o un rol
 * concreto, cómo es el formulario de login, el logout, y algunos ajustes
 * necesarios para que la consola H2 funcione en desarrollo.
 *
 * NOTA: el bean PasswordEncoder se ha movido a PasswordEncoderConfig
 * y el bean UserDetailsService lo provee automáticamente
 * CustomUserDetailsService al implementar la interfaz y llevar @Service.
 * Mantenerlos aquí causaría dependencias circulares entre beans.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                .requestMatchers("/", "/catalogo", "/auth/login").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            // Conserva la URL original a la que el usuario intentaba acceder
            // antes de ser redirigido al login, y lo lleva allí tras autenticarse.
            .requestCache(cache -> {
                HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
                requestCache.setMatchingRequestParameterName(null);
                cache.requestCache(requestCache);
            })
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            )
            // Permitir acceso a la consola H2 (solo desarrollo)
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
            );

        return http.build();
    }
}
