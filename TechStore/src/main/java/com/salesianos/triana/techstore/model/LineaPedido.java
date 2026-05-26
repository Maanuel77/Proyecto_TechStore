package com.salesianos.triana.techstore.model;

import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Cada línea representa un producto dentro de un pedido.
// Guardamos precioUnitario y subtotal en el momento de la compra para que
// los pedidos históricos no cambien si después se actualiza el precio.
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class LineaPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "linea_pedido_seq")
    @SequenceGenerator(name = "linea_pedido_seq", sequenceName = "linea_pedido_seq", allocationSize = 1)
    private Long id;

    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
    private boolean garantiaExtendida;
    private Double costeGarantia;

    // LAZY: solo se carga el producto cuando hace falta (con JOIN FETCH en la query).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto",
        foreignKey = @ForeignKey(name = "fk_lineapedido_producto"))
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido",
        foreignKey = @ForeignKey(name = "fk_lineapedido_pedido"))
    @ToString.Exclude
    private Pedido pedido;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy hp
                ? hp.getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hp
                ? hp.getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        LineaPedido that = (LineaPedido) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hp
                ? hp.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
