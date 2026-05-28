package com.salesianos.triana.techstore.cupon;

import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.salesianos.triana.techstore.security.Cliente;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

// Cupón de descuento. Dos sabores:
//   - PUBLICO: lo crea el admin con un código fijo. Cualquier cliente que
//     lo conozca puede aplicarlo en su carrito.
//   - FIDELIDAD: lo genera el sistema automáticamente con código aleatorio
//     y se asigna a un cliente concreto cuando su gasto ≥ umbral.
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Cupon {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cupon_seq")
    @SequenceGenerator(name = "cupon_seq", sequenceName = "cupon_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo;

    // Porcentaje de descuento entre 0 y 1 (0.25 = 25%).
    @Column(nullable = false)
    private Double descuento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCupon tipo;

    // Solo aplica a los de tipo FIDELIDAD (null para PUBLICO).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente",
        foreignKey = @ForeignKey(name = "fk_cupon_cliente"))
    @ToString.Exclude
    private Cliente cliente;

    @Column(nullable = false)
    private boolean activo;

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
        Cupon that = (Cupon) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hp
                ? hp.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
