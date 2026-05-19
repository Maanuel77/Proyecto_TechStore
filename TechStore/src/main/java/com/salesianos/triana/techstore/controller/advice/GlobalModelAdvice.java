package com.salesianos.triana.techstore.controller.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.salesianos.triana.techstore.service.CarritoService;

import lombok.RequiredArgsConstructor;

/**
 * Inyecta la cantidad de items del carrito en todos los modelos de la app,
 * para que el navbar pueda mostrar el badge sin repetir código en cada controlador.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final CarritoService carritoService;

    @ModelAttribute("cantidad_carrito")
    public int cantidadCarrito() {
        return carritoService.getCantidadTotal();
    }
}
