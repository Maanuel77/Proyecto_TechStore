package com.salesianos.triana.techstore.security;

import java.util.ArrayList;
import java.util.List;

import com.salesianos.triana.techstore.model.Pedido;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.ToString;

// Cliente del sistema: hereda de Usuario y añade el historial de pedidos.
// Antes había DOS entidades (User en security + Cliente en model). Ahora son
// una única jerarquía y desaparece el "puente por email".
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue("CLIENTE")
public class Cliente extends Usuario {

    // LAZY: solo se carga el historial cuando se usa, con JOIN FETCH en la query.
    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<Pedido> pedidos = new ArrayList<>();

    @Override
    public UserRole getRole() {
        return UserRole.CLIENTE;
    }

    // Helpers para mantener coherente la relación bidireccional.
    public void addPedido(Pedido pedido) {
        pedido.setCliente(this);
        pedidos.add(pedido);
    }

    public void removePedido(Pedido pedido) {
        pedidos.remove(pedido);
        pedido.setCliente(null);
    }
}
