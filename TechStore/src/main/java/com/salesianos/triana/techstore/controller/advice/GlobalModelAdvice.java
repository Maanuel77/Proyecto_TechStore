package com.salesianos.triana.techstore.controller.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.salesianos.triana.techstore.service.CarritoService;

import lombok.RequiredArgsConstructor;

// Inyecta cantidad_carrito en todas las vistas, para que el navbar pueda
// pintar el badge sin que cada controlador tenga que añadirlo a mano.
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final CarritoService carritoService;

    @ModelAttribute("cantidad_carrito")
    public int cantidadCarrito() {
        return carritoService.getCantidadTotal();
    }
}
