package com.salesianos.triana.techstore.cupon;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CuponRepository extends JpaRepository<Cupon, Long> {

    // Búsqueda por código (case-insensitive) para el carrito al validar.
    Optional<Cupon> findByCodigoIgnoreCase(String codigo);

    // Cupón de fidelidad asignado a un cliente concreto (0 o 1).
    Optional<Cupon> findByClienteIdAndTipo(Long clienteId, TipoCupon tipo);

    // Listado de cupones públicos para la pantalla del admin.
    List<Cupon> findByTipoOrderByCodigoAsc(TipoCupon tipo);
}
