package com.salesianos.triana.techstore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Excepción de negocio que se lanza cuando se intenta añadir al carrito o
// tramitar un pedido sin stock suficiente. La captura el ControllerAdvice.
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SinStockException extends RuntimeException {
    public SinStockException(String mensaje) {
        super(mensaje);
    }
}
