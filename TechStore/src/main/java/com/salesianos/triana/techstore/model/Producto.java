package com.salesianos.triana.techstore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Producto del catálogo. Las validaciones (@NotBlank, @Min, @DecimalMin)
// se aplican al guardarlo desde el formulario del admin.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Producto {

    @Id @GeneratedValue
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
}
