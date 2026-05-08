package com.salesianos.triana.techstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.salesianos.triana.techstore.model.Producto;
import com.salesianos.triana.techstore.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public void delete(Long id) {
        productoRepository.deleteById(id);
    }

    public List<Producto> lowStock() {
        return productoRepository.findByLowAvailability();
    }

    public List<Object[]> findMasVendidos() {
        return productoRepository.findMasVendidos();
    }

    public List<Object[]> findVentasPorMarca() {
        return productoRepository.findVentasPorMarca();
    }
}
