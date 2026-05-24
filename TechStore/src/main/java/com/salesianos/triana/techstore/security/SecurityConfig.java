package com.salesianos.triana.techstore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                .requestMatchers("/", "/catalogo", "/auth/login", "/auth/registro").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // El carrito y el historial de pedidos son exclusivos de clientes:
                // los admins no compran ni tienen pedidos.
                .requestMatchers("/carrito/**", "/pedidos/**").hasRole("CLIENTE")
                .anyRequest().authenticated()
            )
            .requestCache(cache -> {
                HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
                requestCache.setMatchingRequestParameterName(null);
                cache.requestCache(requestCache);
            })
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
            );

        return http.build();
    }
}
