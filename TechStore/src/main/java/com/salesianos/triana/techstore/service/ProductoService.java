package com.salesianos.triana.techstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.salesianos.triana.techstore.exception.GarantiaInvalidaException;
import com.salesianos.triana.techstore.exception.RecursoNoEncontradoException;
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

    public Producto findById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
    }

    public Producto save(Producto producto) {
        if (producto.getGarantiaMeses() > 60) {
            throw new GarantiaInvalidaException("La garantia no puede superar los 60 meses");
        }
        return productoRepository.save(producto);
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
