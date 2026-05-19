package com.salesianos.triana.techstore.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Pedido {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String codigo;

    private LocalDate fecha;
    private Double total;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente",
        foreignKey = @ForeignKey(name = "fk_pedido_cliente"))
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    @Builder.Default
    private List<LineaPedido> lineas = new ArrayList<>();

    // Métodos helper de la asociación bidireccional Pedido - LineaPedido
    public void addLineaPedido(LineaPedido linea) {
        linea.setPedido(this);
        lineas.add(linea);
    }

    public void removeLineaPedido(LineaPedido linea) {
        lineas.remove(linea);
        linea.setPedido(null);
    }
}
