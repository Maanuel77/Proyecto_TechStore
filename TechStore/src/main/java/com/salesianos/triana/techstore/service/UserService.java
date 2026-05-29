package com.salesianos.triana.techstore.service;

import java.util.NoSuchElementException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.salesianos.triana.techstore.security.Cliente;
import com.salesianos.triana.techstore.security.Usuario;
import com.salesianos.triana.techstore.security.UsuarioRepository;
import com.salesianos.triana.techstore.security.UserRole;
import com.salesianos.triana.techstore.service.base.BaseServiceImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService extends BaseServiceImpl<Usuario, Long, UsuarioRepository> {

    // Longitud mínima para una nueva contraseña. Defensa en profundidad:
    // aunque los controllers validan, también lo hace el service.
    private static final int LONGITUD_MINIMA_PASSWORD = 4;

    private final PasswordEncoder passwordEncoder;

    // Actualiza los datos de perfil (no toca la contraseña ni el rol).
    @Transactional
    public void editarDatos(Long id, String username, String email, String fullname, String telefono) {
        Usuario user = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "No se ha encontrado el usuario con id " + id));
        user.setUsername(username);
        user.setEmail(email);
        user.setFullname(fullname);
        user.setTelefono(telefono);
        repository.save(user);
    }

    // Cambio de contraseña a partir del texto plano. Valida longitud + hashea.
    // Lo usan los flujos directos (registro, scripts, etc.).
    @Transactional
    public void cambiarPassword(Long id, String nuevaPassword) {
        if (nuevaPassword == null || nuevaPassword.length() < LONGITUD_MINIMA_PASSWORD) {
            throw new IllegalArgumentException(
                "La contraseña debe tener al menos " + LONGITUD_MINIMA_PASSWORD + " caracteres.");
        }
        cambiarPasswordHash(id, passwordEncoder.encode(nuevaPassword));
    }

    // Aplica directamente un hash BCrypt ya calculado al usuario. Lo usa el
    // flujo de 2FA: la contraseña se hashea en cuanto el cliente la introduce
    // y SOLO el hash viaja a la sesión hasta confirmar el código por email.
    // Así, si la sesión se filtrara, no se descubriría la contraseña.
    @Transactional
    public void cambiarPasswordHash(Long id, String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Hash de contraseña inválido.");
        }
        Usuario user = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "No se ha encontrado el usuario con id " + id));
        user.setPassword(passwordHash);
        repository.save(user);
    }

    // Hashea una contraseña sin persistir nada. La usa el PerfilController
    // para guardar el hash (no el plain) en la sesión durante el 2FA.
    public String hashear(String passwordEnClaro) {
        if (passwordEnClaro == null || passwordEnClaro.length() < LONGITUD_MINIMA_PASSWORD) {
            throw new IllegalArgumentException(
                "La contraseña debe tener al menos " + LONGITUD_MINIMA_PASSWORD + " caracteres.");
        }
        return passwordEncoder.encode(passwordEnClaro);
    }

    // Comprueba que la contraseña en claro coincide con la guardada (BCrypt).
    @Transactional(readOnly = true)
    public boolean contraseñaActualCoincide(Long id, String passwordEnClaro) {
        return repository.findById(id)
                .map(u -> passwordEncoder.matches(passwordEnClaro, u.getPassword()))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean existeUsername(String username) {
        return repository.existsByUsername(username);
    }

    // Alta desde el formulario público: siempre crea un Cliente (los Admin
    // solo se crean en import.sql o promoviendo un cliente vía toggleRole).
    @Transactional
    public Cliente registrar(String username, String rawPassword,
                              String email, String fullname, String telefono) {
        if (rawPassword == null || rawPassword.length() < LONGITUD_MINIMA_PASSWORD) {
            throw new IllegalArgumentException(
                "La contraseña debe tener al menos " + LONGITUD_MINIMA_PASSWORD + " caracteres.");
        }
        Cliente cliente = Cliente.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .email(email)
                .fullname(fullname)
                .telefono(telefono)
                .build();
        return repository.save(cliente);
    }

    // Alterna ADMIN <-> CLIENTE.
    //
    // OJO con SINGLE_TABLE: JPA no permite cambiar la subclase de una entidad
    // ya persistida (el discriminador es inmutable desde Java). Antes hacíamos
    // delete + save de la otra subclase, pero eso falla con FK violation si
    // el cliente tiene pedidos asociados (la tabla `pedido` referencia su id).
    //
    // Solución: UPDATE nativo de la columna user_type. Modifica solo el tipo
    // y conserva intactos los pedidos del cliente.
    @Transactional
    public void toggleRole(Long id, String usernameSolicitante) {
        Usuario user = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "No se ha encontrado el usuario con id " + id));

        if (user.isSuperadmin()) {
            throw new IllegalStateException("No se puede cambiar el rol del superadmin");
        }
        if (user.getUsername().equals(usernameSolicitante)) {
            throw new IllegalStateException("No puedes cambiar tu propio rol");
        }

        String nuevoTipo = (user.getRole() == UserRole.ADMIN) ? "CLIENTE" : "ADMIN";
        repository.cambiarTipoUsuario(id, nuevoTipo);
    }
}
