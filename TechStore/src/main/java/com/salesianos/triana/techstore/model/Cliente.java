package com.salesianos.triana.techstore.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class Cliente {

    @Id @GeneratedValue
    private Long id;

    @NotBlank
    private String nombre;

    @Email @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String telefono;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.EAGER)
    @ToString.Exclude
    @Builder.Default
    private List<Pedido> pedidos = new ArrayList<>();

    // Métodos helper de la asociación bidireccional Cliente - Pedido
    public void addPedido(Pedido pedido) {
        pedido.setCliente(this);
        pedidos.add(pedido);
    }

    public void removePedido(Pedido pedido) {
        pedidos.remove(pedido);
        pedido.setCliente(null);
    }
}
