package com.salesianos.triana.techstore.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
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

    // Si no existe lanzamos NoSuchElementException (la captura el ControllerAdvice
    // global y se transforma en un flash + redirect).
    public Producto buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException());
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
}
