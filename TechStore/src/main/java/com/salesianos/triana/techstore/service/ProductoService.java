package com.salesianos.triana.techstore.service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.salesianos.triana.techstore.dto.MarcaVentasDto;
import com.salesianos.triana.techstore.dto.ProductoTopDto;
import com.salesianos.triana.techstore.dto.ProductoVentasDto;
import com.salesianos.triana.techstore.model.Producto;
import com.salesianos.triana.techstore.repository.ProductoRepository;
import com.salesianos.triana.techstore.service.base.BaseServiceImpl;

@Service
public class ProductoService extends BaseServiceImpl<Producto, Long, ProductoRepository> {

    @Transactional(readOnly = true)
    public List<Producto> lowStock() {
        return repository.findByLowAvailability();
    }

    @Transactional(readOnly = true)
    public List<ProductoVentasDto> findMasVendidos() {
        return repository.findMasVendidos();
    }

    @Transactional(readOnly = true)
    public List<MarcaVentasDto> findVentasPorMarca() {
        return repository.findVentasPorMarca();
    }

    // Si no existe lanzamos NoSuchElementException con un mensaje claro
    // (la captura el ControllerAdvice global y se convierte en flash + redirect).
    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "No se ha encontrado el producto con id " + id));
    }

    @Override
    public Producto save(Producto producto) {
        // Regla de negocio: no permitimos productos por encima de 50.000 €.
        if (producto.getPrecio() != null && producto.getPrecio() > 50000.0) {
            throw new IllegalArgumentException(
                "Por política de la tienda, no se permiten productos de más de 50.000 €.");
        }
        return super.save(producto);
    }

    // Borrar producto. Si está referenciado en pedidos antiguos la BD lanza
    // DataIntegrityViolationException (FK), que convertimos en un mensaje
    // amigable. flush() fuerza el DELETE inmediato para capturarlo aquí.
    @Transactional
    public void eliminar(Long id) {
        Producto producto = buscarPorId(id);
        try {
            repository.delete(producto);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(
                "No se puede eliminar el producto '" + producto.getNombre()
                + "' porque está incluido en pedidos históricos.");
        }
    }

    @Transactional(readOnly = true)
    public List<ProductoTopDto> findTopVendidos(int limit) {
        return repository.findTopVendidos(PageRequest.of(0, limit));
    }

    @Transactional(readOnly = true)
    public List<ProductoTopDto> findTopVendidosBetween(LocalDate desde, LocalDate hasta, int limit) {
        return repository.findTopVendidosBetween(desde, hasta, PageRequest.of(0, limit));
    }
}
