package com.salesianos.triana.techstore.cupon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Configuración global del programa de fidelidad. Patrón "single row":
// hay exactamente una fila en BD (id = 1) que el admin edita. No se
// usan secuencias: el id es siempre 1.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "configuracion_app")
public class ConfiguracionApp {

    @Id
    private Long id;

    // Gasto histórico necesario para que un cliente reciba cupón de fidelidad.
    @Column(nullable = false)
    private Double umbralFidelidad;

    // Porcentaje de descuento del cupón de fidelidad (0–1; 0.25 = 25%).
    @Column(nullable = false)
    private Double descuentoFidelidad;
}
