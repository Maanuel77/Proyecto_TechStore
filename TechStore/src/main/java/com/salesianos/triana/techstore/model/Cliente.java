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

// Cliente del dominio (asociado a los pedidos). Es distinto del User
// de seguridad: ambos se enlazan por email cuando el usuario tramita
// su primer pedido (ver ClienteService.findOrCreateForUser).
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

    // Relación 1:N con Pedido. EAGER para poder mostrar el historial sin
    // problemas de sesión cerrada en las vistas.
    @OneToMany(mappedBy = "cliente", fetch = FetchType.EAGER)
    @ToString.Exclude
    @Builder.Default
    private List<Pedido> pedidos = new ArrayList<>();

    // Helpers para mantener la coherencia de la relación bidireccional.
    public void addPedido(Pedido pedido) {
        pedido.setCliente(this);
        pedidos.add(pedido);
    }

    public void removePedido(Pedido pedido) {
        pedidos.remove(pedido);
        pedido.setCliente(null);
    }
}
