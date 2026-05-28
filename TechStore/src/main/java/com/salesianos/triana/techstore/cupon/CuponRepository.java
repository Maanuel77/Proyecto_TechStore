package com.salesianos.triana.techstore.cupon;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CuponRepository extends JpaRepository<Cupon, Long> {

    // Búsqueda por código (case-insensitive) para el carrito al validar.
    Optional<Cupon> findByCodigoIgnoreCase(String codigo);

    // Cupón de fidelidad ACTIVO del cliente (el "disponible para usar" ahora mismo).
    // Como los FIDELIDAD son single-use, solo puede haber 1 activo a la vez.
    Optional<Cupon> findByClienteIdAndTipoAndActivoTrue(Long clienteId, TipoCupon tipo);

    // Cuántos cupones FIDELIDAD ha tenido este cliente HISTÓRICAMENTE (activos o ya
    // consumidos). Lo usamos para el umbral acumulativo: el N-ésimo cupón requiere
    // haber gastado N · umbral.
    long countByClienteIdAndTipo(Long clienteId, TipoCupon tipo);

    // Listado de cupones públicos para la pantalla del admin.
    List<Cupon> findByTipoOrderByCodigoAsc(TipoCupon tipo);
}
