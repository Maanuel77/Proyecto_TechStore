package com.salesianos.triana.techstore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Esta anotación le dice a Spring el código HTTP por defecto si no se captura
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SinStockException extends RuntimeException {
    public SinStockException(String mensaje) {
        super(mensaje);
    }
}
