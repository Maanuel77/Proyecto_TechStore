package com.salesianos.triana.techstore.model;

import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Producto del catálogo. Las validaciones (@NotBlank, @Min, @DecimalMin)
// se aplican al guardarlo desde el formulario del admin.
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_seq")
    @SequenceGenerator(name = "producto_seq", sequenceName = "producto_seq", allocationSize = 1)
    private Long id;

    @NotBlank(message = "El nombre del producto no puede estar vacío.")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres.")
    private String nombre;

    @NotBlank(message = "La marca es obligatoria.")
    private String marca;

    @NotNull(message = "El precio es obligatorio.")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo.")
    private Double precio;

    @NotNull(message = "El stock es obligatorio.")
    @Min(value = 0, message = "El stock no puede ser negativo.")
    private Integer stock;

    @NotNull(message = "La garantía en meses es obligatoria.")
    @Min(value = 0, message = "La garantía no puede ser negativa.")
    private Integer garantiaMeses;

    @NotNull(message = "Debes indicar si el producto es reacondicionado.")
    private Boolean refurbished;

    private String imagenUrl;

    // equals/hashCode basados en id, compatibles con HibernateProxy.
    // Evita los problemas que generaría @Data con lazy loading.
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
        Producto that = (Producto) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hp
                ? hp.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
