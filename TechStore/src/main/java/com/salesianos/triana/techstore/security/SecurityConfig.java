package com.salesianos.triana.techstore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // Reglas de autorización por URL. Los admins NO acceden al carrito
            // ni al historial de cliente (son funcionalidad exclusiva CLIENTE).
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                // /auth/login y /auth/verificar* son públicos: el primero es la pantalla
                // de credenciales; el segundo es el paso 2FA antes de autenticar.
                .requestMatchers("/", "/catalogo", "/auth/login",
                                 "/auth/registro", "/auth/verificar/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/carrito/**", "/pedidos/**").hasRole("CLIENTE")
                .requestMatchers("/perfil/**").authenticated()
                // Catch-all permitAll (antes era .authenticated()): así las URLs
                // que no matchean ningún controlador llegan al dispatcher de error
                // de Spring Boot y muestran templates/error/404.html en lugar de
                // un 403 vacío. Los endpoints sensibles SIEMPRE deben declararse
                // explícitamente arriba (hasRole / authenticated).
                .anyRequest().permitAll()
            )
            .requestCache(cache -> {
                HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
                requestCache.setMatchingRequestParameterName(null);
                cache.requestCache(requestCache);
            })
            // El login NO usa el formLogin estándar de Spring Security:
            // lo gestionamos en AuthController para meter el paso 2FA por email
            // a los CLIENTE antes de marcarles como autenticados.
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            // En lugar del Whitelabel de 403, redirige con flash message.
            .exceptionHandling(ex -> ex
                .accessDeniedHandler(customAccessDeniedHandler)
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            // sameOrigin (no disable) -> protege contra clickjacking pero
            // permite que la consola H2, que vive en el mismo origen, use
            // su propio frame interno. Disable() abriria la app a iframes
            // de cualquier sitio externo.
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }
}
