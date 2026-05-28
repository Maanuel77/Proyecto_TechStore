package com.salesianos.triana.techstore.cupon;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.salesianos.triana.techstore.repository.PedidoRepository;
import com.salesianos.triana.techstore.security.Cliente;

import lombok.RequiredArgsConstructor;

// Lógica de cupones públicos (CRUD) y de fidelidad (asignación automática
// según umbral). Centraliza también la validación al aplicar en el carrito.
@Service
@RequiredArgsConstructor
public class CuponService {

    private final CuponRepository cuponRepository;
    private final ConfiguracionAppRepository configRepository;
    private final PedidoRepository pedidoRepository;

    //Configuración global

    @Transactional(readOnly = true)
    public ConfiguracionApp getConfiguracion() {
        return configRepository.findById(1L).orElseThrow(() -> new IllegalStateException(
                "No existe la fila de configuración (id=1). Revisa import.sql."));
    }

    @Transactional
    public void actualizarConfiguracion(double umbral, double descuento) {
        if (umbral < 0) {
            throw new IllegalArgumentException("El umbral no puede ser negativo.");
        }
        if (descuento < 0 || descuento > 1) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 1.");
        }
        ConfiguracionApp c = getConfiguracion();
        c.setUmbralFidelidad(umbral);
        c.setDescuentoFidelidad(descuento);
        configRepository.save(c);
    }

    //Cupones públicos (admin)

    @Transactional(readOnly = true)
    public List<Cupon> findPublicos() {
        return cuponRepository.findByTipoOrderByCodigoAsc(TipoCupon.PUBLICO);
    }

    @Transactional
    public Cupon crearCuponPublico(String codigo, double descuento) {
        String limpio = codigo == null ? "" : codigo.trim();
        if (limpio.isEmpty()) {
            throw new IllegalArgumentException("El código del cupón no puede estar vacío.");
        }
        if (descuento <= 0 || descuento > 1) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 1 (excl. 0).");
        }
        if (cuponRepository.findByCodigoIgnoreCase(limpio).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cupón con ese código.");
        }
        Cupon c = Cupon.builder()
                .codigo(limpio.toUpperCase())
                .descuento(descuento)
                .tipo(TipoCupon.PUBLICO)
                .activo(true)
                .build();
        return cuponRepository.save(c);
    }

    @Transactional
    public void toggleActivo(Long id) {
        Cupon c = cuponRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("No existe el cupón con id " + id));
        c.setActivo(!c.isActivo());
        cuponRepository.save(c);
    }

    @Transactional
    public void eliminar(Long id) {
        Cupon c = cuponRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("No existe el cupón con id " + id));
        if (c.getTipo() == TipoCupon.FIDELIDAD) {
            throw new IllegalArgumentException(
                "No se pueden borrar cupones de fidelidad desde aquí: los gestiona el sistema.");
        }
        cuponRepository.delete(c);
    }

    // Lógica de fidelidad 
    
    // Cupón de fidelidad asignado al cliente (si ya tiene uno).
    @Transactional(readOnly = true)
    public Optional<Cupon> getCuponFidelidad(Cliente cliente) {
        if (cliente == null) return Optional.empty();
        return cuponRepository.findByClienteIdAndTipo(cliente.getId(), TipoCupon.FIDELIDAD);
    }

    // Si el cliente cumple el umbral y no tiene cupón de fidelidad, se lo
    // crea con código aleatorio y descuento de la configuración actual.
    // Devuelve el cupón resultante (el nuevo o el ya existente) o vacío si
    // todavía no cumple el umbral.
    @Transactional
    public Optional<Cupon> asignarFidelidadSiProcede(Cliente cliente) {
        if (cliente == null) return Optional.empty();

        // ¿Ya tiene cupón de fidelidad? Devolver el existente.
        Optional<Cupon> existente = getCuponFidelidad(cliente);
        if (existente.isPresent()) return existente;

        // ¿Cumple el umbral?
        ConfiguracionApp cfg = getConfiguracion();
        Double gasto = pedidoRepository.sumGastoByClienteId(cliente.getId());
        double gastoActual = gasto != null ? gasto : 0.0;
        if (gastoActual < cfg.getUmbralFidelidad()) {
            return Optional.empty();
        }

        // Generar cupón nuevo con código aleatorio único.
        String codigo;
        do {
            codigo = "FID-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (cuponRepository.findByCodigoIgnoreCase(codigo).isPresent());

        Cupon nuevo = Cupon.builder()
                .codigo(codigo)
                .descuento(cfg.getDescuentoFidelidad())
                .tipo(TipoCupon.FIDELIDAD)
                .cliente(cliente)
                .activo(true)
                .build();
        return Optional.of(cuponRepository.save(nuevo));
    }

    // Validación al aplicar en el carrito

    // Busca el cupón por código y comprueba que es aplicable al cliente.
    //   - PUBLICO activo → vale para cualquiera.
    //   - FIDELIDAD → solo si pertenece al cliente Y cumple el umbral actual.
    // Si no procede, devuelve Optional vacío.
    @Transactional(readOnly = true)
    public Optional<Cupon> validar(String codigo, Cliente cliente) {
        if (codigo == null || codigo.isBlank()) return Optional.empty();

        Optional<Cupon> encontrado = cuponRepository.findByCodigoIgnoreCase(codigo.trim());
        if (encontrado.isEmpty()) return Optional.empty();

        Cupon c = encontrado.get();
        if (!c.isActivo()) return Optional.empty();

        if (c.getTipo() == TipoCupon.PUBLICO) {
            return Optional.of(c);
        }

        // FIDELIDAD: pertenece al cliente actual + sigue cumpliendo umbral.
        if (cliente == null || c.getCliente() == null) return Optional.empty();
        if (!c.getCliente().getId().equals(cliente.getId())) return Optional.empty();

        ConfiguracionApp cfg = getConfiguracion();
        Double gasto = pedidoRepository.sumGastoByClienteId(cliente.getId());
        if ((gasto != null ? gasto : 0.0) < cfg.getUmbralFidelidad()) {
            return Optional.empty();
        }
        return Optional.of(c);
    }
}
