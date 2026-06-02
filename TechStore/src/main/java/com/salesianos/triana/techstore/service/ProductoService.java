package com.salesianos.triana.techstore.service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.salesianos.triana.techstore.dto.MarcaVentasDto;
import com.salesianos.triana.techstore.dto.ProductoStockDto;
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

    // Stock bajo enriquecido para el dashboard: lista de productos con
    // stock <= umbral, cada uno con sus unidades vendidas en los últimos
    // 30 días. Permite distinguir "crítico" de "tranquilo".
    @Transactional(readOnly = true)
    public List<ProductoStockDto> stockBajoConVentas(int umbral) {
        return repository.findStockBajoConVentas(umbral, LocalDate.now().minusDays(30));
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

    // ---- Soft delete ----
    // En lugar de borrar el producto (que rompería la FK con pedidos históricos),
    // lo marcamos como archivado. Deja de aparecer en el catálogo público y en
    // el listado normal del admin, pero los pedidos antiguos lo siguen viendo
    // correctamente. Es reversible vía `restaurar`.
    @Transactional
    public void archivar(Long id) {
        Producto producto = buscarPorId(id);
        producto.setArchivado(true);
        repository.save(producto);
    }

    // Reactiva un producto previamente archivado: vuelve al catálogo público
    // y al listado normal de productos del admin.
    @Transactional
    public void restaurar(Long id) {
        Producto producto = buscarPorId(id);
        producto.setArchivado(false);
        repository.save(producto);
    }

    // Listado de productos activos (no archivados). Lo usan el catálogo público
    // y el listado normal del admin. Para ver TODOS (incluidos archivados),
    // usa `findAll()` de la clase base. Para ver solo archivados, `findArchivados()`.
    @Transactional(readOnly = true)
    public List<Producto> findActivos() {
        return repository.findByArchivadoFalseOrderByIdAsc();
    }

    // Listado de productos archivados (pestaña "Archivados" del panel admin).
    @Transactional(readOnly = true)
    public List<Producto> findArchivados() {
        return repository.findByArchivadoTrueOrderByIdAsc();
    }

    // Conteo de productos activos para el KPI del dashboard ("productos en venta").
    @Transactional(readOnly = true)
    public long countActivos() {
        return repository.countByArchivadoFalse();
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
