package com.salesianos.triana.techstore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Cada línea representa un producto dentro de un pedido.
// Guardamos precioUnitario y subtotal en el momento de la compra para que
// los pedidos históricos no cambien si después se actualiza el precio.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class LineaPedido {

    @Id @GeneratedValue
    private Long id;

    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
    private boolean garantiaExtendida;
    private Double costeGarantia;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto",
        foreignKey = @ForeignKey(name = "fk_lineapedido_producto"))
    private Producto producto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pedido",
        foreignKey = @ForeignKey(name = "fk_lineapedido_pedido"))
    private Pedido pedido;
}
