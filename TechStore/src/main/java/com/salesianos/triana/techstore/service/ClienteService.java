package com.salesianos.triana.techstore.service;

import org.springframework.stereotype.Service;

import com.salesianos.triana.techstore.model.Cliente;
import com.salesianos.triana.techstore.repository.ClienteRepository;
import com.salesianos.triana.techstore.security.User;
import com.salesianos.triana.techstore.service.base.BaseServiceImpl;

@Service
public class ClienteService extends BaseServiceImpl<Cliente, Long, ClienteRepository> {

    /**
     * Busca el Cliente del dominio asociado al usuario logueado por su email.
     * Si todavía no existe (es la primera compra del usuario), lo crea con
     * los datos de su cuenta (fullname, email, telefono).
     */
    public Cliente findOrCreateForUser(User user) {
        return repository.findByEmail(user.getEmail())
                .orElseGet(() -> repository.save(Cliente.builder()
                        .nombre(user.getFullname())
                        .email(user.getEmail())
                        .telefono(user.getTelefono())
                        .build()));
    }
}
