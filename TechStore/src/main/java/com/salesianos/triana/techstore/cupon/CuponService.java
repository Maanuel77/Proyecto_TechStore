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
        // Mismo criterio que crearCuponPublico: descuento estrictamente > 0.
        // Un cupón con 0% no tiene sentido y solo confundiría al cliente.
        if (descuento <= 0 || descuento > 1) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 1 (excl. 0).");
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

    // Cupón de fidelidad disponible AHORA mismo para el cliente (activo).
    // Como los FIDELIDAD son single-use, solo puede haber 1 activo a la vez.
    @Transactional(readOnly = true)
    public Optional<Cupon> getCuponFidelidad(Cliente cliente) {
        if (cliente == null) return Optional.empty();
        return cuponRepository.findByClienteIdAndTipoAndActivoTrue(cliente.getId(), TipoCupon.FIDELIDAD);
    }

    // Genera un cupón de fidelidad nuevo SI el cliente cumple las condiciones:
    //   - No tiene ningún FIDELIDAD activo ahora mismo.
    //   - Su gasto histórico cubre el umbral ACUMULATIVO: el N-ésimo cupón
    //     requiere haber gastado N · umbral. Así, un cliente con cupones
    //     ya consumidos solo recibe otro cuando vuelve a "ganárselo".
    // Devuelve el cupón resultante (el nuevo o el activo existente) o vacío.
    @Transactional
    public Optional<Cupon> asignarFidelidadSiProcede(Cliente cliente) {
        if (cliente == null) return Optional.empty();

        // 1) Si ya tiene uno ACTIVO, no le damos otro hasta que lo consuma.
        Optional<Cupon> activo = getCuponFidelidad(cliente);
        if (activo.isPresent()) return activo;

        // 2) Umbral acumulativo: cuántos FIDELIDAD ha tenido en total (activos o ya
        //    consumidos). Para el siguiente cupón necesita gastar (n+1) · umbral.
        long cuponesHastaAhora = cuponRepository.countByClienteIdAndTipo(
                cliente.getId(), TipoCupon.FIDELIDAD);
        ConfiguracionApp cfg = getConfiguracion();
        double umbralRequerido = (cuponesHastaAhora + 1) * cfg.getUmbralFidelidad();

        Double gasto = pedidoRepository.sumGastoByClienteId(cliente.getId());
        double gastoActual = gasto != null ? gasto : 0.0;
        if (gastoActual < umbralRequerido) {
            return Optional.empty();
        }

        // 3) Generar cupón nuevo con código aleatorio único.
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

    // Marca como consumido el cupón aplicado en un pedido. Lo llama el
    // CarritoController tras tramitar. Los FIDELIDAD se desactivan (single-use);
    // los PUBLICO no se tocan (siguen valiendo para futuras compras de otros).
    @Transactional
    public void consumir(String codigo) {
        if (codigo == null || codigo.isBlank()) return;
        cuponRepository.findByCodigoIgnoreCase(codigo.trim())
                .filter(c -> c.getTipo() == TipoCupon.FIDELIDAD)
                .ifPresent(c -> {
                    c.setActivo(false);
                    cuponRepository.save(c);
                });
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
