package com.salesianos.triana.techstore.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.salesianos.triana.techstore.security.Cliente;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Pedido de un cliente. El código se genera al guardarlo en PedidoService.
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pedido_seq")
    @SequenceGenerator(name = "pedido_seq", sequenceName = "pedido_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true)
    private String codigo;

    private LocalDate fecha;
    private Double total;

    // Snapshot del cupón aplicado en el momento de la compra. NO es una FK al
    // cupón porque el admin puede borrarlo o cambiar el % y queremos conservar
    // la información histórica intacta (igual que se congela el precio en LineaPedido).
    private String cuponCodigo;
    private Double cuponDescuento;  // fracción 0–1 (p.ej. 0.25 = 25%)

    // LAZY: en las vistas que necesitan al cliente, usamos JOIN FETCH en la query.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente",
        foreignKey = @ForeignKey(name = "fk_pedido_cliente"))
    private Cliente cliente;

    // CASCADE.ALL + orphanRemoval: al borrar un pedido se borran sus líneas,
    // y al quitar una línea de la lista también desaparece de BD.
    // LAZY: las vistas con detalle usan JOIN FETCH en la query.
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<LineaPedido> lineas = new ArrayList<>();

    // Subtotal "de catálogo": precio sin descuentos de las líneas + sus garantías.
    // Es la fuente de verdad para mostrar el subtotal en el detalle del pedido
    // (antes el JS lo reconstruía como total / (1-descuento), con riesgo de
    // imprecisión flotante y duplicando lógica que ya vive aquí).
    @Transient
    public double getSubtotalSinDescuento() {
        if (lineas == null) return 0.0;
        return lineas.stream()
                .mapToDouble(l -> (l.getSubtotal() != null ? l.getSubtotal() : 0.0)
                              + (l.getCosteGarantia() != null ? l.getCosteGarantia() : 0.0))
                .sum();
    }

    // Helpers para mantener coherente la relación bidireccional.
    public void addLineaPedido(LineaPedido linea) {
        linea.setPedido(this);
        lineas.add(linea);
    }

    public void removeLineaPedido(LineaPedido linea) {
        lineas.remove(linea);
        linea.setPedido(null);
    }

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
        Pedido that = (Pedido) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hp
                ? hp.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
