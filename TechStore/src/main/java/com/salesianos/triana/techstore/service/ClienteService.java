package com.salesianos.triana.techstore.service;

import org.springframework.stereotype.Service;

import com.salesianos.triana.techstore.model.Cliente;
import com.salesianos.triana.techstore.repository.ClienteRepository;
import com.salesianos.triana.techstore.service.base.BaseServiceImpl;

@Service
public class ClienteService extends BaseServiceImpl<Cliente, Long, ClienteRepository> {

}
