package com.salesianos.triana.techstore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LineaPedido {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidad;
    private Double  precioUnitario;
    private Double  subtotal;
    private boolean garantiaExtendida;
    private Double  costeGarantia;

    @ManyToOne(fetch = FetchType.LAZY)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    private Pedido pedido;
}
