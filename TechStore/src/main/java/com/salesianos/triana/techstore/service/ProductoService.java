package com.salesianos.triana.techstore.service;

import java.util.List;
import java.util.NoSuchElementException;

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

    /**
     * Devuelve el producto con ese id o lanza {@link NoSuchElementException}
     * si no existe. La excepción es capturada por el ControllerAdvice global
     * que la transforma en una página de error amigable.
     */
    public Producto buscarPorId(Long id) {
        // Si no se encuentra, lanza una excepción nativa de Java (NoSuchElementException)
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException());
    }

    @Override
    public Producto save(Producto producto) {
        // Uso de excepción del API de Java (uso preventivo)
        if (producto.getPrecio() != null && producto.getPrecio() > 50000.0) {
            throw new IllegalArgumentException(
                "Por política de la tienda, no se permiten productos de más de 50.000 €.");
        }
        return super.save(producto);
    }
}
