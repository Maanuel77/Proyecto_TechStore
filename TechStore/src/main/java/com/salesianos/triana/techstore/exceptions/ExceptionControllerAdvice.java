package com.salesianos.triana.techstore.exceptions;

import java.util.NoSuchElementException;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/*Un controlador global de excepciones (@ControllerAdvice)
 * para capturarlo todo de forma centralizada y amigable,
 * sin que la aplicación se rompa ni muestre la típica
 * página fea de "Whitelabel Error Page"
 *
 * Dejo para vosotros el estudio de la anotación @ExceptionHandler
 *
 * */


@ControllerAdvice
public class ExceptionControllerAdvice {

    // 1. Manejo de nuestra excepción personalizada
    @ExceptionHandler(SinStockException.class)
    public String handleSinStock(SinStockException ex, Model model) {
        model.addAttribute("errorTitulo", "Producto sin stock");
        model.addAttribute("errorMensaje", ex.getMessage());
        return "errores/general";
    }

    // 2. Manejo de una excepción propia del API de Java
    //(Ej: Buscar un ID que no existe)
    @ExceptionHandler(NoSuchElementException.class)
    public String handleNotFound(NoSuchElementException ex, Model model) {
        model.addAttribute("errorTitulo", "Elemento No Encontrado");
        model.addAttribute("errorMensaje", "El elemento solicitado no existe en nuestra base de datos.");
        return "errores/general";
    }

    // 3. Manejo de otra excepción del API de Java
    //(Argumento ilegal o inválido en lógica de negocio)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        model.addAttribute("errorTitulo", "Operación Inválida");
        model.addAttribute("errorMensaje", ex.getMessage());
        return "errores/general";
    }
}
