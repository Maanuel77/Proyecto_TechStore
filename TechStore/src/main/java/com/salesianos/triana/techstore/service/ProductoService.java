package com.salesianos.triana.techstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.salesianos.triana.techstore.model.Producto;
import com.salesianos.triana.techstore.repository.ProductoRepository;
import com.salesianos.triana.techstore.service.base.BaseServiceImpl;

@Service
public class ProductoService extends BaseServiceImpl<Producto, Long, ProductoRepository> {

    public List<Producto> lowStock() {
        return repository.findByLowAvailability();
    }

    public List<Object[]> findMasVendidos() {
        return repository.findMasVendidos();
    }

    public List<Object[]> findVentasPorMarca() {
        return repository.findVentasPorMarca();
    }
}
