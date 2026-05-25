package com.salesianos.triana.techstore.service;

import org.springframework.stereotype.Service;

import com.salesianos.triana.techstore.model.Cliente;
import com.salesianos.triana.techstore.repository.ClienteRepository;
import com.salesianos.triana.techstore.security.User;
import com.salesianos.triana.techstore.service.base.BaseServiceImpl;

@Service
public class ClienteService extends BaseServiceImpl<Cliente, Long, ClienteRepository> {

    // Puente entre el User de seguridad y el Cliente del dominio: se enlazan
    // por email. La primera vez que un usuario compra se crea su Cliente.
    public Cliente findOrCreateForUser(User user) {
        return repository.findByEmail(user.getEmail())
                .orElseGet(() -> repository.save(Cliente.builder()
                        .nombre(user.getFullname())
                        .email(user.getEmail())
                        .telefono(user.getTelefono())
                        .build()));
    }
}
