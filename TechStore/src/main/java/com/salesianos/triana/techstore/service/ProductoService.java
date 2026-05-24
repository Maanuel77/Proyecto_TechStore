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

    /**
     * Elimina un producto por su id.
     *
     * Si el producto no existe lanza {@link NoSuchElementException}.
     * Si el producto está referenciado en líneas de pedidos históricos,
     * la base de datos lanza una violación de FK que aquí transformamos
     * en {@link IllegalArgumentException} con un mensaje user-friendly.
     * Ambas excepciones las captura el ExceptionControllerAdvice global.
     */
    public void eliminar(Long id) {
        Producto producto = buscarPorId(id);
        try {
            repository.delete(producto);
            // Forzamos el DELETE inmediato para que la FK falle aquí
            // y no más tarde al cerrarse la transacción.
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(
                "No se puede eliminar el producto '" + producto.getNombre()
                + "' porque está incluido en pedidos históricos.");
        }
    }
}
